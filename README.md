# EasyCache

# 简介

对远程缓存(Redis)和本地缓存(Caffeine)的封装, 实现轻量级的多级缓存框架; 

# 使用示例

## 快速入门

### 1. 导入依赖和配置

```xml
<dependency>
    <groupId>org.galileo.easycache</groupId>
    <artifactId>easycache-cli-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

```properties
# SpringDataRedis 配置
spring.redis.host=127.0.0.1
spring.redis.port=6379
#spring.redis.password=
spring.redis.database=1
spring.redis.timeout=10s
spring.redis.jedis.pool.min-idle=4
spring.redis.jedis.pool.max-idle=20
spring.redis.jedis.pool.max-active=100
spring.redis.jedis.pool.max-wait=10000

# easyCache 配置
easycache.enabled=true
easycache.ns.dft.type=both
easycache.ns.dft.remote.type=redis#spring.redis-org.springframework.boot.autoconfigure.data.redis.RedisProperties
```

### 2. 使用注解

```java
    @Cached(cacheName = "user", key = "#id")
    public UserDO get(int id) {
        return userDAO.selectById(id);
    }

    @CacheRemove(cacheName = "user", key = "#id")
    public void del(int id) {
        userDAO.deleteById(id);
    }

    @CacheUpdate(cacheName = "user", key = "#userDO.id", consistency = ConsistencyType.EVENTUAL)
    public UserDO update(UserDO userDO) {
        if (userDAO.updateById(userDO) > 0) {
            return userDO;
        }
        return null;
    }
```

### 3. 使用API

```java
EasyCacheUtils.put(namespace, cacheName, key, userDO, 100 * 1000);
UserDO userDO1 = EasyCacheUtils.get(namespace, cacheName, key, UserDO.class);
```



# 特点

- 支持注解+API
- 支持多级缓存(默认远程Redis, 本地Caffeine)
- 支持bigkey检测和过滤
- 远程缓存失败, 可容错降级成本地缓存
- 支持多namespace, 多组远程+本地的配置
- 支持配置动态更新生效(绝大多数配置, 在SpringCloud环境下)
- 支持缓存管理(规划中)
- 支持缓存监控(规划中)


更多支持开发中..., 欢迎参与建设

# TODO 
- [ ] 分布式锁
- [ ] redis 客户端支持
- [ ] 管理后台监控


# 打包

```
# 打包
mvn package -Dmaven.test.skip=true

# 安装到仓库
mvn install -Dmaven.test.skip=true

```

# Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 [Issue](!(https://github.com/noseew/easycache/issues)) 讨论新特性或者变更。

# Copyright and License

产品开源免费，并且将持续提供免费的社区技术支持。个人或企业内部可自由的接入和使用。如有需要可邮件联系作者免费获取项目授权。



