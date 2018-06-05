package com.smartluobo.mesh.agent.registry;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.smartluobo.mesh.agent.constant.Constant;
import com.smartluobo.mesh.agent.node.NodeInfo;
import com.smartluobo.mesh.agent.repositry.ServiceRepositry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EtcdRegistry implements IRegistry{

    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdRegistry.class);
    private Lease lease;
    private KV kv;
    private long leaseId;
    private Watch watch;
    private ServiceRepositry serviceRepositry;

    public EtcdRegistry(String registryAddress,ServiceRepositry serviceRepositry){
        this.serviceRepositry = serviceRepositry;
        Client client = Client.builder().endpoints(registryAddress).build();
        this.lease   = client.getLeaseClient();
        this.kv      = client.getKVClient();
        this.watch   = client.getWatchClient();
        try {
            this.leaseId = lease.grant(30).get().getID();
        } catch (Exception e) {
            e.printStackTrace();
        }

        keepAlive();
    }

    /**
     * 将服务信息注册到etcd
     * @param serviceName
     * @param port
     * @throws Exception
     */
    @Override
    public void register(String serviceName, int port) throws Exception {
        LOGGER.info("start registry service to etcd serviceName: "+serviceName+" port: "+port);
        // 服务注册的key为:  /agent/mesh/com.some.package.IHelloService/192.168.100.100:2000
        String strKey = MessageFormat.format("/{0}/{1}/{2}:{3}:{4}",
                Constant.ROOT_PATH,serviceName,IpHelper.getHostIp(),String.valueOf(port),Constant.PROVIDER_WEIGHT);
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString("123456789");
        kv.put(key,val, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        LOGGER.info("Register a new service at:" + strKey);
    }

    /**
     * 通过服务名称serviceName 从etcd注册中心获取服务列表
     * @param serviceName
     * @return
     * @throws Exception
     */
    @Override
    public void find(final String serviceName) throws Exception {
        String strKey = MessageFormat.format("/{0}/{1}",Constant.ROOT_PATH,serviceName);
        final ByteSequence key  = ByteSequence.fromString(strKey);
        findKey(key);
        watchEtcdKey(key);
    }

    public void findKey(ByteSequence key ) throws Exception{
        List<NodeInfo> nodeInfos = new ArrayList<>();
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();
        for (KeyValue kv : response.getKvs()){
            String s = kv.getKey().toStringUtf8();
            int index = s.lastIndexOf("/");
            String NodeInfoStr = s.substring(index + 1,s.length());
            String[] nodeInfosStr = NodeInfoStr.split(":");
            String host = nodeInfosStr[0];
            String port = nodeInfosStr[1];
            Integer weight = Integer.valueOf(nodeInfosStr[2]);
            for (int i = 0; i < weight; i++) {
                nodeInfos.add(new NodeInfo(host,port));
            }
        }
        for (NodeInfo nodeInfo : nodeInfos) {
            LOGGER.info("----------------------------------"+nodeInfo.toString()+"-------------------------------------");
        }
        serviceRepositry.setNodeInfos(nodeInfos);
    }

    // 发送心跳到ETCD,表明该host是活着的
    public void keepAlive(){
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        Lease.KeepAliveListener listener = lease.keepAlive(leaseId);
                        listener.listen();
                        LOGGER.info("KeepAlive lease:" + leaseId + "; Hex format:" + Long.toHexString(leaseId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void watchEtcdKey(ByteSequence key){
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    Watch.Watcher watcher = this.watch.watch(key);
                    while (true){
                        try {
                            for (WatchEvent event : watcher.listen().getEvents()) {
                                KeyValue kv = event.getKeyValue();
                                System.out.println(event.getEventType());
                                System.out.println(kv.getKey().toStringUtf8());
                                System.out.println(kv.getValue().toStringUtf8());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}
