package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.StringWriter;

public class EventMessage extends WampMessage {
	public String topicURI;
	public Object event;

	public EventMessage() {
		super(MessageType.EVENT);
	}

	public EventMessage(String topicURI, Object event) {
		super(MessageType.EVENT);
		this.topicURI = topicURI;
		this.event = event;
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
			jg.writeObject(event);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static EventMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;
		if (jp.getValueAsInt() != MessageType.EVENT.getCode()) return null;

		EventMessage em = new EventMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		em.topicURI = jp.getValueAsString();

		jp.nextToken();
		em.event = jp.readValueAsTree();

		jp.close();
		return em;
	}
}