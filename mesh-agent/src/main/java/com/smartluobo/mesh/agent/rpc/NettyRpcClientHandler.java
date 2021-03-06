package com.smartluobo.mesh.agent.rpc;

import com.smartluobo.mesh.agent.model.AgentRpcRequestHolder;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.model.RpcResponse;
import com.smartluobo.mesh.agent.threadpool.NettyClientThreadPoolUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        NettyClientThreadPoolUtil.nettyRpcClientHandler(channelHandlerContext,response);
//        String requestId = response.getRequestId();
//        RpcFuture future = DubboRpcRequestHolder.get(requestId);
//        if(null != future){
//            AgentRpcRequestHolder.remove(requestId);
//            future.done(response);
//        }
    }
}