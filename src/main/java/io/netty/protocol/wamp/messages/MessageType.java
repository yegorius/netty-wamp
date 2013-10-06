package io.netty.protocol.wamp.messages;

public enum MessageType {
	WELCOME(0),
	PREFIX(1),
	CALL(2),
	CALLRESULT(3),
	CALLERROR(4),
	SUBSCRIBE(5),
	UNSUBSCRIBE(6),
	PUBLISH(7),
	EVENT(8);

	private final int value;

	private MessageType(int value) {
		this.value = value;
	}

	public int getCode() {
		return value;
	}

	public static MessageType fromInteger(int value) {
		switch(value) {
		case 0:
			return WELCOME;
		case 1:
			return PREFIX;
		case 2:
			return CALL;
		case 3:
			return CALLRESULT;
		case 4:
			return CALLERROR;
		case 5:
			return SUBSCRIBE;
		case 6:
			return UNSUBSCRIBE;
		case 7:
			return PUBLISH;
		case 8:
			return EVENT;
		}
		return null;
	}
}
