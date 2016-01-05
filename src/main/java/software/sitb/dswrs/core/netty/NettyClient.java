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
public abstract class NettyClient<I, O> extends SimpleChannelInboundHandler<O> implements NettyNetwork<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private final Object obj = new Object();
    private O response;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }

    public O send(I request) throws Exception {
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

            ChannelFuture future = bootstrap.connect(getHost(), getPort()).sync();
            LOGGER.info("send data -> {}", request);
            future.channel().writeAndFlush(request).sync();

            synchronized (obj) {
                LOGGER.debug("Waiting response....");
                obj.wait();
            }

            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext arg0, O response) throws Exception {
        LOGGER.info("receive data -> {}", response);
        this.response = response;
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    protected void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast(new MessageDecoder(getResponseClazz()));
        pipeline.addLast(new MessageEncoder(getRequestClazz()));
    }


}


