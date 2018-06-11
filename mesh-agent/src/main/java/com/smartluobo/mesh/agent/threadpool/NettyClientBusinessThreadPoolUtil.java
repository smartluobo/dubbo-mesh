package com.smartluobo.mesh.agent.threadpool;

import com.smartluobo.mesh.agent.constant.Constant;
import com.smartluobo.mesh.agent.dubbo.Bytes;
import com.smartluobo.mesh.agent.enumer.DecodeResult;
import com.smartluobo.mesh.agent.model.RpcResponse;
import com.smartluobo.mesh.agent.protocol.DubboProtocolRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyClientBusinessThreadPoolUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientBusinessThreadPoolUtil.class);

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(100,500,1000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(100000));

    public static void encode(ChannelHandlerContext ctx, DubboProtocolRequest req, ByteBuf buffer) {

        //异步线程池处理
        EXECUTOR.submit(() -> {
            Thread.currentThread().setName("business-thread"+Thread.currentThread().getId());
            int savedWriteIndex = buffer.writerIndex();//获取buffer可以写的位置
            buffer.writerIndex(savedWriteIndex);//标记buffer从什么位置开始写入数据
            buffer.writeBytes(req.getHeader().array()); // 写入header部分
            buffer.writerIndex(savedWriteIndex+ Constant.HEADER_LENGTH);//标记body写入buffer的位置为开始获取buffer的写入位置+header的长度定长16
            //心跳包发送不需要发送body数据
            LOGGER.info("requestId: "+req.getRequestId());
            byte[] body = req.getRpcBody().getBytes();
            buffer.writeBytes(body);//写入body数据
            buffer.writerIndex(savedWriteIndex+Constant.HEADER_LENGTH+body.length);//设置buffer的可以写入位置
        });
    }

    public static void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {

        //异步线程池处理
        EXECUTOR.submit(() -> {
            Thread.currentThread().setName("business-thread"+Thread.currentThread().getId());
            try {
                do {
                    int savedReaderIndex = byteBuf.readerIndex();
                    LOGGER.info("byteBuf readable position "+savedReaderIndex);
                    Object msg = null;
                    try {
                        msg = decode2(byteBuf);
                    } catch (Exception e) {
                        throw e;
                    }
                    if (msg == DecodeResult.NEED_MORE_INPUT) {
                        byteBuf.readerIndex(savedReaderIndex);
                        break;
                    }
                    out.add(msg);
                } while (byteBuf.isReadable());
            } finally {
                if (byteBuf.isReadable()) {
                    byteBuf.discardReadBytes();
                }
            }
        });
    }

    private static Object decode2(ByteBuf byteBuf){

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < Constant.HEADER_LENGTH) {
            LOGGER.info("byteBuf readable limit < request header length");
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[Constant.HEADER_LENGTH];
        byteBuf.readBytes(header);
        byte[] dataLen = Arrays.copyOfRange(header,12,16);
        int len = Bytes.bytes2int(dataLen);
        int tt = len + Constant.HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.readerIndex(savedReaderIndex);
        byte[] data = new byte[tt];
        byteBuf.readBytes(data);

        // HEADER_LENGTH + 1，忽略header & Response value type的读取，直接读取实际Return value
        // dubbo返回的body中，前后各有一个换行，去掉
        byte[] subArray = Arrays.copyOfRange(data,Constant.HEADER_LENGTH + 3, data.length -2 );

        String s = new String(subArray);
        LOGGER.info("provider return result :"+ s );

        byte[] requestIdBytes = Arrays.copyOfRange(data,4,12);
        long requestId = Bytes.bytes2long(requestIdBytes,0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(subArray);
        return response;
    }

}
