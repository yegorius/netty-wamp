package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.StringWriter;

public class WelcomeMessage extends WampMessage {
	public static final int PROTOCOL_VERSION = 1;

	public String sessionId;
	public int protocolVersion = PROTOCOL_VERSION;
	public String serverIdent;

	public WelcomeMessage() {
		super(MessageType.WELCOME);
	}

	public WelcomeMessage(String sessionId, String serverIdent) {
		super(MessageType.WELCOME);
		this.sessionId = sessionId;
		this.serverIdent = serverIdent;
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
			jg.writeString(sessionId);
			jg.writeNumber(protocolVersion);
			jg.writeString(serverIdent);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static WelcomeMessage fromJson(final String jsonStr) throws IOException {
		try (JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr)) {
			boolean valid = MessageMapper.validate(jp, MessageType.WELCOME);
			if (valid) return fromParser(jp);
			else throw new IOException("Wrong format");
		}
	}

	public static WelcomeMessage fromParser(final JsonParser jp) throws IOException {
		WelcomeMessage wm = new WelcomeMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) throw new IOException();
		wm.sessionId = jp.getValueAsString();

		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) throw new IOException();
		wm.protocolVersion = jp.getValueAsInt();

		if (jp.nextToken() != JsonToken.VALUE_STRING) throw new IOException();
		wm.serverIdent = jp.getValueAsString();

		return wm;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof WelcomeMessage)) return false;

		WelcomeMessage msg = (WelcomeMessage) obj;
		return msg.getMessageCode() == this.getMessageCode() &&
				msg.sessionId.equals(this.sessionId) &&
				msg.protocolVersion == this.protocolVersion &&
				msg.serverIdent.equals(this.serverIdent);
	}
}
