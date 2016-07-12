package software.sitb.dswrs.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.sitb.dswrs.core.utils.SerializationUtil;

/**
 * 编码器，把Object对象转换为二进制数据
 * date 2015-4-10
 * time 上午10:28:59
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEncoder.class);

    private Class<?> genericClass;

    public MessageEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (genericClass.isInstance(msg)) {
            LOGGER.debug("write data = [{}]", msg);
            byte[] data = SerializationUtil.serialize(msg);
            LOGGER.debug("write data length = [{}]", data.length);
            out.writeInt(data.length);
            out.writeBytes(data);
        } else {
            ctx.close();
        }
    }


}
