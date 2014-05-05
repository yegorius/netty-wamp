package io.netty.protocol.wamp.server;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.protocol.wamp.messages.EventMessage;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class Topic {
	public final String topicURI;
	//private final Set<Session> subscribers = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<Session, Boolean>()));
	private final Set<Session> subscribers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<Session, Boolean>()));
	private Boolean notify = false;

	public Topic(final String topicURI) {
		this.topicURI = topicURI;
	}

	public Topic(final String topicURI, final Boolean notify) {
		this.topicURI = topicURI;
		this.notify = notify;
	}

	public void add(Session session) {
		if (!subscribers.add(session)) return;
		if (notify) post(new ObjectNode(JsonNodeFactory.instance).put("partyJoined", session.id), session);
	}

	public void remove(final Session session) {
		if (!subscribers.remove(session)) return;
		if (notify) post(new ObjectNode(JsonNodeFactory.instance).put("partyLeft", session.id), session);
	}

	public void post(final TreeNode event, final Session who) {
		EventMessage msg = new EventMessage(topicURI, event);
		synchronized (subscribers) {
			for (Session s : subscribers) s.write(msg);
		}
	}
}
