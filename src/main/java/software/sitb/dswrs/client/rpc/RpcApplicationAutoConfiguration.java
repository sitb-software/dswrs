package software.sitb.dswrs.client.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.sitb.dswrs.core.ServiceDiscovery;
import software.sitb.dswrs.core.config.DswrsProperties;
import software.sitb.dswrs.core.zookeeper.ZooKeeperServiceDiscovery;

/**
 * @author Sean sean.snow@live.com
 */
@Configuration
@EnableConfigurationProperties(DswrsProperties.class)
public class RpcApplicationAutoConfiguration {

    @Autowired
    private DswrsProperties properties;

    @Bean
    @ConditionalOnMissingBean(ServiceDiscovery.class)
    public ServiceDiscovery serviceDiscovery() {
        if (properties.getZooKeeper().getEnabled()) {
            ZooKeeperServiceDiscovery serviceDiscovery = new ZooKeeperServiceDiscovery();
            serviceDiscovery.setConnectString(properties.getZooKeeper().getConnectString());
            serviceDiscovery.setSessionTimeOut(properties.getZooKeeper().getSessionTimeout());
            serviceDiscovery.setRegistryPath(properties.getZooKeeper().getRegistryPath());
            serviceDiscovery.init();
            return serviceDiscovery;
        }

        if (null == properties.getHost()) {
            throw new IllegalArgumentException("ZooKeeper 没有启用,也没有配置默认的主机 dswrs.host = null");
        }
        if (null == properties.getPort()) {
            throw new IllegalArgumentException("ZooKeeper 没有启用,也没有配置默认的端口 dswrs.port = null");
        }

        return () -> properties.getHost() + ":" + properties.getPort();

    }
}
