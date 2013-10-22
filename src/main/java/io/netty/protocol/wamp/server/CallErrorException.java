package io.netty.protocol.wamp.server;

import java.io.IOException;

public class CallErrorException extends IOException {
	public CallErrorException() {
		super();
	}

	public CallErrorException(String message) {
		super(message);
	}

	public CallErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public CallErrorException(Throwable cause) {
		super(cause);
	}
}
