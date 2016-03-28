package software.sitb.dswrs.core.netty;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

/**
 * @author 田尘殇Sean sean.snow@live.com
 */
public abstract class AbstractNettyClient<I, O> extends NettyClient<I, O> {


    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast(new ReadTimeoutHandler(getReadTimeout()));
        pipeline.addLast(new WriteTimeoutHandler(getWriteTimeout()));
        addMessageHandler(pipeline);
    }


    /**
     * 添加用户使用的处理器
     *
     * @param pipeline 管道
     */
    public abstract void addMessageHandler(ChannelPipeline pipeline);


    /**
     * 读取数据超时时间
     * 单位:秒
     *
     * @return 超时时间
     */
    public int getReadTimeout() {
        return 0;
    }

    /**
     * 写入数据超时时间
     *
     * @return 超时时间
     */
    public int getWriteTimeout() {
        return 0;
    }
}
