package com.smartluobo.mesh.agent.registry;

public interface IRegistry {
    // 注册服务
    void register(String serviceName, int port) throws Exception;

    //获取服务列表信息
    void find(String serviceName) throws Exception;

}
