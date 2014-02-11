package io.netty.protocol.wamp.cra;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.netty.protocol.wamp.server.CallErrorException;
import io.netty.protocol.wamp.server.RpcHandler;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AuthHandler implements RpcHandler {
	public static final String AUTH_URI = "http://api.wamp.ws/procedure#auth";

	@Override
	public TreeNode call(final List<TreeNode> args, final HandlerContext ctx) throws CallErrorException {
		if (!ctx.getSession().isAuthRequested()) throw new CallErrorException("No authentication previously requested");
		if (ctx.wampServer.authSecretProvider == null) throw new CallErrorException("Internal server error");

		final String clientSignature = ((TextNode) args.get(0)).textValue();

		final String correctSignature;
		try {
			final String secret = ctx.wampServer.authSecretProvider.getSecret(ctx.getSession().authKey);
			if (secret == null || secret.isEmpty()) throw new CallErrorException("Authentication secret does not exist");
			correctSignature = HmacSHA256.generate(ctx.getSession().challenge, secret);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CallErrorException("Internal sever error");
		}

		if (clientSignature.equals(correctSignature)) {
			ctx.getSession().signature = clientSignature;
			return ctx.mapper.createObjectNode();
		} else {
			ctx.getSession().authKey = null;
			ctx.getSession().challenge = null;
			ctx.getSession().signature = null;
			throw new CallErrorException("Signature for authentication request is invalid");
		}
	}
}
