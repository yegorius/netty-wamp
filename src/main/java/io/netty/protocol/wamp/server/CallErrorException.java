package io.netty.protocol.wamp.server;

import com.fasterxml.jackson.core.TreeNode;
import io.netty.protocol.wamp.messages.CallErrorMessage;

import java.io.IOException;

public class CallErrorException extends IOException {
	private String errorURI = CallErrorMessage.DEFAULT_ERROR_URI;
	public TreeNode errorDetails = null;

	public CallErrorException() {
		super();
	}

	public CallErrorException(final String message) {
		super(message);
	}

	public CallErrorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	private CallErrorException(final Throwable cause) {
	}

	public CallErrorException(final String errorURI, final String message) {
		super(message);
		this.errorURI = errorURI;
	}

	public CallErrorException(final String errorURI, final String message, final Throwable cause) {
		super(message, cause);
		this.errorURI = errorURI;
	}

	public String getErrorURI() {
		return errorURI;
	}
}
