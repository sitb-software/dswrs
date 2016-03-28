package software.sitb.dswrs.core.netty;

import io.netty.channel.ChannelPipeline;

/**
 * netty网络服务接口定义
 *
 * @author Sean sean.snow@live.com
 */
public interface NettyNetwork {

    int getPort();

    String getHost();

    /**
     * 添加额外的消息编码器
     *
     * @param pipeline 管道
     */
    void addHandler(ChannelPipeline pipeline);


}
