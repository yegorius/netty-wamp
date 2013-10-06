package io.netty.protocol.wamp;

import com.fasterxml.jackson.core.TreeNode;
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

	public WampServerHandler(WampServer wampServer) {
		this.wampServer = wampServer;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		session = new Session(ctx);
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
			callResult = rpcHandler.call(cm.args);
		} catch (CallErrorException cex) {
			ctx.write(new CallErrorMessage(cm.callId, cex));
			return;
		}

		ctx.write(new CallResultMessage(cm.callId, callResult));
	}

	public void handleSubscribeMessage(SubscribeMessage sm) {
		Topic topic = wampServer.getTopic(sm.topicURI);
		if (topic == null) {
			sm.topicURI = resolveCURI(sm.topicURI);
			topic = wampServer.getTopic(sm.topicURI);
		}
		if (topic == null) {
			// TODO: no such topic
			logger.debug("Topic not found: {}", sm.topicURI);
			return;
		}

		topic.add(session);
	}

	public void handleUnsubscribeMessage(UnsubscribeMessage usm) {
		Topic topic = wampServer.getTopic(usm.topicURI);
		if (topic == null) {
			usm.topicURI = resolveCURI(usm.topicURI);
			topic = wampServer.getTopic(usm.topicURI);
		}
		if (topic == null) {
			// TODO: no such topic
			logger.debug("Topic not found: {}", usm.topicURI);
			return;
		}

		topic.remove(session);
	}

	public void handlePublishMessage(PublishMessage pm) {
		Topic topic = wampServer.getTopic(pm.topicURI);
		if (topic == null) {
			pm.topicURI = resolveCURI(pm.topicURI);
			topic = wampServer.getTopic(pm.topicURI);
		}
		if (topic == null) {
			// TODO: no such topic
			logger.debug("Topic not found: {}", pm.topicURI);
			return;
		}

		topic.post(pm.event);
	}

	private String resolveCURI(final String curi) {
		// TODO
		String[] parts = curi.split(":");
		if (parts[0].equals("http") || parts[0].equals("https")) return curi;
		else return session.prefixes.get(parts[0]) + parts[1];
	}
}
