package io.netty.protocol.wamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.protocol.wamp.server.WampServer;

public class TestServer {
	public static final WampServer wampServer = new WampServer("MyWampServer");
	//public static final ObjectMapper objectMapper = new ObjectMapper();

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
				.childHandler(new WampServerInitializer(wampServer));

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
		wampServer.addTopic("http://localhost/chat");
		wampServer.registerHandler("http://localhost/sum", new SumHandler());
		wampServer.registerHandler("http://localhost/echo", new EchoHandler());
	}
}
