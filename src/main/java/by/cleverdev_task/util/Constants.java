package by.cleverdev_task.util;

import lombok.experimental.UtilityClass;

/**
 * Constants used in the application.
 */
@UtilityClass
public class Constants {
    public static final String DATE_FORMAT_OLD_SYSTEM = "yyyy-MM-dd HH:mm:ss";

    public static final String IMPORT_DATE_FROM = "2010-01-01";
    public static final String IMPORT_DATE_TO = "2030-01-01";

    public static final short STATUS_ACTIVE = 200;
    public static final short STATUS_PARTIALLY_ACTIVE = 210;
    public static final short STATUS_UNDER_OBSERVATION = 230;

    public static final String API_URL = "http://localhost:8081";
}
