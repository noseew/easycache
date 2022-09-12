# EasyCache

# 简介

对远程缓存(Redis)和本地缓存(Caffeine)的封装, 实现轻量级的多级缓存框架; 

# 特点

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
EasyCacheHelper.put(namespace, cacheName, key, userDO, 100 * 1000);
UserDO userDO1 = EasyCacheHelper.get(namespace, cacheName, key, UserDO.class);
```

