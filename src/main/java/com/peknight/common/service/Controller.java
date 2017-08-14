package com.peknight.common.service;

/**
 * 服务控制器
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/10.
 */
public interface Controller {
    default void start(long millis) {};

    default void pause(long millis) {};

    default void restart(long millis) {};

    default void stop(long millis) {};

    default void initEnvironment(long millis) {};

    default void restoreEnvironment(long millis) {};
}