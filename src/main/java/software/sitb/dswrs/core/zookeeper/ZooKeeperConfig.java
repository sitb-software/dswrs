package software.sitb.dswrs.core.zookeeper;

/**
 * @author Sean sean.snow@live.com
 */
public class ZooKeeperConfig {

    private Boolean enabled;

    private String connectString;

    private Integer sessionTimeout;

    private String registryPath;

    private String nodePath;

    private Integer initIdleCapacity;

    private Integer maxIdle;

    private Integer initialSize;

    public ZooKeeperConfig() {
        this.enabled = true;
    }

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public void setRegistryPath(String registryPath) {
        this.registryPath = registryPath;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public Integer getInitIdleCapacity() {
        return initIdleCapacity;
    }

    public void setInitIdleCapacity(Integer initIdleCapacity) {
        this.initIdleCapacity = initIdleCapacity;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
