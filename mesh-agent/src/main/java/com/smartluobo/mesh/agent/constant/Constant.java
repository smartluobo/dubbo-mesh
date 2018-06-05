package com.smartluobo.mesh.agent.constant;

public class Constant {
    public static final String SERVICE_NAME = "com.alibaba.dubbo.performance.demo.provider.IHelloService";

    public static final String ETCD_URL = System.getProperty("etcd.url","http://192.168.1.100:2379");

    public static final String ROOT_PATH = "agent/mesh";

    public static final int PROVIDER_AGENT_PORT=Integer.valueOf(System.getProperty("server.port","30000"));

    public static final String DUBBO_VERSION="2.6.1";

    public static final String SERVICE_VERSION="0.0.0.0";

    public static final String DUBBO_PORT=System.getProperty("dubbo.protocol.port","20889");

    public static final String AGENT_NETTY_PORT=System.getProperty("agent.netty.port","9559");

    public static final String PROVIDER_WEIGHT=System.getProperty("provider.weight","1");

    public static final byte HEARTFLAG = (byte) 0xCC;//心跳包检测


}
