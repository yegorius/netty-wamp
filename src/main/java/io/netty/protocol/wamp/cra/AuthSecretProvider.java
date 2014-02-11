package io.netty.protocol.wamp.cra;

public interface AuthSecretProvider {
	public String getSecret(final String authKey);
	public boolean keyExists(final String authKey);
}
