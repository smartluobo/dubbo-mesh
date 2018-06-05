package com.smartluobo.mesh.agent.netty;

import com.smartluobo.mesh.agent.rpc.AgentRpcClientInitializer;
import com.smartluobo.mesh.agent.rpc.DubboRpcClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AgentConnecManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private volatile Bootstrap bootstrap;

    private Map<String,Channel> channelRepositry = new ConcurrentHashMap<>();
    private Object lock = new Object();

    public Channel getChannel(String key) throws Exception {

        if (null != channelRepositry.get(key)) {
            return channelRepositry.get(key);
        }

        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channelRepositry.get(key)) {
            synchronized (lock){
                if (null == channelRepositry.get(key)){
                    String[] ipAndPort = key.split(":");
                    Channel channel = bootstrap.connect(ipAndPort[0], Integer.valueOf(ipAndPort[1])).sync().channel();
                    channelRepositry.put(key,channel);
                }
            }
        }
        return channelRepositry.get(key);
    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new AgentRpcClientInitializer());
    }
}
