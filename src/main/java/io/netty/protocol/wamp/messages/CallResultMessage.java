package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

import java.io.IOException;
import java.io.StringWriter;

public class CallResultMessage extends WampMessage {
	public String callId;
	public TreeNode result;

	public CallResultMessage() {
		super(MessageType.CALLRESULT);
	}

	public CallResultMessage(String callId, TreeNode result) {
		super(MessageType.CALLRESULT);
		this.callId = callId;
		this.result = result;
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
			jg.writeTree(result);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static CallResultMessage fromJson(final String jsonStr) throws IOException {
		try (JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr)) {
			boolean valid = MessageMapper.validate(jp, MessageType.CALLRESULT);
			if (valid) return fromParser(jp);
			else throw new IOException("Wrong format");
		}
	}

	public static CallResultMessage fromParser(final JsonParser jp) throws IOException {
		CallResultMessage crm = new CallResultMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		crm.callId = jp.getValueAsString();

		jp.nextToken();
		crm.result = jp.readValueAsTree();

		return crm;
	}
}
