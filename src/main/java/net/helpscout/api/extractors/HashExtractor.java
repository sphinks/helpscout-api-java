package net.helpscout.api.extractors;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.helpscout.api.ResultExtractor;
import net.helpscout.api.utils.HTTPConnectionUtils;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;

/**
 * @Author: ivan
 * Date: 20.05.16
 * Time: 1:16
 */
public class HashExtractor implements ResultExtractor<String> {

    public String extract(HttpURLConnection conn) {
        String hash = null;
        String response;
        try {
            response = HTTPConnectionUtils.getResponse(conn);
            LoggerFactory.getLogger(getClass()).debug("attachment: {}",
                    response);
            JsonElement obj = (new JsonParser()).parse(response);
            JsonElement item = obj.getAsJsonObject().get("item");
            hash = item.getAsJsonObject().get("hash").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            HTTPConnectionUtils.close(conn);
        }
        return hash;
    }
}
