package com.smartluobo.mesh.agent.rpc;

import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DubboRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
        String requestId = response.getRequestId();
        RpcFuture future = DubboRpcRequestHolder.get(requestId);
        if(null != future){
            DubboRpcRequestHolder.remove(requestId);
            future.done(response);
        }
    }
}