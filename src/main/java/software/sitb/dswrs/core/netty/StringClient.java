package software.sitb.dswrs.core.netty;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author 田尘殇Sean sean.snow@live.com
 */
public class StringClient extends AbstractNettyClient<String, String> {
    private String host;
    private int port;

    public StringClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    /**
     * 添加用户使用的处理器
     *
     * @param pipeline 管道
     */
    @Override
    public void addMessageHandler(ChannelPipeline pipeline) {
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
    }
}
