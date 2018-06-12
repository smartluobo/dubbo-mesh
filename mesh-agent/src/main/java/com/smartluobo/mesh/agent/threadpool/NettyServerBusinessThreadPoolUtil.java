package com.smartluobo.mesh.agent.threadpool;

import com.smartluobo.mesh.agent.dubbo.Bytes;
import com.smartluobo.mesh.agent.rpc.NettyRpcClient;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyServerBusinessThreadPoolUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerBusinessThreadPoolUtil.class);
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(100,500,1000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(100000));


    public static void doBusiness(ChannelHandlerContext ctx, byte[] msg, NettyRpcClient nettyRpcClient) {

        //异步线程池处理
        EXECUTOR.submit(() -> {
            Thread.currentThread().setName("business-thread"+Thread.currentThread().getId());
            byte[] requestIdBytes = Arrays.copyOfRange(msg,4,12);//获取requestId
            long requestId = Bytes.bytes2long(requestIdBytes,0);
            LOGGER.info("netty server send msg to provider requestId"+requestId);
            try {
                long startTime = System.currentTimeMillis();
                Object result = nettyRpcClient.invoke(msg);
                ctx.writeAndFlush(result);
                long endTime = System.currentTimeMillis();
                LOGGER.info("current requestId : "+requestId+" wait time : "+(endTime-startTime)+"ms");
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("current requestId"+requestId+"happen exception");
            }
        });
    }
}
