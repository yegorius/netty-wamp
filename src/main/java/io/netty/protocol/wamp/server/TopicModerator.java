package io.netty.protocol.wamp.server;

import com.fasterxml.jackson.core.TreeNode;

public interface TopicModerator {
	public boolean mayAdd(Session session);

	public boolean mayPost(TreeNode event, Session who);

	public void remove(Session session);
}
