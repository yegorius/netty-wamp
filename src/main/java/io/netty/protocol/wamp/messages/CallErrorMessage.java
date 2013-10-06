package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import io.netty.protocol.wamp.server.CallErrorException;

import java.io.IOException;
import java.io.StringWriter;

public class CallErrorMessage extends WampMessage {
	public String callId;
	public String errorURI;
	public String errorDesc;
	public Object errorDetails;

	public CallErrorMessage() {
		super(MessageType.CALLERROR);
	}

	public CallErrorMessage(String callId, String errorURI, String errorDesc) {
		super(MessageType.CALLERROR);
		this.callId = callId;
		this.errorURI = errorURI;
		this.errorDesc = errorDesc;
	}

	public CallErrorMessage(String callId, CallErrorException cex) {
		super(MessageType.CALLERROR);
		this.callId = callId;
		// TODO: create message from exception
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
			jg.writeString(callId);
			jg.writeString(errorURI);
			jg.writeString(errorDesc);
			if (errorDetails != null) jg.writeObject(errorDetails);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static CallErrorMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		if (jp.nextToken() != JsonToken.START_ARRAY) return null;
		if (jp.nextToken() != JsonToken.VALUE_NUMBER_INT) return null;
		if (jp.getValueAsInt() != MessageType.CALLERROR.getCode()) return null;

		CallErrorMessage cem = new CallErrorMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		cem.callId = jp.getValueAsString();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		cem.errorURI = jp.getValueAsString();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		cem.errorDesc = jp.getValueAsString();

		if (jp.nextToken() != JsonToken.END_ARRAY) cem.errorDetails = jp.readValueAsTree();

		jp.close();
		return cem;
	}
}
