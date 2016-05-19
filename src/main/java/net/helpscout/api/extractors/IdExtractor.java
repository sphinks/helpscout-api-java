package net.helpscout.api.extractors;

import net.helpscout.api.ResultExtractor;

import java.net.HttpURLConnection;

/**
 * @Author: ivan
 * Date: 20.05.16
 * Time: 1:15
 */
public class IdExtractor implements ResultExtractor<Long> {

    public Long extract(HttpURLConnection conn) {
        String location = conn.getHeaderField("LOCATION");
        if (location != null && location.trim().length() > 0) {
            return new Long(location.substring(
                    location.lastIndexOf("/") + 1,
                    location.lastIndexOf(".")));
        } else {
            return null;
        }
    }
}
