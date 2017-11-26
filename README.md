# Issue
Spring cloud defines a **[Bootstrap phase](http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_the_bootstrap_application_context)** wherein it creates a context from configurations in boostrap.properties (or .yml) files. However it fails if Jasypt is used to decrypt encrypted text in boostrap.properties. 
<p>
Consider the following scenario where the [config-server itself is bootstrapped](https://cloud.spring.io/spring-cloud-config/multi/multi__embedding_the_config_server.html) - so that the config-server is trying to pull configurations from config-repo during the bootstrap phase. However the password for config-repo is encrypted with Jasypt, but Jasypt decrypts the properties at a later stage only (after boostrap phase).

```
spring.cloud.config.server.bootstrap=true
spring.application.name=config-server

spring.cloud.config.server.git.uri=https://bitbucket.org/fahim-experiment/config-repo

spring.cloud.config.server.git.username=myusername@somewhere.com
spring.cloud.config.server.git.password=ENC(GarNzpLDDoIJ3Y5lDabAllQuV74tNFbj)

jasypt.encryptor.algorithm=PBEWithMD5AndDES
jasypt.encryptor.password=passphrase

```

And this will result in following exception message i.e. **not authorized**.

```
Caused by: org.eclipse.jgit.errors.TransportException: https://bitbucket.org/fahim-experiment/config-repo: not authorized
```



## Analysis
spring-cloud-context defines following bootstrap configuration in it's spring.factories.

```
org.springframework.cloud.bootstrap.BootstrapConfiguration=\
org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
```
`PropertySourceBootstrapConfiguration` implements `ApplicationContextInitializer`. During it's `PropertySourceBootstrapConfiguration#initialize()` method it's trying to pull configurations from config-repo. Also it's having an order of ` Ordered.HIGHEST_PRECEDENCE + 10`. Even though `PropertySourceBootstrapConfiguration#initialize()` is invoked during bootstrap phase, it will be after any `postProcessBeanFactory()` of `BeanFactoryPostProcessors` defined in bootstrap phase only.

Jasypt defines an autoconfiguration and and a listener.

```
org.springframework.context.ApplicationListener=\
com.ulisesbocchio.jasyptspringboot.configuration.EnableEncryptablePropertiesBeanFactoryPostProcessor

org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.ulisesbocchio.jasyptspringboot.JasyptSpringBootAutoConfiguration
```


# Solution
As a solution, Jasypt can be bootstrapped in a spring cloud based application, so that encrypted text will have decrypted by the time config-server is trying to pull from config-repo. However it's not required to redefine `EnableEncryptablePropertiesBeanFactoryPostProcessor` with a higher priority because context is refreshed after post-processing bean factories only. i.e. `PropertySourceBootstrapConfiguration#initialize()` is invoked after any `EnableEncryptablePropertiesBeanFactoryPostProcessor#postProcessBeanFactory()` only.




