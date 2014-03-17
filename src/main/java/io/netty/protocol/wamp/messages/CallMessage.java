package io.netty.protocol.wamp.messages;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class CallMessage extends WampMessage {
	public String callId;
	public String procURI;
	public List<TreeNode> args;

	public CallMessage() {
		super(MessageType.CALL);
	}

	public CallMessage(String callId, String procURI) {
		super(MessageType.CALL);
		this.callId = callId;
		this.procURI = procURI;
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
			jg.writeString(procURI);
			if (args != null && !args.isEmpty())
				for (TreeNode tn : args) jg.writeTree(tn);
			jg.writeEndArray();
			jg.close();
			jsonStr = sw.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static CallMessage fromJson(final String jsonStr) throws IOException {
		JsonParser jp = MessageMapper.jsonFactory.createParser(jsonStr);
		boolean valid = MessageMapper.validate(jp, MessageType.CALL);
		if (valid) return fromParser(jp);
		else throw new IOException("Wrong format");
	}

	public static CallMessage fromParser(final JsonParser jp) throws IOException {
		CallMessage cm = new CallMessage();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		cm.callId = jp.getValueAsString();

		if (jp.nextToken() != JsonToken.VALUE_STRING) return null;
		cm.procURI = jp.getValueAsString();

		cm.args = new ArrayList<>();
		while (jp.nextToken() != JsonToken.END_ARRAY) cm.args.add(jp.readValueAsTree());

		jp.close();
		return cm;
	}
}
