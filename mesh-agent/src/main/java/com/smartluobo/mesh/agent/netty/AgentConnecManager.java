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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AgentConnecManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(100);

    private volatile Bootstrap bootstrap;
    private static AtomicInteger smallIndex = new AtomicInteger(0);
    private static AtomicInteger mediumIndex = new AtomicInteger(0);
    private static AtomicInteger largeIndex = new AtomicInteger(0);

    private Map<String,List<Channel>> channelRepositry = new ConcurrentHashMap<>();
    private List<Channel> smallChannels = new ArrayList<>(50);
    private List<Channel> mediumChannels = new ArrayList<>(100);
    private List<Channel> largeChannels = new ArrayList<>(150);
    private Object lock = new Object();

    public Channel getChannel(String key) throws Exception {
        String[] ipAndPort = key.split(":");
        Integer weight = Integer.valueOf(ipAndPort[2]);
        Channel ch = getChannel(key, weight);
        if(ch != null){
            return ch;
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
                    int channelSize = weight * 50;
                    for(int i = 0;i < channelSize;i++){
                        Channel channel = bootstrap.connect(ipAndPort[0], Integer.valueOf(ipAndPort[1])).sync().channel();
                        if(weight == 1){
                            smallChannels.add(channel);
                        }else if(weight == 2){
                            mediumChannels.add(channel);
                        }else{
                            largeChannels.add(channel);
                        }

                    }
                    if(weight == 1){
                        channelRepositry.put(key,smallChannels);
                    }else if(weight == 2){
                        channelRepositry.put(key,mediumChannels);
                    }else {
                        channelRepositry.put(key,largeChannels);
                    }
                }
            }
        }
        return getChannel(key,weight);
    }

    public Channel getChannel(String key,Integer weight){
        if (null != channelRepositry.get(key)) {
            if(weight == 1){
                return channelRepositry.get(key).get(smallIndex.getAndIncrement()%50);
            }else if(weight == 2){
                return channelRepositry.get(key).get(mediumIndex.getAndIncrement()%100);
            }else {
                return channelRepositry.get(key).get(largeIndex.getAndIncrement()%150);
            }
        }
        return null;
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
