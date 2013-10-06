package io.netty.protocol.wamp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.protocol.wamp.messages.MessageMapper;
import io.netty.protocol.wamp.messages.WampMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class WampMessageCodec extends MessageToMessageCodec<TextWebSocketFrame, WampMessage> {
	private static final Logger logger = LoggerFactory.getLogger(WampMessageCodec.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, WampMessage msg, List<Object> out) throws Exception {
		// TODO: validation
		out.add(new TextWebSocketFrame(msg.toJson()));
		logger.debug("WampMessage encoded: {}", msg.toString());
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
		try {
			WampMessage wampMessage;
			wampMessage = MessageMapper.fromJson(msg.text());
			out.add(wampMessage);
			logger.debug("WampMessage decoded: {}", wampMessage.toString());
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: send error message
		}
	}
}
