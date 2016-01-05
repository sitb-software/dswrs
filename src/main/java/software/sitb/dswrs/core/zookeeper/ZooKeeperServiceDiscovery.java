package software.sitb.dswrs.core.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.sitb.dswrs.core.ServiceDiscovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务发现
 *
 * @author Sean sean.snow@live.com
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private ZooKeeperHelper helper = ZooKeeperHelper.getInstance();

    private volatile List<String> dataList = new ArrayList<>();

    private String connectString;

    private int sessionTimeOut;

    private String registryPath;

    public void init() {
        ZooKeeper zk = null;
        try {
            zk = helper.connectServer(getConnectString(), getSessionTimeOut());
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        if (zk != null) {
            watchNode(zk);
        }
    }

    @Override
    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        if (null == data) {
            throw new IllegalArgumentException("没有可用的服务器(Did not find the available server) on ZooKeeper node > " + getRegistryPath());
        }
        return data;
    }


    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(getRegistryPath(), event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNode(zk);
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(getRegistryPath() + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            LOGGER.debug("node data: {}", dataList);
            this.dataList = dataList;
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public void setRegistryPath(String registryPath) {
        this.registryPath = registryPath;
    }
}
