package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.StringWriter;

public class PrefixMessage extends WampMessage {
	public String prefix;
	public String URI;

	public PrefixMessage() {
		super(MessageType.PREFIX);
	}

	public PrefixMessage(String prefix, String URI) {
		super(MessageType.PREFIX);
		this.prefix = prefix;
		this.URI = URI;
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
			jg.writeString(prefix);
			jg.writeString(URI);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static PrefixMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;
		if (jp.getValueAsInt() != MessageType.PREFIX.getCode()) return null;

		PrefixMessage pm = new PrefixMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		pm.prefix = jp.getValueAsString();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		pm.URI = jp.getValueAsString();

		jp.close();
		return pm;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof PrefixMessage)) return false;

		PrefixMessage msg = (PrefixMessage) obj;
		return msg.getMessageCode() == this.getMessageCode() &&
				msg.prefix.equals(this.prefix) &&
				msg.URI.equals(this.URI);
	}
}
