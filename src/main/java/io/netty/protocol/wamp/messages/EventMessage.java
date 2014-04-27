package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

import java.io.IOException;
import java.io.StringWriter;

public class EventMessage extends WampMessage {
	public String topicURI;
	public TreeNode event;

	public EventMessage() {
		super(MessageType.EVENT);
	}

	public EventMessage(String topicURI, TreeNode event) {
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
			jg.writeTree(event);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static EventMessage fromJson(final String jsonStr) throws IOException {
		try (JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr)) {
			boolean valid = MessageMapper.validate(jp, MessageType.EVENT);
			if (valid) return fromParser(jp);
			else throw new IOException("Wrong format");
		}
	}

	public static EventMessage fromParser(final JsonParser jp) throws IOException {
		EventMessage em = new EventMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		em.topicURI = jp.getValueAsString();

		jp.nextToken();
		em.event = jp.readValueAsTree();

		return em;
	}
}
