package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import io.netty.protocol.wamp.server.CallErrorException;

import java.io.IOException;
import java.io.StringWriter;

public class CallErrorMessage extends WampMessage {
	public static final String DEFAULT_ERROR_URI = "http://wamp.ws/error#generic";
	public String callId;
	// TODO: errorURI resolver
	public String errorURI;
	public String errorDesc;
	// TODO: errorDetails to JsonNode
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
		this.errorURI = cex.getErrorURI();
		this.errorDesc = cex.getMessage();
		this.errorDetails = cex.errorDetails;
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
		boolean valid = MessageMapper.validate(jp, MessageType.CALLERROR);
		if (valid) return fromParser(jp);
		else throw new IOException("Wrong format");
	}

	public static CallErrorMessage fromParser(final JsonParser jp) throws IOException {
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
