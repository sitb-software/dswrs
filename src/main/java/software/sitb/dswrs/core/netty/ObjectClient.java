package software.sitb.dswrs.core.netty;

import io.netty.channel.ChannelPipeline;

/**
 * Netty 对象传输客户端
 *
 * @author 田尘殇Sean sean.snow@live.com
 */
public abstract class ObjectClient<I, O> extends AbstractNettyClient<I, O> {

    /**
     * 添加用户使用的处理器
     *
     * @param pipeline 管道
     */
    @Override
    public void addMessageHandler(ChannelPipeline pipeline) {
        pipeline.addLast(new MessageDecoder(getResponseClazz()));
        pipeline.addLast(new MessageEncoder(getRequestClazz()));
    }

    /**
     * @return 获取发送对象的Class对象
     */
    protected abstract Class<I> getRequestClazz();

    /**
     * @return 获取响应对象的Class 对象
     */
    protected abstract Class<O> getResponseClazz();


}
