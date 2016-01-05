package software.sitb.dswrs.core.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.sitb.dswrs.core.ServiceRegistry;

import java.io.IOException;

/**
 * 服务注册
 *
 * @author Sean sean.snow@live.com
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private ZooKeeperHelper helper = ZooKeeperHelper.getInstance();

    private String connectString;
    private int sessionTimeout;
    private String registryPath;
    private String nodePath;

    @Override
    public void register(Object data) {
        if (data != null) {
            ZooKeeper zk = null;
            try {
                zk = helper.connectServer(getConnectString(), getSessionTimeout());
            } catch (IOException | InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
            if (zk != null) {
                try {
                    Stat stat = zk.exists(getRegistryPath(), true);
                    if (null == stat) {
                        String root = helper.createRootNode(zk, getRegistryPath());
                        LOGGER.debug("create root node {}", root);
                    }
                    String nodePath = getRegistryPath() + getNodePath();
                    stat = zk.exists(nodePath, true);
                    if (null == stat) {
                        String serverNode = helper.createChildNode(zk, nodePath, data.toString());
                        LOGGER.debug("create server node {}", serverNode);
                    }
                } catch (KeeperException | InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public void setRegistryPath(String registryPath) {
        this.registryPath = registryPath;
    }
}
