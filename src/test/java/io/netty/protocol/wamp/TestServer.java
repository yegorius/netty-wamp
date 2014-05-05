package io.netty.protocol.wamp;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.protocol.wamp.server.Session;
import io.netty.protocol.wamp.server.TopicModerator;
import io.netty.protocol.wamp.server.WampServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {
	public static final WampServer wampServer = new WampServer("MyWampServer");
	public static final ObjectMapper objectMapper = new ObjectMapper();

	private final int port;

    public TestServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bs = new ServerBootstrap();
            bs.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new WampServerInitializer(wampServer, objectMapper));

            Channel ch = bs.bind(port).sync().channel();
            System.out.println("Server started at port " + port + '.');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) port = Integer.parseInt(args[0]);
		configure();
        new TestServer(port).run();
    }

	private static void configure() {
		wampServer.addTopic("http://localhost/chat", new LoggingModerator());
		wampServer.registerHandler("http://localhost/sum", new SumHandler());
		wampServer.registerHandler("http://localhost/echo", new EchoHandler());
	}

	private static class LoggingModerator implements TopicModerator {
		private Logger log = LoggerFactory.getLogger(LoggingModerator.class);

		@Override
		public boolean mayAdd(final Session session) {
			log.info("mayAdd: " + session.id);
			return true;
		}

		@Override
		public boolean mayPost(final TreeNode event, final Session who) {
			log.info("mayPost: " + who.id);
			return true;
		}

		@Override
		public void remove(final Session session) {
			log.info("remove: " + session.id);
		}
	}
}
