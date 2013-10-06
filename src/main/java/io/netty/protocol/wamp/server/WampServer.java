package io.netty.protocol.wamp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WampServer {
	final Logger logger = LoggerFactory.getLogger(WampServer.class);

	public final String serverIdent;
	private ConcurrentMap<String, Topic> topics = new ConcurrentHashMap<>();
	private ConcurrentMap<String, RpcHandler> rpcHandlers = new ConcurrentHashMap<>();

	public WampServer(String serverIdent) {
		this.serverIdent = serverIdent;
	}

	public void registerHandler(String procURI, RpcHandler rpcHandler) {
		rpcHandlers.put(procURI, rpcHandler);
	}

	public RpcHandler getHandler(String procURI) {
		return rpcHandlers.get(procURI);
	}

	public Topic getTopic(String topicURI) {
		return topics.get(topicURI);
	}

	public boolean addTopic(String topicURI) {
		// TODO
		return topics.put(topicURI, new Topic(topicURI)) == null;
	}
}
