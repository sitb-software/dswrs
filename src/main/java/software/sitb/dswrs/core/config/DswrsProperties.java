package software.sitb.dswrs.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import software.sitb.dswrs.core.zookeeper.ZooKeeperConfig;

/**
 * @author Sean sean.snow@live.com
 * @date 2016/1/1
 */
@ConfigurationProperties(prefix = "dswrs", ignoreUnknownFields = true)
public class DswrsProperties {

    private String host;

    private Integer port;

    private ZooKeeperConfig zooKeeper;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ZooKeeperConfig getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeperConfig zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
}
