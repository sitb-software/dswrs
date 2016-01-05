package software.sitb.dswrs.server.rpc;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * RPC 标注该服务为RPC服务
 *
 * @author Sean sean.snow@live.com
 * @see Component
 * @see Repository
 * @see Service
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RpcService {
    Class<?> value();
}
