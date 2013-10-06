package io.netty.protocol.wamp.server;

import com.fasterxml.jackson.core.TreeNode;

import java.util.List;

public interface RpcHandler {
	public TreeNode call(List<Object> args) throws CallErrorException;
}
