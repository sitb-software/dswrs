package software.sitb.dswrs.core.netty;

/**
 * @author Sean sean.snow@live.com
 * @date 2015/12/31
 */
public interface NettyNetwork<I, O> {

    int getPort();

    String getHost();

    Class<I> getRequestClazz();

    Class<O> getResponseClazz();
}
