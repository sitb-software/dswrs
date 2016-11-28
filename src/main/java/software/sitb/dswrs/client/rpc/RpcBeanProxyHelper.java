package software.sitb.dswrs.client.rpc;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import software.sitb.dswrs.core.ServiceDiscovery;
import software.sitb.dswrs.core.netty.ObjectClient;
import software.sitb.dswrs.core.rpc.RpcRequest;
import software.sitb.dswrs.core.rpc.RpcResponse;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 创建RPC Bean 代理类
 *
 * @author Sean sean.snow@live.com
 */
public class RpcBeanProxyHelper implements MethodInterceptor {

    private Enhancer enhancer = new Enhancer();

    private ServiceDiscovery serviceDiscovery;


    /**
     * 创建一个代理bean
     *
     * @param clazz 目标类
     * @return 代理类
     */
    public Object createBean(Class<?> clazz) {
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());

        Object[] params = new Object[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                params[i] = "DSWRS_NULL_DATA";
            } else {
                params[i] = objects[i];
            }
        }

        request.setParameters(params);

        String[] serverAddress = getServiceDiscovery().discover().split(":");

        String host = serverAddress[0];
        int port = Integer.parseInt(serverAddress[1]);

        ObjectClient<RpcRequest, RpcResponse> client = new ObjectClient<RpcRequest, RpcResponse>() {
            @Override
            public int getPort() {
                return port;
            }

            @Override
            public String getHost() {
                return host;
            }

            @Override
            public Class<RpcRequest> getRequestClazz() {
                return RpcRequest.class;
            }

            @Override
            public Class<RpcResponse> getResponseClazz() {
                return RpcResponse.class;
            }
        };

        RpcResponse response = client.send(request);

        if (null == response) {
            throw new RuntimeException("RPC 执行中发生未知异常");
        }

        if (response.success()) {
            return response.getResult();
        } else if (response.getError() != null) {
            throw response.getError();
        } else {
            throw new RuntimeException("RPC未知错误");
        }
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
}
