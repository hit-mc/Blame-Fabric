package com.keuin.blame.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrettyUtil {
    public static String timestampToString(long timeMillis) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(timeMillis));
    }
}
