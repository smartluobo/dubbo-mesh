package com.smartluobo.mesh.agent.heart;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class AgentClientHeartbeatHandler extends SimpleChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentClientHeartbeatHandler.class);
    private static final  ByteBuffer HEADER = ByteBuffer.allocate(16);
    static {
        // 魔数 da bb
        HEADER.put((byte) 0xda);
        HEADER.put((byte) 0xbb);

        // 标识 固定为 C6-请求报文 CC-心跳检测报文
        HEADER.put((byte) 0xCC);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("receive message :"+msg);
    }

}
