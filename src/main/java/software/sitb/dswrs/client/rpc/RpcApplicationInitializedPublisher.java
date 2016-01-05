package software.sitb.dswrs.client.rpc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import software.sitb.dswrs.core.ServiceDiscovery;

import java.io.IOException;
import java.util.*;

/**
 * 创建RPC Bean 的 代理Bean
 *
 * @author Sean sean.snow@live.com
 */
public class RpcApplicationInitializedPublisher implements BeanPostProcessor, Ordered, SmartInitializingSingleton {

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    private Map<String, String> rpcBeanInfo;

    public RpcApplicationInitializedPublisher(String[] rpcBeanInfo) {
        this.rpcBeanInfo = new HashMap<>();
        for (String beanInfo : rpcBeanInfo) {
            String[] info = beanInfo.split("________");
            this.rpcBeanInfo.put(info[0], info[1]);

        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (null != this.rpcBeanInfo && this.rpcBeanInfo.containsKey(beanName)) {
            //创建代理Bean返回
            RpcBeanProxyHelper helper = new RpcBeanProxyHelper();
            helper.setServiceDiscovery(serviceDiscovery);
            try {
                Object rpcBean = helper.createBean(Class.forName(this.rpcBeanInfo.get(beanName)));
                this.rpcBeanInfo.remove(beanName);
                return rpcBean;
            } catch (ClassNotFoundException e) {
                return bean;
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Assert.state(this.rpcBeanInfo.isEmpty(), "服务RPC Bean没有注册完成.");
    }

    static class Registrar implements ImportBeanDefinitionRegistrar {

        private static final String BEAN_NAME = "rpcApplicationInitializedPublisher";

        private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";

        private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
            Set<String> rpcBeanInfo = registerRpcBeanDefinitions(packagesToScan, registry);

            if (registry.containsBeanDefinition(BEAN_NAME)) {
                BeanDefinition definition = registry.getBeanDefinition(BEAN_NAME);
                ConstructorArgumentValues.ValueHolder constructorArguments = definition.getConstructorArgumentValues().getGenericArgumentValue(String[].class);
                Set<String> mergedPackages = new LinkedHashSet<>();
                mergedPackages.addAll(Arrays.asList((String[]) constructorArguments.getValue()));
                mergedPackages.addAll(rpcBeanInfo);
                constructorArguments.setValue(toArray(mergedPackages));
            } else {
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(RpcApplicationInitializedPublisher.class);
                beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(toArray(rpcBeanInfo));
                beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
            }
        }

        private String[] toArray(Set<String> set) {
            return set.toArray(new String[set.size()]);
        }

        private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes
                    .fromMap(metadata.getAnnotationAttributes(EnableRpcClient.class.getName()));

            Class<?>[] rpcServiceBaseInterface = attributes.getClassArray("rpcServiceBaseInterface");

            Set<String> packagesToScan = new LinkedHashSet<>();
            for (Class<?> basePackageClass : rpcServiceBaseInterface) {
                packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
            }
            if (packagesToScan.isEmpty()) {
                return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
            }
            return packagesToScan;
        }

        private Set<String> registerRpcBeanDefinitions(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
            if (packagesToScan != null && packagesToScan.size() > 0) {
                Set<String> rpcBeanInfo = new LinkedHashSet<>(packagesToScan.size());
                AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
                for (String pkg : packagesToScan) {
                    try {
                        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                                ClassUtils.convertClassNameToResourcePath(pkg) + CLASS_RESOURCE_PATTERN;
                        Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                        for (Resource resource : resources) {
                            if (resource.isReadable()) {
                                MetadataReader reader = readerFactory.getMetadataReader(resource);
                                if (reader.getClassMetadata().isInterface()) {
                                    String className = reader.getClassMetadata().getClassName();
                                    RpcBeanProxyHelper helper = new RpcBeanProxyHelper();
                                    Object bean = helper.createBean(Class.forName(className));
                                    GenericBeanDefinition definition = new GenericBeanDefinition();
                                    definition.setBeanClass(bean.getClass());
                                    definition.setAutowireCandidate(true);
                                    String beanName = generator.generateBeanName(definition, registry);
                                    registry.registerBeanDefinition(beanName, definition);
                                    rpcBeanInfo.add(beanName + "________" + className);
                                }
                            }
                        }
                        return rpcBeanInfo;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }

            return Collections.emptySet();
        }

    }
}
