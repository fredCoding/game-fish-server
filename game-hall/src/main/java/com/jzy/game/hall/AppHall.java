package com.jzy.game.hall;

import java.io.File;

import com.jzy.game.engine.util.ProjectNameMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jzy.game.engine.redis.jedis.JedisManager;
import com.jzy.game.engine.redis.redisson.RedissonManager;
import com.jzy.game.engine.script.ScriptManager;
import com.jzy.game.hall.manager.MongoManager;
import com.jzy.game.hall.server.HallServer;

/**
 * 大厅启动类
 *
 * @author JiangZhiYong
 * @QQ 359135103 2017年6月28日 上午11:30:49
 */
public final class AppHall {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppHall.class);
    private static String configPath;
    protected static JedisManager redisManager;
    private static HallServer bydrServer;

    private AppHall() {
    }

    public static void main(String[] args) {
        initConfigPath();
        // redis
        redisManager = new JedisManager(configPath);
//		RedissonManager.connectRedis(configPath);

        // 创建mongodb连接
        MongoManager.getInstance().createConnect(configPath);

        // 加载脚本
        ScriptManager.getInstance().init(str -> System.exit(0));

        // 启动通信连接
        bydrServer = new HallServer(configPath);
        new Thread(bydrServer).start();
    }

    private static void initConfigPath() {
        File file = new File(System.getProperty("user.dir"));
        String name = "game-hall";
        ProjectNameMapUtil.setName(name);
        ScriptManager scriptManager = new ScriptManager();

        if ("target".equals(file.getName())) {
            configPath = file.getPath() + File.separatorChar + name + File.separator + "config";
        } else {
            configPath = file.getPath() + File.separatorChar + name + File.separator + "target" + File.separatorChar + "config";
        }
        LOGGER.info("配置路径为：" + configPath);
    }

    public static HallServer getBydrServer() {
        return bydrServer;
    }

}
