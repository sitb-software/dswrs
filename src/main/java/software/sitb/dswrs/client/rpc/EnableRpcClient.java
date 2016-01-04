package software.sitb.dswrs.client.rpc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Sean sean.snow@live.com
 * @date 2016/1/1
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcApplicationInitializedPublisher.Registrar.class, RpcApplicationAutoConfiguration.class})
public @interface EnableRpcClient {

    Class<?>[] rpcServiceBaseInterface() default {};

}
