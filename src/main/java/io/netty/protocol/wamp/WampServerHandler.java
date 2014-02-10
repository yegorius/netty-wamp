package io.netty.protocol.wamp;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.protocol.wamp.messages.*;
import io.netty.protocol.wamp.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// WARNING: This handler is stateful, do not reuse
public class WampServerHandler extends SimpleChannelInboundHandler<WampMessage> {
	private static final Logger logger = LoggerFactory.getLogger(WampServerHandler.class);
	private final WampServer wampServer;
	private Session session;
	private ChannelHandlerContext ctx;
	private RpcHandler.HandlerContext handlerContext;

	public WampServerHandler(WampServer wampServer, ObjectMapper objectMapper) {
		this.wampServer = wampServer;
		handlerContext = new RpcHandler.HandlerContext(wampServer, objectMapper);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		session = new Session(ctx);
		handlerContext.setSession(session);
		ctx.write(new WelcomeMessage(session.sessionId, wampServer.serverIdent));
		//super.channelActive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, final WampMessage message) throws Exception {
		assert ctx == this.ctx;
		switch (message.type) {
			case PREFIX:
				handlePrefixMessage((PrefixMessage) message);
				break;
			case CALL:
				handleCallMessage((CallMessage) message);
				break;
			case SUBSCRIBE:
				handleSubscribeMessage((SubscribeMessage) message);
				break;
			case UNSUBSCRIBE:
				handleUnsubscribeMessage((UnsubscribeMessage) message);
				break;
			case PUBLISH:
				handlePublishMessage((PublishMessage) message);
				break;
			default:
				logger.debug("Not a client-to-server message received: {}", message.toString());
				// not allowed message
				// TODO: send error
		}
	}

	public void handlePrefixMessage(PrefixMessage pm) {
		final String pref = pm.prefix;
		if (pref.startsWith("http") || pref.startsWith("https") || pref.startsWith("ws")) return;
		session.prefixes.put(pm.prefix, pm.URI);
	}

	public void handleCallMessage(CallMessage cm) {
		RpcHandler rpcHandler = wampServer.getHandler(cm.procURI);
		if (rpcHandler == null) rpcHandler = wampServer.getHandler(resolveCURI(cm.procURI));
		if (rpcHandler == null) {
			// TODO: errorURI
			ctx.write(new CallErrorMessage(cm.callId, "errorURI", "No such method"));
			return;
		}

		TreeNode callResult;
		try {
			callResult = rpcHandler.call(cm.args, handlerContext);
		} catch (CallErrorException cex) {
			ctx.write(new CallErrorMessage(cm.callId, cex));
			return;
		}

		ctx.write(new CallResultMessage(cm.callId, callResult));
	}

	public void handleSubscribeMessage(SubscribeMessage sm) {
		Topic topic = getTopicOrFail(sm.topicURI);
		topic.add(session);
	}

	public void handleUnsubscribeMessage(UnsubscribeMessage usm) {
		Topic topic = getTopicOrFail(usm.topicURI);
		topic.remove(session);
	}

	public void handlePublishMessage(PublishMessage pm) {
		Topic topic = getTopicOrFail(pm.topicURI);
		topic.post(pm.event, session);
	}

	private Topic getTopicOrFail(String topicURI) {
		Topic topic = wampServer.getTopic(topicURI);
		if (topic == null) {
			topicURI = resolveCURI(topicURI);
			topic = wampServer.getTopic(topicURI);
		}
		if (topic == null) {
			// TODO: no such topic
			logger.debug("Topic not found: {}", topicURI);
			throw new IllegalArgumentException();
		}
		return topic;
	}

	String resolveCURI(final String curi) {
		if (curi.startsWith("http:") || curi.startsWith("https:") || curi.startsWith("ws:")) return curi;
		String[] parts = curi.split(":");
		if (parts.length < 2) return session.prefixes.get(curi);
		else return session.prefixes.get(parts[0]) + parts[1];
	}
}
