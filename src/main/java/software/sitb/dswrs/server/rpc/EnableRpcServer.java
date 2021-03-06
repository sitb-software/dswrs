package software.sitb.dswrs.server.rpc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Sean sean.snow@live.com
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcApplicationServerAutoConfiguration.class})
public @interface EnableRpcServer {

}
