package com.smartluobo.mesh.agent.rpc;

import com.smartluobo.mesh.agent.dubbo.Bytes;
import com.smartluobo.mesh.agent.model.DubboRpcRequestHolder;
import com.smartluobo.mesh.agent.model.RpcFuture;
import com.smartluobo.mesh.agent.netty.NettyConnecManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

@Component
public class NettyRpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRpcClient.class);

    @Resource
    private NettyConnecManager nettyConnecManager;

    public Object invoke(byte[] msg) throws Exception {

        Channel channel = nettyConnecManager.getChannel();
        LOGGER.info("netty channel :"+channel.toString());
        byte[] requestIdBytes = Arrays.copyOfRange(msg,4,12);//获取requestId
        long requestId = Bytes.bytes2long(requestIdBytes,0);
        LOGGER.info("requestId=" + requestId);

        RpcFuture future = new RpcFuture();
        DubboRpcRequestHolder.put(String.valueOf(requestId),future);

        channel.writeAndFlush(msg);
        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bytes;
    }
}
