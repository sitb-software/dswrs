package software.sitb.dswrs.core.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

/**
 * netty网络服务接口定义
 *
 * @author Sean sean.snow@live.com
 */
public interface NettyNetwork<I, O> {

    int getPort();

    String getHost();

    Class<I> getRequestClazz();

    Class<O> getResponseClazz();

    /**
     * 添加额外的消息编码器
     *
     * @param pipeline 管道
     */
    void addHandler(ChannelPipeline pipeline);

    /**
     * 获取一个消息处理器
     *
     * @return 消息处理器
     */
    ChannelHandler getMessageHandler();
}
