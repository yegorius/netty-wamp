package io.netty.protocol.wamp.server;

import io.netty.protocol.wamp.messages.EventMessage;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class Topic {
	private final String topicURI;
	//private final Set<Session> subscribers = Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<Session, Boolean>()));
	private final Set<Session> subscribers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<Session, Boolean>()));

	public Topic(String topicURI) {
		this.topicURI = topicURI;
	}

	public boolean add(Session session) {
		return subscribers.add(session);
	}

	public boolean remove(Session session) {
		return subscribers.remove(session);
	}

	public void post(final Object event) {
		EventMessage msg = new EventMessage(topicURI, event);
		synchronized (subscribers) {
			for (Session s : subscribers) s.write(msg);
		}
	}
}
