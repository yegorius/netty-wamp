package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MessageMapper {
	public static final ObjectMapper objectMapper = new ObjectMapper();
	public static final JsonFactory jsonFactory = new MappingJsonFactory(objectMapper);

	public static WampMessage fromJson(final String jsonStr) throws IOException {
		final JsonParser jp = jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) throw new IOException("Not a JSON array");
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) throw new IOException("Wrong message format");

		MessageType messageType = MessageType.fromInteger(jp.getValueAsInt());

		switch (messageType) {
			case WELCOME:
				return WelcomeMessage.fromParser(jp);
			case PREFIX:
				return PrefixMessage.fromParser(jp);
			case CALL:
				return CallMessage.fromParser(jp);
			case CALLRESULT:
				return CallResultMessage.fromParser(jp);
			case CALLERROR:
				return CallErrorMessage.fromParser(jp);
			case SUBSCRIBE:
				return SubscribeMessage.fromParser(jp);
			case UNSUBSCRIBE:
				return UnsubscribeMessage.fromParser(jp);
			case PUBLISH:
				return PublishMessage.fromParser(jp);
			case EVENT:
				return EventMessage.fromParser(jp);
		}
		return null;
	}

	public static boolean validate(JsonParser jp, MessageType type) throws IOException {
		if (jp.nextToken() != JsonToken.START_ARRAY) return false;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return false;
		if (jp.getValueAsInt() != type.getCode()) return false;
		return true;
	}
}
