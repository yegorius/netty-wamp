package io.netty.protocol.wamp.server;

public interface TopicModerator {
	public boolean mayAdd(Session session);

	public boolean mayPost(Object event, Session who);

	public void remove(Session session);
}
