package com.smartluobo.mesh.agent.netty;

import com.smartluobo.mesh.agent.rpc.NettyRpcClient;
import com.smartluobo.mesh.agent.threadpool.NettyServerBusinessThreadPoolUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentNettyServerInHandle extends SimpleChannelInboundHandler<byte[]>{
	private NettyRpcClient nettyRpcClient;

	private static final Logger LOGGER = LoggerFactory.getLogger(AgentNettyServerInHandle.class);
	private static final byte FLAG = (byte) 0xCC;

	public AgentNettyServerInHandle(NettyRpcClient nettyRpcClient ){
		this.nettyRpcClient = nettyRpcClient;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		try{
			LOGGER.info("AgentNettyServerInHandle receive agent client send msg :msg length: "+msg.length );
			long startTime = System.currentTimeMillis();
			NettyServerBusinessThreadPoolUtil.doBusiness(ctx, msg,nettyRpcClient);
//			Object reslut = nettyRpcClient.invoke(msg);
//			LOGGER.info("dubbo provider return result : "+reslut);
//			long endTime = System.currentTimeMillis();
//			LOGGER.info("AgentNettyServerInHandle provider-agent to dubbo-provider wait :"+(endTime-startTime)+"ms");
//			ctx.writeAndFlush(reslut);
		}finally {
			ReferenceCountUtil.release(msg);
		}
	}

//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (evt instanceof IdleStateEvent) {
//			// 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
//			sendHeartbeatPacket(ctx);
//		}
//	}
//
//	public void sendHeartbeatPacket(ChannelHandlerContext ctx){
//		LOGGER.info("dubbo RpcClient send heart ..................");
//		DubboProtocolRequest heartInstance = DubboProtocolRequest.getHeartInstance();
//		ctx.writeAndFlush(heartInstance.getHeader().array());
//	}
}
