package software.sitb.dswrs.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.sitb.dswrs.core.utils.SerializationUtil;

import java.util.List;

/**
 * Desc: 消息解码器 把二进制数据转换为Object对象
 * 4个字节的数据大小
 * 得到数据大小以后再读取剩余数据，
 * 根据传入的Class解析为内部对象
 * date 2015-4-10
 * time 上午10:22:52
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    private Class<?> genericClass;

    public MessageDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in instanceof EmptyByteBuf) {
            ctx.close();
            return;
        }

        int readable = in.readableBytes();
        LOGGER.debug("readable = [{}]", readable);
        if (readable < 4) {
            return;
        }

        in.markReaderIndex();

        int dataLength = in.readInt();
        LOGGER.debug("data length = [{}]", dataLength);
        if (dataLength < 0) {
            LOGGER.debug("数据长度小于0,关闭通道");
            ctx.close();
            return;
        }

        if (in.readableBytes() < dataLength) {
            LOGGER.trace("可读数据长度小于数据长度,回滚指针");
            in.resetReaderIndex();
            return;
        }
        LOGGER.debug("read data length = [{}]", dataLength);
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, genericClass);
        LOGGER.debug("read data = [{}]", obj);
        out.add(obj);
    }

}
