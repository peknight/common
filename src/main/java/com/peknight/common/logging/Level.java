package com.peknight.common.logging;

import static com.peknight.common.logging.EventConstants.DEBUG_INT;
import static com.peknight.common.logging.EventConstants.ERROR_INT;
import static com.peknight.common.logging.EventConstants.INFO_INT;
import static com.peknight.common.logging.EventConstants.OFF_INT;
import static com.peknight.common.logging.EventConstants.TRACE_INT;
import static com.peknight.common.logging.EventConstants.WARN_INT;

/**
 * @author PeKnight
 *
 * Created by PeKnight on 2017/9/25.
 */
public enum Level {

    ERROR(ERROR_INT, "ERROR"),
    WARN(WARN_INT, "WARN"),
    INFO(INFO_INT, "INFO"),
    DEBUG(DEBUG_INT, "DEBUG"),
    TRACE(TRACE_INT, "TRACE"),
    OFF(OFF_INT, "OFF");

    private int levelInt;
    private String levelStr;

    Level(int i, String s) {
        levelInt = i;
        levelStr = s;
    }

    public int toInt() {
        return levelInt;
    }

    /**
     * Returns the string representation of this Level.
     */
    public String toString() {
        return levelStr;
    }

}
