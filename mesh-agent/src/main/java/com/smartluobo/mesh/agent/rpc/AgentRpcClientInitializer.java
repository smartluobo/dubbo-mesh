package com.smartluobo.mesh.agent.rpc;

import com.smartluobo.mesh.agent.decoder.AgentRpcDecoder;
import com.smartluobo.mesh.agent.encoder.AgentRpcEncoder;
import com.smartluobo.mesh.agent.heart.AgentClientHeartbeatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class AgentRpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new AgentRpcEncoder());
        pipeline.addLast(new AgentRpcDecoder());
        pipeline.addLast(new IdleStateHandler(0, 5, 0));
        pipeline.addLast(new AgentRpcClientHandler());
//        pipeline.addLast(new AgentClientHeartbeatHandler());
    }
}
