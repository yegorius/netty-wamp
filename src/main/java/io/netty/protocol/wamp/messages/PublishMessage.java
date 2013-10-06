package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class PublishMessage extends WampMessage {
	public String topicURI;
	public Object event;
	public Boolean excludeMe;
	public List<String> exclude;
	public List<String> eligible;

	public PublishMessage() {
		super(MessageType.PUBLISH);
	}

	public PublishMessage(String topicURI, Object event) {
		super(MessageType.PUBLISH);
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
			if (excludeMe != null && excludeMe) {
				jg.writeBoolean(excludeMe);
			} else if (exclude != null || eligible != null) {
				if (exclude != null) {
					jg.writeObject(exclude);
				} else {
					jg.writeStartArray();
					jg.writeEndArray();
				}
				if (eligible != null) {
					jg.writeObject(eligible);
				} else {
					jg.writeStartArray();
					jg.writeEndArray();
				}
			}
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static PublishMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;
		if (jp.getValueAsInt() != MessageType.PUBLISH.getCode()) return null;

		PublishMessage pm = new PublishMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		pm.topicURI = jp.getValueAsString();

		jp.nextToken();
		pm.event = jp.readValueAsTree();

		if (jp.nextToken() != JsonToken.END_ARRAY) {
			if (jp.getCurrentToken() == JsonToken.VALUE_TRUE || jp.getCurrentToken() == JsonToken.VALUE_FALSE) {
				pm.excludeMe = jp.getValueAsBoolean();

				if (jp.nextToken() != JsonToken.END_ARRAY) {
					// Wrong message format, excludeMe should not be followed by any value
					return null;
				}
			} else {
				TypeReference<List<String>> typRef = new TypeReference<List<String>>() {};

				if (jp.getCurrentToken() != JsonToken.START_ARRAY) return null;
				pm.exclude = jp.readValueAs(typRef);

				if (jp.nextToken() != JsonToken.START_ARRAY) return null;
				pm.eligible = jp.readValueAs(typRef);
			}
		}

		jp.close();
		return pm;
	}
}
