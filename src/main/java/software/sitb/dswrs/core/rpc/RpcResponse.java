package software.sitb.dswrs.core.rpc;

/**
 * RPC响应结果
 *
 * @author Sean sean.snow@live.com
 */
public class RpcResponse implements java.io.Serializable {
    private static final long serialVersionUID = -6248397742873424100L;

    private String requestId;

    private Throwable error;

    private Object result;


    /**
     * 请求是否成功
     *
     * @return 成功状态
     */
    public boolean success() {
        return this.error == null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T) result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
