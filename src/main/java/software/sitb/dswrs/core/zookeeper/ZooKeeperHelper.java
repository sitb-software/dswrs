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
     * 创建一个临时节点
     *
     * @param path 路径
     * @param data 数据
     * @param zk ZooKeeper
     * @return the actual path of the created node
     * @throws KeeperException if the server returns a non-zero error code
     * @throws InterruptedException if the transaction is interrupted
     */
    public String createNodeWithEphemeral(ZooKeeper zk, String path, String data) throws KeeperException, InterruptedException {
        return createNode(zk, path, data, CreateMode.EPHEMERAL);
    }


    /**
     * 创建一个永久节点
     *
     * @param path 路径
     * @param data 数据
     * @param zk ZooKeeper
     * @return the actual path of the created node
     * @throws KeeperException if the server returns a non-zero error code
     * @throws InterruptedException if the transaction is interrupted
     */
    public String createNodeWithPersistent(ZooKeeper zk, String path, String data) throws KeeperException, InterruptedException {
        return createNode(zk, path, data, CreateMode.PERSISTENT);
    }


    /**
     * 创建一个节点
     *
     * @param zk         Zk 服务
     * @param nodePath   节点路径
     * @param nodeData   节点数据
     * @param createMode 创建模式
     * @return 创建的路径
     * @throws KeeperException      KeeperException
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
