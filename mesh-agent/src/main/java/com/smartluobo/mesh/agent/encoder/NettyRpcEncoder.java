package com.smartluobo.mesh.agent.encoder;

import com.alibaba.fastjson.JSONObject;
import com.smartluobo.mesh.agent.protocol.DubboProtocolRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class NettyRpcEncoder extends MessageToByteEncoder<byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRpcEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf buffer) throws Exception {
        int savedWriteIndex = buffer.writerIndex();//获取buffer可以写的位置
        buffer.writerIndex(savedWriteIndex);//标记buffer从什么位置开始写入数据
        buffer.writeBytes(msg); // 写入数据
        LOGGER.info("request: "+msg);
        buffer.writerIndex(savedWriteIndex+msg.length);//设置buffer的可以写入位置
    }
}
