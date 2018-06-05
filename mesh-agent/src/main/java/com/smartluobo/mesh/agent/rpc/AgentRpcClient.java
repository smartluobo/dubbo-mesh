package com.smartluobo.mesh.agent.rpc;

import com.smartluobo.mesh.agent.model.AgentRpcRequestHolder;
import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.netty.AgentConnecManager;
import com.smartluobo.mesh.agent.protocol.AgentProtocolRequest;
import com.smartluobo.mesh.agent.protocol.DubboProtocolRequest;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AgentRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentRpcClient.class);

    @Resource
    private AgentConnecManager agentConnecManager;

    public Object invoke(String hostAndPort, AgentProtocolRequest requestMsg) throws Exception {

        Channel channel = agentConnecManager.getChannel(hostAndPort);
        LOGGER.info("agent netty channel :"+channel.toString());
        LOGGER.info("requestId=" + requestMsg.getRequestId());
        RpcFuture future = new RpcFuture();
        AgentRpcRequestHolder.put(String.valueOf(requestMsg.getRequestId()),future);
        requestMsg.getPath();
        channel.writeAndFlush(requestMsg);
        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Object invoke(String hostAndPort, String requestMsg) throws Exception {
        Channel channel = agentConnecManager.getChannel(hostAndPort);
        channel.writeAndFlush(requestMsg);
        return "123";
    }
    public Object invoke(String hostAndPort, DubboProtocolRequest requestMsg) throws Exception {

        Channel channel = agentConnecManager.getChannel(hostAndPort);
        LOGGER.info("agent netty channel :"+channel.toString());
        LOGGER.info("requestId=" + requestMsg.getRequestId());
        RpcFuture future = new RpcFuture();
        AgentRpcRequestHolder.put(String.valueOf(requestMsg.getRequestId()),future);

        channel.writeAndFlush(requestMsg);
        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
