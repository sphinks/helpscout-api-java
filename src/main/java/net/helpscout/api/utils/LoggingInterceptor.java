package net.helpscout.api.utils;

import net.helpscout.api.ApiException;
import net.helpscout.api.exception.*;
import net.helpscout.api.json.JsonFormatter;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

/**
 * @Author: ivan
 * Date: 03.09.16
 * Time: 14:20
 */
public class LoggingInterceptor implements Interceptor {

    private static String apiKey;

    private static Logger logger = Logger.getLogger("LoggingInterceptor");

    public LoggingInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    public static void setApiKey(String apiKey) {
        LoggingInterceptor.apiKey = apiKey;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();

        Request requestWithHeaders = request.newBuilder()
                .header("Authorization", "Basic " + EncodeUtils.getEncoded(apiKey + ":x"))
                .build();

        long t1 = System.nanoTime();
        logger.info(String.format("Sending request %s on %s%n%s",
                requestWithHeaders.url(), chain.connection(), requestWithHeaders.headers()));

        Response response = chain.proceed(requestWithHeaders);
        checkStatusCode(response);

        long t2 = System.nanoTime();
        logger.info(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
//        logger.info("Response body: " + response.body().string());

        return response;
    }

    private static void checkStatusCode(Response response) throws ApiException, IOException {

        if (response.isSuccessful()) {
            return;
        }

        int code = response.code();

        switch(code) {
            case HTTP_BAD_REQUEST:
//                String details = getDetailedErrorMessage(conn);
                throw new InvalidFormatException("The request was not formatted correctly");
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
//            case HTTP_REQUESTS_LIMIT:
//                throw new ThrottleRateException("Throttle limit reached. Too many requests");
            case HTTP_INTERNAL_ERROR:
                throw new ServerException("Application error or server error");
            case HTTP_UNAVAILABLE:
                throw new ServiceUnavailableException("Service Temporarily Unavailable");
            default:
                throw new ApiException("Unknown API exception");
        }
    }

    private static String getDetailedErrorMessage(HttpURLConnection conn) throws IOException {
        InputStream is = conn.getErrorStream();
        String json = "";
        if (is != null) {
            json = IOUtils.toString(is, "UTF-8");
        }

        return StringUtils.isNotEmpty(json) ? new JsonFormatter().format(json) : null;
    }
}
