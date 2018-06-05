package com.smartluobo.mesh.agent.rpc;

import com.smartluobo.mesh.agent.model.AgentRpcRequestHolder;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.model.RpcResponse;
import com.smartluobo.mesh.agent.protocol.DubboProtocolRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.Agent;

import java.nio.ByteBuffer;

public class AgentRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentRpcClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        String requestId = response.getRequestId();
        RpcFuture future = AgentRpcRequestHolder.get(requestId);
        if(null != future){
            AgentRpcRequestHolder.remove(requestId);
            future.done(response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
            sendHeartbeatPacket(ctx);
        }
    }

    public void sendHeartbeatPacket(ChannelHandlerContext ctx){
        LOGGER.info("agentRpcClient send heart ..................");
        DubboProtocolRequest heartInstance = DubboProtocolRequest.getHeartInstance();
        ctx.writeAndFlush(heartInstance);
    }
}