package software.sitb.dswrs.core.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP netty 客户端
 * author 田尘殇(Sean tianchenshang@lijindai.com)
 * date 2015-4-11
 * time 下午1:45:21
 */
public abstract class NettyClient<I, O> extends ChannelInboundHandlerAdapter implements NettyNetwork {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private I request;

    private O response;

    public O send(I request) throws Exception {
        this.request = request;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    addHandler(channel.pipeline());
                    channel.pipeline().addLast(NettyClient.this);
                }
            }).option(ChannelOption.SO_KEEPALIVE, true);

            bootstrap.connect(getHost(), getPort()).channel().closeFuture().await();
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("send data => [{}]", request);
        ctx.writeAndFlush(request);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("receive data -> [{}]", msg);
        response = (O) msg;
        LOGGER.info("read complete.close channel.");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("NettyClient Exception", cause);
        super.exceptionCaught(ctx, cause);
    }
}


