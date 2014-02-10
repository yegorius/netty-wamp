package io.netty.protocol.wamp.cra;

public interface SecretHolder {
	public String getSecret(final String authKey);
	public boolean keyExists(final String authKey);
}
