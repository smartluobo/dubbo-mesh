package com.smartluobo.mesh.agent.rpc;

import com.alibaba.fastjson.JSONObject;
import com.smartluobo.mesh.agent.constant.Constant;
import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.netty.DubboConnecManager;
import com.smartluobo.mesh.agent.protocol.AgentProtocolRequest;
import com.smartluobo.mesh.agent.protocol.DubboProtocolRequest;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DubboRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboRpcClient.class);

    @Resource
    private DubboConnecManager connectManager;

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Channel channel = connectManager.getChannel();
        LOGGER.info("netty channel :"+channel.toString());

        DubboProtocolRequest dubboRequest = new DubboProtocolRequest();

        dubboRequest.dubboVersion(Constant.DUBBO_VERSION)
                .serviceVersion(Constant.SERVICE_VERSION)
                .path(interfaceName)
                .method(method)
                .paramTypes(parameterTypesString)
                .parameter(parameter)
                .attachments(JSONObject.parseObject("{\"path\":\""+interfaceName+"\",\"interface\":\""+interfaceName+"\",\"version\":\"0.0.0\"}"))
                .build(dubboRequest.getRequestId());

        LOGGER.info("requestId=" + dubboRequest.getRequestId());

        RpcFuture future = new RpcFuture();
        DubboRpcRequestHolder.put(String.valueOf(dubboRequest.getRequestId()),future);

        channel.writeAndFlush(dubboRequest);
        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Object invoke(DubboProtocolRequest dubboProtocolRequest) {
        return "hello world";
    }

    public Object invoke(AgentProtocolRequest agentProtocolRequest) throws Exception {
        Channel channel = connectManager.getChannel();
        LOGGER.info("netty channel :"+channel.toString());
        String interfaceName =agentProtocolRequest.getPath();

        DubboProtocolRequest dubboRequest = new DubboProtocolRequest();

        dubboRequest.dubboVersion(Constant.DUBBO_VERSION)
                .serviceVersion(Constant.SERVICE_VERSION)
                .path(interfaceName)
                .method(agentProtocolRequest.getMethod())
                .paramTypes(agentProtocolRequest.getParamTypes())
                .parameter(agentProtocolRequest.getParameter())
                .attachments(JSONObject.parseObject("{\"path\":\""+interfaceName+"\",\"interface\":\""+interfaceName+"\",\"version\":\"0.0.0\"}"))
                .setRequestId(agentProtocolRequest.getRequestId())
                .build(dubboRequest.getRequestId());

        LOGGER.info("requestId=" + dubboRequest.getRequestId());

        RpcFuture future = new RpcFuture();
        DubboRpcRequestHolder.put(String.valueOf(dubboRequest.getRequestId()),future);

        channel.writeAndFlush(dubboRequest);
        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Object invoke(Object msg) {
        return null;
    }
}
