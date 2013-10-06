package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.StringWriter;

public class UnsubscribeMessage extends WampMessage {
	public String topicURI;

	public UnsubscribeMessage() {
		super(MessageType.UNSUBSCRIBE);
	}

	public UnsubscribeMessage(String topicURI) {
		super(MessageType.UNSUBSCRIBE);
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

	public static UnsubscribeMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;
		if (jp.getValueAsInt() != MessageType.UNSUBSCRIBE.getCode()) return null;

		UnsubscribeMessage usm = new UnsubscribeMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		usm.topicURI = jp.getValueAsString();

		jp.close();
		return usm;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof UnsubscribeMessage)) return false;

		UnsubscribeMessage msg = (UnsubscribeMessage) obj;
		return msg.getMessageCode() == this.getMessageCode() &&
				msg.topicURI.equals(this.topicURI);
	}
}
