package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.StringWriter;

public class SubscribeMessage extends WampMessage {
	public String topicURI;

	public SubscribeMessage() {
		super(MessageType.SUBSCRIBE);
	}

	public SubscribeMessage(String topicURI) {
		super(MessageType.SUBSCRIBE);
		this.topicURI = topicURI;
	}

	@Override
	public String toJson() {
		String jsonStr = null;
		try (
				StringWriter sw = new StringWriter();
				JsonGenerator jg = MessageMapper.jsonFactory.createGenerator(sw)
		) {
			jg.writeStartArray();
			jg.writeNumber(getMessageCode());
			jg.writeString(topicURI);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static SubscribeMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;
		if (jp.getValueAsInt() != MessageType.SUBSCRIBE.getCode()) return null;

		SubscribeMessage sm = new SubscribeMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		sm.topicURI = jp.getValueAsString();

		jp.close();
		return sm;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof SubscribeMessage)) return false;

		SubscribeMessage msg = (SubscribeMessage) obj;
		return msg.getMessageCode() == this.getMessageCode() &&
				msg.topicURI.equals(this.topicURI);
	}
}
