package com.peknight.common.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 输入调度
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/8/10.
 */
public interface InputDispatcher {

    Logger LOGGER = LoggerFactory.getLogger(InputDispatcher.class);

    default String dispatch(String input) {
        LOGGER.info("[Input] {}", input);
        return input;
    }
}
