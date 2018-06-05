package com.smartluobo.mesh.agent.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.smartluobo.mesh.agent.constant.Constant;
import com.smartluobo.mesh.agent.loadbalance.LoadBalance;
import com.smartluobo.mesh.agent.netty.NettyUtil;
import com.smartluobo.mesh.agent.node.NodeInfo;
import com.smartluobo.mesh.agent.protocol.AgentProtocolRequest;
import com.smartluobo.mesh.agent.protocol.DubboProtocolRequest;
import com.smartluobo.mesh.agent.rpc.AgentRpcClient;
import com.smartluobo.mesh.agent.rpc.DubboRpcClient;
import com.smartluobo.mesh.agent.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Resource(name = "roundRobinLoadBalance")
    private LoadBalance loadBalance;

    @Resource
    private AgentRpcClient agentRpcClient;

    String type = System.getProperty("agent.type");

    @Resource
    private DubboRpcClient dubboRpcClient;

    @Override
    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) {
        if("provider".equals(type)){
            try {
                return dubboRpcClient.invoke(interfaceName,method,parameterTypesString,parameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;//provider-agent不提供http请求服务返回null
        }else {
            NodeInfo nodeInfo = loadBalance.doSelect();
            StringBuffer sb = new StringBuffer();
            sb.append(nodeInfo.getHost()).append(":").append(nodeInfo.getPort());
//            AgentProtocolRequest agentProtocolRequest = new AgentProtocolRequest();
//            agentProtocolRequest.path(interfaceName)
//                    .method(method)
//                    .paramTypes(parameterTypesString)
//                    .parameter(parameter);
            DubboProtocolRequest dubboRequest = new DubboProtocolRequest();
            dubboRequest.dubboVersion(Constant.DUBBO_VERSION)
                    .serviceVersion(Constant.SERVICE_VERSION)
                    .path(interfaceName)
                    .method(method)
                    .paramTypes(parameterTypesString)
                    .parameter(parameter)
                    .attachments(JSONObject.parseObject("{\"path\":\""+interfaceName+"\",\"interface\":\""+interfaceName+"\",\"version\":\"0.0.0\"}"))
                    .build(dubboRequest.getRequestId());
            try {
                LOGGER.info("request send provider agent address: "+sb.toString());
//                return agentRpcClient.invoke(sb.toString(),agentProtocolRequest);
                Object result = agentRpcClient.invoke(sb.toString(), dubboRequest);
                LOGGER.info("return result : "+result);
                return result;
            }catch (Exception e){
                LOGGER.error("request send provider agent address: "+sb.toString()+"exception :",e);
                return "request send provider agent address: "+sb.toString()+"exception ";
            }
        }

    }
}
