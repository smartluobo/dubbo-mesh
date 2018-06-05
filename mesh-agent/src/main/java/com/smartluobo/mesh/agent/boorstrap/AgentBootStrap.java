package com.smartluobo.mesh.agent.boorstrap;

import com.smartluobo.mesh.agent.constant.Constant;
import com.smartluobo.mesh.agent.netty.NettyUtil;
import com.smartluobo.mesh.agent.node.NodeInfo;
import com.smartluobo.mesh.agent.registry.EtcdRegistry;
import com.smartluobo.mesh.agent.repositry.ServiceRepositry;
import com.sun.javafx.binding.IntegerConstant;
import com.sun.javafx.binding.StringConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AgentBootStrap implements InitializingBean,ApplicationListener<ContextRefreshedEvent> {

    String type = System.getProperty("agent.type");
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentBootStrap.class);

    @Resource
    private ServiceRepositry serviceRepositry;

    @Resource
    private NettyUtil nettyUtil;

    @Override
    public void afterPropertiesSet() throws Exception {
        EtcdRegistry etcdRegistry = new EtcdRegistry(Constant.ETCD_URL,serviceRepositry);
        if("consumer".equals(type)){
            //如果是consumer类型的agent从etcd注册中心获取服务列表信息
            etcdRegistry.find(Constant.SERVICE_NAME);
        }else if("provider".equals(type)){
            //如果是provider类型的agent将服务信息注册到etcd注册中心
            etcdRegistry.register(Constant.SERVICE_NAME, Integer.valueOf(Constant.AGENT_NETTY_PORT));
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(ContextRefreshedEvent.class.getName().equals(event.getClass().getName())) {
            if("provider".equals(type)) {
                new Thread(() -> {
                    try {
                        nettyUtil.startNettyServer(Constant.AGENT_NETTY_PORT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}
