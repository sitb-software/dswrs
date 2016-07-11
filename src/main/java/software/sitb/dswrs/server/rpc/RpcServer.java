package software.sitb.dswrs.server.rpc;

import io.netty.channel.ChannelPipeline;
import org.springframework.context.ApplicationContext;
import software.sitb.dswrs.core.ServiceRegistry;
import software.sitb.dswrs.core.config.DswrsProperties;
import software.sitb.dswrs.core.netty.ObjectServer;
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
public class RpcServer extends ObjectServer<RpcRequest, RpcResponse> {

    private DswrsProperties properties;

    private ServiceRegistry serviceRegistry;

    private Map<String, Object> rpcBeans = new HashMap<>(0);

    private ApplicationContext applicationContext;

    public RpcServer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.init();
    }

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

    /**
     * 添加数据处理通道
     *
     * @param pipeline 管道
     */
    @Override
    public void addMessageHandler(ChannelPipeline pipeline) {
        RpcServerHandler handler = new RpcServerHandler();
        handler.setRpcServiceBean(this.rpcBeans);
        pipeline.addLast(handler);
    }

    protected void init() {
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
     * 添加一个RPC服务Bean
     *
     * @param clazz 接口Class
     * @param bean  接口实现示例
     */
    public void addRpcBeans(Class<?> clazz, Object bean) {
        if (null != clazz && null != bean) {
            String interfaceName = clazz.getName();
            this.rpcBeans.put(interfaceName, bean);
        }
    }

    public void startRpcServer() throws InterruptedException {
        this.start(() -> {
            ZooKeeperConfig config = this.properties.getZooKeeper();
            if (config.getEnabled()) {
                String data = this.getHost() + ":" + this.getPort();
                this.serviceRegistry.register(data);
            }
        });
    }

}
