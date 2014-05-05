package io.netty.protocol.wamp.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.protocol.wamp.messages.EventMessage;

import java.util.HashMap;
import java.util.Random;

public class Session {
	private static final int ID_LENGTH = 16;

	public final String id;
	private final ChannelHandlerContext ctx;
	public final HashMap<String, String> prefixes = new HashMap<>();
	public String authKey;
	public String challenge;
	public String signature; // HmacSHA256(challenge, secret)

	public Session(ChannelHandlerContext ctx) {
		this.id = randomString(ID_LENGTH);
		this.ctx = ctx;
	}

	public ChannelFuture write(EventMessage msg) {
		return ctx.writeAndFlush(msg);
	}

	public boolean isAuthRequested() {
		return authKey != null;
	}

	public boolean isAuthenticated() {
		return signature != null;
	}

	private final static String alphaNum = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private final static Random rnd = new Random();

	private static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(alphaNum.charAt(rnd.nextInt(alphaNum.length())));
		return sb.toString();
	}
}
