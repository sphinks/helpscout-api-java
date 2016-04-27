package net.helpscout.api.utils;

import net.helpscout.api.ApiException;
import net.helpscout.api.exception.*;
import net.helpscout.api.json.JsonFormatter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static java.net.HttpURLConnection.*;

/**
 * @Author: ivan
 * Date: 26.04.16
 * Time: 23:37
 */
public class HTTPConnectionUtils {

    public static final int HTTP_REQUESTS_LIMIT = 429;

    public static HttpURLConnection getConnection(String apiKey, String url, String method, boolean hasRequestBody) throws Exception {

        URL aUrl = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) aUrl.openConnection();

        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod(method);

        if (hasRequestBody) {
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
        }
        conn.setRequestProperty("Authorization", "Basic " + EncodeUtils.getEncoded(apiKey + ":x"));
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        return conn;
    }

    public static void checkStatusCode(HttpURLConnection conn, int expectedCode) throws ApiException, IOException {
        int code = conn.getResponseCode();

        if (code == expectedCode) {
            return;
        }

        switch(code) {
            case HTTP_BAD_REQUEST:
                String details = getDetailedErrorMessage(conn);
                throw new InvalidFormatException("The request was not formatted correctly", details);
            case HTTP_UNAUTHORIZED:
                throw new InvalidApiKeyException("Invalid API key");
            case HTTP_PAYMENT_REQUIRED:
                throw new ApiKeySuspendedException("API key suspended");
            case HTTP_FORBIDDEN:
                throw new AccessDeniedException("Access denied");
            case HTTP_NOT_FOUND:
                throw new NotFoundException("Resource not found");
            case HTTP_BAD_METHOD:
                throw new InvalidMethodException("Invalid method type");
            case HTTP_REQUESTS_LIMIT:
                throw new ThrottleRateException("Throttle limit reached. Too many requests");
            case HTTP_INTERNAL_ERROR:
                throw new ServerException("Application error or server error", getDetailedErrorMessage(conn));
            case HTTP_UNAVAILABLE:
                throw new ServiceUnavailableException("Service Temporarily Unavailable");
            default:
                throw new ApiException("Unknown API exception");
        }
    }

    public static String getDetailedErrorMessage(HttpURLConnection conn) throws IOException {
        InputStream is = conn.getErrorStream();
        String json = "";
        if (is != null) {
            json = IOUtils.toString(is, "UTF-8");
        }

        return StringUtils.isNotEmpty(json) ? new JsonFormatter().format(json) : null;
    }

    public static void close(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static String getResponse(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    public static void close(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static InputStream getInputStream(HttpURLConnection conn) throws IOException {
        String encoding = conn.getContentEncoding();

        InputStream inputStream = null;

        //create the appropriate stream wrapper based on
        //the encoding type
        if (encoding != null) {
            if (encoding.equalsIgnoreCase("gzip")) {
                inputStream = new GZIPInputStream(conn.getInputStream());
            } else if (encoding.equalsIgnoreCase("deflate")) {
                inputStream = new InflaterInputStream(conn.getInputStream(), new Inflater(true));
            }
        }
        if (inputStream == null) {
            inputStream = conn.getInputStream();
        }
        return inputStream;
    }

}
