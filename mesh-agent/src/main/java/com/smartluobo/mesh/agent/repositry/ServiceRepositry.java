package com.smartluobo.mesh.agent.repositry;

import com.smartluobo.mesh.agent.node.NodeInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceRepositry {
    List<NodeInfo> nodeInfos;

    public List<NodeInfo> getNodeInfos() {
        return nodeInfos;
    }

    public void setNodeInfos(List<NodeInfo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }
}
