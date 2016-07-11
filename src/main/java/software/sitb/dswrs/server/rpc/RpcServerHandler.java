package software.sitb.dswrs.server.rpc;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import software.sitb.dswrs.core.rpc.RpcRequest;
import software.sitb.dswrs.core.rpc.RpcResponse;

import java.util.Map;

/**
 * @author Sean sean.snow@live.com
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private Map<String, Object> rpcServiceBean;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ctx.writeAndFlush(handler((RpcRequest) msg));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("服务处理异常,关闭通道", cause);
        if (ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    /**
     * RPC 请求处理
     *
     * @param request 请求RPC信息
     * @return RPC 处理结果
     * @see RpcResponse
     */
    private RpcResponse handler(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            String className = request.getClassName();
            Object serviceBean = rpcServiceBean.get(className);
            if (null == serviceBean) {
                throw new IllegalArgumentException("请求RPC服务不存在 -> " + className);
            }

            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();

            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();
            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
            response.setResult(serviceFastMethod.invoke(serviceBean, parameters));
        } catch (Throwable t) {
            LOGGER.error("RPC Handler Error -> {}", t.getMessage(), t);
            response.setError(t);
        }
        return response;
    }

    public void setRpcServiceBean(Map<String, Object> rpcServiceBean) {
        this.rpcServiceBean = rpcServiceBean;
    }


}
