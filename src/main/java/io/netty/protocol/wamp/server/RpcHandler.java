package io.netty.protocol.wamp.server;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public interface RpcHandler {
	public TreeNode call(List<TreeNode> args, HandlerContext ctx) throws CallErrorException;

	public static class HandlerContext {
		public final WampServer wampServer;
		public final ObjectMapper mapper;
		private Session session;

		public HandlerContext(WampServer wampServer, ObjectMapper mapper) {
			this.wampServer = wampServer;
			this.mapper = mapper;
		}

		public Session getSession() {
			return session;
		}

		public void setSession(final Session session) {
			if (this.session != null) return;
			this.session = session;
		}
	}
}
