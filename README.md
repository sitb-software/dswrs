# 分布式RPC服务框架
与Spring Boot 完美结合,使用Netty作为底层数据通信.可选的服务注册中心(ZooKeeper)



# 如何使用

## 引入依赖

### Maven

### Gradle


## 服务端

    /**
     * 继承RpcServer
     */
    @EnableRpcServer            //添加自动配置注解
    @SpringBootApplication
    public class MyAppServer extends RpcServer{
         public static void main(String[] args) throws InterruptedException {
            SpringApplication.run(MyAppServer.class, args);
         }
         @Override
         public void serverStartCallback() {
            // server start finish.
            // To do something.
         }

    }

``application.properties``

    # listen host
    dswrs.host=127.0.0.1
    # listen port
    dswrs.port=9000

    # default true
    dswrs.zoo-keeper.enabled=true
    dswrs.zoo-keeper.connect-string=127.0.0.1:2181
    dswrs.zoo-keeper.session-timeout=5000
    # zookeeper server path
    dswrs.zoo-keeper.registry-path=/my-server
    # zookeeper server child node path
    # └── /
    #   └── /my-server
    #       └── /sh-01
    #       └── /sh-02
    #       └── /sh-03
    #       └── /sh-03
    dswrs.zoo-keeper.node-path=/sh-01


## 客户端

    @SpringBootApplication
    @EnableRpcClient(rpcServiceBaseInterface = {ServiceBase.class})
    public class MyAppClient implements CommandLineRunner{

        @Autowired
        private Service1 service1;

        @Autowired
        private Service2 service2;

        public static void main(String[] args) {
            SpringApplication.run(OnlineApplication.class, args);
        }

        @Override
        public void run(String... args) throws Exception {
            System.out.println(service1);
            System.out.println(service2);
        }
    }

``application.properties``

    dswrs.zoo-keeper.connect-string=127.0.0.1:2181
    dswrs.zoo-keeper.session-timeout=5000
    dswrs.zoo-keeper.registry-path=/my-server
