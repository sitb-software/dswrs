package software.sitb.dswrs.server.rpc;

import java.lang.annotation.*;

/**
 * RPC 标注该服务为RPC服务
 *
 * @author Sean sean.snow@live.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
    Class<?> value();
}
