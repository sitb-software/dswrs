package software.sitb.dswrs.core.netty;

import io.netty.channel.ChannelPipeline;

/**
 * @author 田尘殇Sean sean.snow@live.com
 */
public abstract class ObjectServer<I, O> extends NettyServer {


    /**
     * 添加额外的消息编码器
     *
     * @param pipeline 管道
     */
    @Override
    public void addHandler(ChannelPipeline pipeline) {
        pipeline.addLast(new MessageDecoder(getRequestClazz()));
        pipeline.addLast(new MessageEncoder(getResponseClazz()));
        addMessageHandler(pipeline);
    }

    /**
     * 添加数据处理通道
     *
     * @param pipeline 管道
     */
    public abstract void addMessageHandler(ChannelPipeline pipeline);


    /**
     * @return 获取接收对象的Class对象
     */
    protected abstract Class<I> getRequestClazz();

    /**
     * @return 获取响应对象的Class 对象
     */
    protected abstract Class<O> getResponseClazz();
}
