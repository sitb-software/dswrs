package software.sitb.dswrs.server.rpc;

import io.netty.channel.ChannelHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import software.sitb.dswrs.core.ServiceRegistry;
import software.sitb.dswrs.core.config.DswrsProperties;
import software.sitb.dswrs.core.netty.NettyServer;
import software.sitb.dswrs.core.rpc.RpcRequest;
import software.sitb.dswrs.core.rpc.RpcResponse;
import software.sitb.dswrs.core.zookeeper.ZooKeeperConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC 服务器
 *
 * @author Sean sean.snow@live.com
 */
public abstract class RpcServer extends NettyServer<RpcRequest, RpcResponse> implements ApplicationContextAware, InitializingBean {

    private DswrsProperties properties;

    private ServiceRegistry serviceRegistry;

    private Map<String, Object> rpcBeans = new HashMap<>(0);

    @Override
    public int getPort() {
        return properties.getPort();
    }

    @Override
    public String getHost() {
        return properties.getHost();
    }

    @Override
    public Class<RpcRequest> getRequestClazz() {
        return RpcRequest.class;
    }

    @Override
    public Class<RpcResponse> getResponseClazz() {
        return RpcResponse.class;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start(() -> {
            ZooKeeperConfig config = this.properties.getZooKeeper();
            if (config.getEnabled()) {
                String data = this.getHost() + ":" + this.getPort();
                this.serviceRegistry.register(data);
            }
            this.serverStartCallback();
        });
    }

    @Override
    public ChannelHandler getMessageHandler() {
        RpcServerHandler handler = new RpcServerHandler();
        handler.setRpcServiceBean(this.rpcBeans);
        return handler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.properties = applicationContext.getBean(DswrsProperties.class);
        this.serviceRegistry = applicationContext.getBean(ServiceRegistry.class);
        Map<String, Object> rpcBeans = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (null != rpcBeans && !rpcBeans.isEmpty()) {
            this.rpcBeans = new HashMap<>(rpcBeans.size());
            rpcBeans.forEach((key, value) -> {
                String interfaceName = value.getClass().getAnnotation(RpcService.class).value().getName();
                this.rpcBeans.put(interfaceName, value);
            });
        }
    }


    /**
     * 服务启动回调函数
     */
    public abstract void serverStartCallback();

}
