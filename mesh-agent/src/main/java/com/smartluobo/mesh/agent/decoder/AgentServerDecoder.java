package com.smartluobo.mesh.agent.decoder;

import com.smartluobo.mesh.agent.dubbo.Bytes;
import com.smartluobo.mesh.agent.enumer.DecodeResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AgentServerDecoder extends ByteToMessageDecoder {
    private static final int HEADER_LENGTH = 16;
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentServerDecoder.class);

//    @Override
    protected void decodeback(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        int saveReaderIndex = buffer.readerIndex();
        try {
            Object msg = decode(ctx, buffer);
            if(msg == DecodeResult.NEED_MORE_INPUT){
                buffer.readerIndex(saveReaderIndex);
            }else{
                if(saveReaderIndex==buffer.readerIndex()){
                    throw new IOException("Decode without read data.");
                }
                if(msg != null){
                    out.add(msg);
                }
            }
        }catch (Exception e){
            LOGGER.info("provider agent netty server decoder exception");
            throw e;
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        try {
            LOGGER.info("");
            do {
                int saveReaderIndex = buffer.readerIndex();
                Object msg = null;
                LOGGER.info("");
                try {
                    msg = decode(ctx, buffer);
                } catch (Exception e) {
                    throw e;
                }
                if (msg == DecodeResult.NEED_MORE_INPUT) {
                    buffer.readerIndex(saveReaderIndex);
                    break;
                }
                out.add(msg);
            } while (buffer.isReadable());
        } finally {
            if (buffer.isReadable()) {
                buffer.discardReadBytes();
            }
        }
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        int readable = buffer.readableBytes();
        byte[] header = new byte[Math.min(readable, HEADER_LENGTH)];
        buffer.readBytes(header);
        return decode(ctx, buffer, readable, header);
    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer, int readable, byte[] header) throws Exception {

        // check length.
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        // get data length.
        int len = Bytes.bytes2int(header, 12);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        byte[] data = new byte[tt];
        int readerIndex = buffer.readerIndex();
        if(readerIndex < HEADER_LENGTH){
            throw new Exception("header length exception");
        }
        buffer.readerIndex(readerIndex-HEADER_LENGTH);
        int readerIndex1 = buffer.readerIndex();
        buffer.readBytes(data);
        buffer.readerIndex(tt);
        return data;
    }
}
