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