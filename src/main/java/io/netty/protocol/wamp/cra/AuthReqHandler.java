package io.netty.protocol.wamp.cra;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.netty.protocol.wamp.server.CallErrorException;
import io.netty.protocol.wamp.server.RpcHandler;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AuthReqHandler implements RpcHandler {
	public static final String AUTHREQ_URI = "http://api.wamp.ws/procedure#authreq";

	@Override
	public TreeNode call(final List<TreeNode> args, final HandlerContext ctx) throws CallErrorException {
		if (ctx.getSession().isAuthRequested()) throw new CallErrorException("Already authenticated");

		final String authKey = ((TextNode) args.get(0)).textValue();
		if (!ctx.wampServer.authSecretProvider.keyExists(authKey)) throw new CallErrorException("Authentication key does not exist");

		String extra = null;
		if (args.get(1) != null && !(args.get(1) instanceof NullNode)) {
			extra = ((TextNode) args.get(1)).textValue();
		}

		try {
			final String challenge = HmacSHA256.generate(ctx.getSession().id + System.currentTimeMillis(), authKey);
			ctx.getSession().authKey = authKey;
			ctx.getSession().challenge = challenge;
			return new TextNode(challenge);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CallErrorException("Internal server error");
		}
	}
}
