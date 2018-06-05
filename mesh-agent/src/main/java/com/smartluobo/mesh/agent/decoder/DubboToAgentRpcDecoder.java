package com.smartluobo.mesh.agent.decoder;

import com.smartluobo.mesh.agent.dubbo.Bytes;
import com.smartluobo.mesh.agent.enumer.DecodeResult;
import com.smartluobo.mesh.agent.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class DubboToAgentRpcDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboToAgentRpcDecoder.class);
    // header length.
    protected static final int HEADER_LENGTH = 16;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

        try {
            do {
                int savedReaderIndex = byteBuf.readerIndex();
                LOGGER.info("byteBuffer readable position: "+savedReaderIndex);
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

                list.add(msg);
            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }
    }

    /**
     * Demo为简单起见，直接从特定字节位开始读取了的返回值，demo未做：
     * 1. 请求头判断
     * 2. 返回值类型判断
     *
     * @param byteBuf
     * @return
     */
    private Object decode2(ByteBuf byteBuf){

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();
        LOGGER.info("byteBuffer readable limit :"+readable);
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[HEADER_LENGTH];//读取header头信息
        byteBuf.readBytes(header);
        byte[] dataLen = Arrays.copyOfRange(header,12,16);//读取信息长度信息
        int len = Bytes.bytes2int(dataLen);//转换长度信息为整型
        int tt = len + HEADER_LENGTH;//计算返回报文长度信息
        if (readable < tt) {//如果报文长度信息不等于可读长度信息表示服务端返回的报文有拆包行为
            return DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.readerIndex(savedReaderIndex);
        byte[] data = new byte[tt];
        byteBuf.readBytes(data);//将报文信息读取到临时字节数组钟
        byte[] requestIdBytes = Arrays.copyOfRange(data,4,12);//
        long requestId = Bytes.bytes2long(requestIdBytes,0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(data);
        return response;
    }
}
