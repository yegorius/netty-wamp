package io.netty.protocol.wamp.server;

public class ModeratedTopic extends Topic {
	private final TopicModerator moderator;

	public ModeratedTopic(final String topicURI, final TopicModerator moderator) {
		super(topicURI);
		this.moderator = moderator;
	}

	public ModeratedTopic(final String topicURI, final TopicModerator moderator, final Boolean notify) {
		super(topicURI, notify);
		this.moderator = moderator;
	}

	@Override
	public void add(final Session session) {
		if (moderator.mayAdd(session)) super.add(session);
	}

	@Override
	public void remove(final Session session) {
		moderator.remove(session);
		super.remove(session);
	}

	@Override
	public void post(final Object event, final Session who) {
		if (moderator.mayPost(event, who)) super.post(event, who);
	}
}
