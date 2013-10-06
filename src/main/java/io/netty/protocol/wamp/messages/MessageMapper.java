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
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;

		MessageType messageType = MessageType.fromInteger(jp.getValueAsInt());
		jp.close();
		// TODO: reuse jp

		switch (messageType) {
			case WELCOME:
				return WelcomeMessage.fromJson(jsonStr);
			case PREFIX:
				return PrefixMessage.fromJson(jsonStr);
			case CALL:
				return CallMessage.fromJson(jsonStr);
			case CALLRESULT:
				return CallResultMessage.fromJson(jsonStr);
			case CALLERROR:
				return CallErrorMessage.fromJson(jsonStr);
			case SUBSCRIBE:
				return SubscribeMessage.fromJson(jsonStr);
			case UNSUBSCRIBE:
				return UnsubscribeMessage.fromJson(jsonStr);
			case PUBLISH:
				return PublishMessage.fromJson(jsonStr);
			case EVENT:
				return EventMessage.fromJson(jsonStr);
		}
		return null;
	}
}
