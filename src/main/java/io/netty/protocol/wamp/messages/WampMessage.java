package io.netty.protocol.wamp.messages;


public abstract class WampMessage {
	public final MessageType type;

	WampMessage(MessageType type) {
		this.type = type;
	}

	public int getMessageCode() {
		return type.getCode();
	}

	public abstract String toJson();

	@Override
	public String toString() {
		return toJson();
	}
}
