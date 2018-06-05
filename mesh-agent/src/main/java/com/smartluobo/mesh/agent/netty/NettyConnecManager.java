package com.smartluobo.mesh.agent.netty;

import com.smartluobo.mesh.agent.constant.Constant;
import com.smartluobo.mesh.agent.registry.IpHelper;
import com.smartluobo.mesh.agent.rpc.DubboRpcClientInitializer;
import com.smartluobo.mesh.agent.rpc.NettyRpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NettyConnecManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyConnecManager.class);

    private Bootstrap bootstrap;

    private Channel channel;
    private Object lock = new Object();

    public NettyConnecManager() {
    }

    public Channel getChannel() throws Exception {
        if (null != channel) {
            return channel;
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (lock){
                if (null == channel){
                    LOGGER.info("初始化netty服务端到dubbo provider 端channel通信信道");
                    int port = Integer.valueOf(Constant.DUBBO_PORT);
                    channel = bootstrap.connect(IpHelper.getHostIp(), port).sync().channel();
                }
            }
        }

        return channel;
    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new NettyRpcClientInitializer());
    }
}
