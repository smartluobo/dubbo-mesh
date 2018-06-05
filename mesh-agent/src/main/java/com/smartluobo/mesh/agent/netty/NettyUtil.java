package com.smartluobo.mesh.agent.netty;

import com.smartluobo.mesh.agent.decoder.AgentServerDecoder;
import com.smartluobo.mesh.agent.encoder.AgentServerEncoder;
import com.smartluobo.mesh.agent.rpc.DubboRpcClient;
import com.smartluobo.mesh.agent.rpc.NettyRpcClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NettyUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyUtil.class);
    @Resource
    private NettyRpcClient nettyRpcClient;

    public void startNettyServer(String port) {
        final NettyRpcClient finalNettyRpcClient = nettyRpcClient;
        LOGGER.info("provider agent start netty service port: "+port);
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new AgentServerDecoder());
                            p.addLast(new AgentServerEncoder());
                            p.addLast(new IdleStateHandler(6, 0, 0));
                            p.addLast(new AgentNettyServerInHandle(finalNettyRpcClient));
                        }
                    });
            // Start the server.
            ChannelFuture f = b.bind(Integer.valueOf(port)).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("netty exception", e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
