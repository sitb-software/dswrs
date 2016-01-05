package software.sitb.dswrs.client.rpc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动RPC客户端自动配置
 *
 * @author Sean sean.snow@live.com
 * @see software.sitb.dswrs.client.rpc.RpcApplicationInitializedPublisher.Registrar
 * @see RpcApplicationAutoConfiguration
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcApplicationInitializedPublisher.Registrar.class, RpcApplicationAutoConfiguration.class})
public @interface EnableRpcClient {

    /**
     * 基础接口类
     * @return 接口类型Class
     */
    Class<?>[] rpcServiceBaseInterface() default {};

}
