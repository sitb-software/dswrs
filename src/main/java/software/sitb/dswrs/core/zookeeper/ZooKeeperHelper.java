package software.sitb.dswrs.core.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author Sean sean.snow@live.com
 */
public class ZooKeeperHelper {

    private CountDownLatch latch = new CountDownLatch(1);

    private ZooKeeperHelper() {
    }

    public static ZooKeeperHelper getInstance() {
        return ZooKeeperHelperFactory.zooKeeperHelper;
    }

    /**
     * 连接到ZK
     *
     * @param connectString  zk地址
     * @param sessionTimeout 超时时间
     * @return ZooKeeper
     * @throws IOException          IOException IOException
     * @throws InterruptedException InterruptedException InterruptedException
     */
    public ZooKeeper connectServer(String connectString, int sessionTimeout) throws IOException, InterruptedException {
        ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                latch.countDown();
            }
        });
        latch.await();
        return zk;
    }

    /**
     * 创建一个根节点
     *
     * @param zk       ZooKeeper
     * @param rootPath 根路径
     * @return 创建后的路径
     * @throws KeeperException KeeperException
     * @throws InterruptedException InterruptedException
     */
    public String createRootNode(ZooKeeper zk, String rootPath) throws KeeperException, InterruptedException {
        return createNode(zk, rootPath, null, CreateMode.PERSISTENT);
    }

    /**
     * 创建一个子节点
     *
     * @param zk            ZooKeeper
     * @param childNodePath 路径
     * @param nodeData      数据
     * @return 创建后的路径
     * @throws KeeperException KeeperException
     * @throws InterruptedException InterruptedException
     */
    public String createChildNode(ZooKeeper zk, String childNodePath, String nodeData) throws KeeperException, InterruptedException {
        return createNode(zk, childNodePath, nodeData, CreateMode.EPHEMERAL);
    }

    /**
     * 创建一个节点
     *
     * @param zk         Zk 服务
     * @param nodePath   节点路径
     * @param nodeData   节点数据
     * @param createMode 创建模式
     * @return 创建的路径
     * @throws KeeperException KeeperException
     * @throws InterruptedException InterruptedException
     */
    private String createNode(ZooKeeper zk, String nodePath, String nodeData, CreateMode createMode) throws KeeperException, InterruptedException {
        byte[] bytes = nodeData == null || nodeData.isEmpty() ? null : nodeData.getBytes();
        return zk.create(nodePath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    private static class ZooKeeperHelperFactory {
        private static ZooKeeperHelper zooKeeperHelper = new ZooKeeperHelper();
    }
}
