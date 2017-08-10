package com.peknight.common.service;

/**
 * 服务控制器
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/10.
 */
public interface ServiceController {
    void start(long millis);

    void pause(long millis);

    void restart(long millis);

    void stop(long millis);

    void initEnvironment(long millis);

    void initDevelopment(long millis);

    void restoreEnvironment(long millis);

    void restoreDevelopment(long millis);
}