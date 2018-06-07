package com.smartluobo.mesh.agent.decoder;

import com.smartluobo.mesh.agent.dubbo.Bytes;
import com.smartluobo.mesh.agent.enumer.DecodeResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AgentServerDecoder extends ByteToMessageDecoder {
    private static final int HEADER_LENGTH = 16;
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentServerDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        try {
            do {
                int saveReaderIndex = buffer.readerIndex();
                Object msg = null;
                LOGGER.info("*******hhhhahhaha*********************");
                try {
                    msg = decode2(buffer);
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

    private Object decode2(ByteBuf byteBuf){

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }
        LOGGER.info("*******************decode2 agent client send msg*************");
        byte[] header = new byte[HEADER_LENGTH];
        byteBuf.readBytes(header);
        byte[] dataLen = Arrays.copyOfRange(header,12,16);
        int len = Bytes.bytes2int(dataLen);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.readerIndex(savedReaderIndex);
        byte[] data = new byte[tt];
        byteBuf.readBytes(data);
        return data;
    }
}
