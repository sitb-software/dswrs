package software.sitb.dswrs.core.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sean sean.snow@live.com
 * @date 2015/12/31
 */
public abstract class NettyServer<I, O> implements NettyNetwork<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 启动Netty 服务端
     *
     * @throws InterruptedException
     */
    public void start(Callback callback) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline().addLast(new MessageDecoder(getRequestClazz()));
                    channel.pipeline().addLast(new MessageEncoder(getResponseClazz()));
                    channel.pipeline().addLast(getHandler(getRequestClazz()));
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            LOGGER.info("Server start at host[{}] port[{}]", getHost(), getPort());
            ChannelFuture future = bootstrap.bind(getHost(), getPort()).sync();

            if (null != callback) {
                callback.onFinish();
            }

            future.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public abstract ChannelHandler getHandler(Class<I> requestClazz);

    public interface Callback {
        void onFinish();
    }
}
