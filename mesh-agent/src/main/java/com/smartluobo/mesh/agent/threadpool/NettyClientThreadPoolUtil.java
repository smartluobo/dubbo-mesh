package com.smartluobo.mesh.agent.threadpool;

import com.smartluobo.mesh.agent.model.AgentRpcRequestHolder;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyClientThreadPoolUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientThreadPoolUtil.class);
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(100,500,1000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(100000));

    public static void dubboRpcClientHandler(ChannelHandlerContext channelHandlerContext, RpcResponse response){
        EXECUTOR.submit(() -> {
            Thread.currentThread().setName("business-thread"+Thread.currentThread().getId());
            String requestId = response.getRequestId();
            RpcFuture future = DubboRpcRequestHolder.get(requestId);
            if(null != future){
                DubboRpcRequestHolder.remove(requestId);
                future.done(response);
            }
        });
    }

    public static void agentRpcClientHandler(ChannelHandlerContext channelHandlerContext, RpcResponse response){
        EXECUTOR.submit(() -> {
            Thread.currentThread().setName("business-thread"+Thread.currentThread().getId());
            String requestId = response.getRequestId();
            RpcFuture future = AgentRpcRequestHolder.get(requestId);
            if(null != future){
                AgentRpcRequestHolder.remove(requestId);
                future.done(response);
            }
        });
    }


    public static void nettyRpcClientHandler(ChannelHandlerContext channelHandlerContext, RpcResponse response){
        EXECUTOR.submit(() -> {
            String requestId = response.getRequestId();
            RpcFuture future = DubboRpcRequestHolder.get(requestId);
            if(null != future){
                AgentRpcRequestHolder.remove(requestId);
                future.done(response);
            }
        });
    }
}
