package io.netty.protocol.wamp.server;

import com.fasterxml.jackson.core.TreeNode;
import io.netty.protocol.wamp.messages.EventMessage;
import io.netty.protocol.wamp.messages.MessageMapper;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class Topic {
	private final String topicURI;
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
		if (notify) post(MessageMapper.objectMapper.valueToTree(Collections.singletonMap("partyJoined", session.sessionId)), session);
	}

	public void remove(final Session session) {
		if (!subscribers.remove(session)) return;
		if (notify) post(MessageMapper.objectMapper.valueToTree(Collections.singletonMap("partyLeft", session.sessionId)), session);
	}

	public void post(final TreeNode event, final Session who) {
		EventMessage msg = new EventMessage(topicURI, event);
		synchronized (subscribers) {
			for (Session s : subscribers) s.write(msg);
		}
	}
}
