package software.sitb.dswrs.server.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.sitb.dswrs.core.ServiceRegistry;
import software.sitb.dswrs.core.config.DswrsProperties;
import software.sitb.dswrs.core.zookeeper.ZooKeeperServiceRegistry;

/**
 * 服务Bean配置
 * 引入项目Properties配置文件
 *
 * @author Sean sean.snow@live.com
 */
@Configurable
@EnableConfigurationProperties({DswrsProperties.class})
public class RpcApplicationServerAutoConfiguration {

    @Autowired
    private DswrsProperties properties;


    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry serviceRegistry() {
        if (properties.getZooKeeper().getEnabled()) {
            ZooKeeperServiceRegistry serviceRegistry = new ZooKeeperServiceRegistry();
            serviceRegistry.setConnectString(properties.getZooKeeper().getConnectString());
            serviceRegistry.setSessionTimeout(properties.getZooKeeper().getSessionTimeout());
            serviceRegistry.setRegistryPath(properties.getZooKeeper().getRegistryPath());
            serviceRegistry.setNodePath(properties.getZooKeeper().getNodePath());
            return serviceRegistry;
        }

        //default
        return data -> {
        };
    }

}
