package net.helpscout.api.utils;

import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ivan
 * Date: 28.04.16
 * Time: 0:29
 */
public class ParamsUtils {

    @SneakyThrows
    public static String setParams(String url, Map<String, String> params) {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            for (String key : params.keySet()) {
                if (sb.indexOf("?") > 0) {
                    sb.append("&");
                } else {
                    sb.append("?");
                }
                String encodedParameter = URLEncoder.encode(params.get(key), "UTF-8");
                sb.append(key).append("=").append(encodedParameter);
            }
            return sb.toString();
        }
        return url;
    }

    public static String setFields(String url, List<String> fields) {
        if (fields != null && fields.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf("?") > 0) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            sb.append("fields=");

            String sep = "";
            for (String field : fields) {
                sb.append(sep).append(field);
                sep = ",";
            }
            url = sb.toString();
        }
        return url;
    }

    public static Map<String, String> getCustomerSearchParams(String email, String firstName, String lastName, Integer page) {
        Map<String, String> params = new HashMap<String, String>();
        if (email != null && email.trim().length() > 0) {
            params.put("email", email.trim().toLowerCase());
        }

        if (firstName != null && firstName.trim().length() > 0) {
            params.put("firstName", firstName.trim());
        }

        if (lastName != null && lastName.trim().length() > 0) {
            params.put("lastName", lastName.trim());
        }

        if (page != null && page > 0) {
            params.put("page", String.valueOf(page));
        }
        return params;
    }
}
