package com.core.mall.config.cache;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 本地缓存服务
 * key = 字符串 | value = 字符串 | expire = 秒(有效时间)
 *
 * put 添加缓存
 * get 读取缓存值
 *
 * 失效策略：
 *  1. 定期删除策略：启动1个线程，每2分钟扫描一次，超时数据移除
 *  2. 懒惰淘汰策略：每次访问时校验有效性，如果失效移除
 */
public class LocalCache {
    private final static Logger logger = LoggerFactory.getLogger(LocalCache.class);

    /**
     * 启动开始后延迟5秒执行时效策略
     */
    private static final int INITIAL_DELAY_TIME = 30;
    /**
     * 执行时效策略间隔时间
     */
    private static final int PERIOD_TIME = 60;
    /**
     * 本地缓存map
     */
    private static ConcurrentHashMap<String, Cache> store;
    /**
     * 执行时效策略线程池
     */
    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /*
     * 静态代码块
     *
     * 初始化缓存map
     * 添加时效策略定时线程任务
     */
    static {
        store = new ConcurrentHashMap<>();
        executor.scheduleAtFixedRate(new Task(), INITIAL_DELAY_TIME, PERIOD_TIME, TimeUnit.SECONDS);
    }

    public static void put(String key, String value) {
        put(key, value, 0);
    }

    /**
     * 设置缓存
     * @param key 唯一key
     * @param value 值
     * @param expire 超时时间-单位(s/秒)
     */
    public static void put(String key, String value, long expire) {
        logger.debug("添加缓存，key={}, value={}, expire={}秒", key, value, expire);
        if (expire > 0) {
            store.put(key, new Cache(value, expire));
        } else {
            store.put(key, new Cache(value));
        }
    }

    public static String get(String key) {
        Cache cache = store.get(key);
        if (cache == null) {
            return null;
        }

        if (cache.getExpire() > 0 && cache.getExpire() < System.currentTimeMillis()) {
            remove(key);
            return null;
        }
        return store.get(key).getValue();
    }

    private static void remove(String key) {
        Cache cache = store.remove(key);
        logger.debug("懒惰淘汰策略: 移除超时失效数据, cache={}", cache);
    }

    private static void removeAll() {
        logger.debug("定期删除策略: 开始执行, store={}", store);
        for (String key : store.keySet()) {
            Cache cache = store.get(key);
            if (cache.getExpire() > 0 && cache.getExpire() < System.currentTimeMillis()) {
                store.remove(key);
                logger.info("定期删除策略: 移除超时失效数据, key={}, value={}, time={}", key, cache.getValue(), cache.getExpire());
            }
        }
    }

    /**
     * 定时移除时效数据任务
     */
    private static class Task implements Runnable {
        @Override
        public void run() {
            try {
                LocalCache.removeAll();
            } catch (Exception e) {
                logger.error("定期删除策略异常", e);
            }
        }
    }

    /**
     * 本地缓存对象
     */
    @Data
    private static class Cache {
        private String value;
        private long expire = 0;

        Cache(String value, long expire) {
            this.value = value;
            this.expire = System.currentTimeMillis() + expire * 1000;
        }

        Cache(String value) {
            this.value = value;
        }
    }
}
