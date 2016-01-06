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
 */
public abstract class NettyServer<I, O> implements NettyNetwork<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 启动Netty 服务端
     *
     * @param callback 服务启动回调函数
     * @throws InterruptedException InterruptedException
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
                    addHandler(channel.pipeline());
                    channel.pipeline().addLast(getMessageHandler());
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

    /**
     * 添加预处理程序
     *
     * @param pipeline ChannelPipeline
     */
    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast(new MessageDecoder(getRequestClazz()));
        pipeline.addLast(new MessageEncoder(getResponseClazz()));
    }

    public interface Callback {
        void onFinish();
    }
}
