package net.helpscout.api;

import lombok.Getter;

/**
 * @Author: ivan
 * Date: 22.08.16
 * Time: 23:19
 */
public class HTTPMethodWrapper {

    private final static String METHOD_GET = "GET";
    private final static String METHOD_POST = "POST";
    private final static String METHOD_PUT = "PUT";
    private final static String METHOD_DELETE = "DELETE";

    @Getter
    private String apiKey;
    @Getter
    private String baseUrl;

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> T doPost(String url, String requestBody, int expectedCode, ResultExtractor<T> extractor) throws ApiException {

        try (HTTPConnectionWrapper conn = new HTTPConnectionWrapper(apiKey, baseUrl + url, METHOD_POST, expectedCode, requestBody)) {
            return extractor.extract(conn);
        } catch(ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void doPut(String url, String requestBody, int expectedCode) throws ApiException {

        try (HTTPConnectionWrapper conn = new HTTPConnectionWrapper(apiKey, baseUrl + url, METHOD_PUT, expectedCode, requestBody)) {
        } catch(ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String doGet(String url, int expectedCode) throws ApiException {
        String response;
        try (HTTPConnectionWrapper conn = new HTTPConnectionWrapper(apiKey, baseUrl + url, METHOD_GET, expectedCode)) {
            response = conn.getResponse();
        } catch(ApiException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public void doDelete(String url, int expectedCode) throws ApiException {
        try (HTTPConnectionWrapper conn = new HTTPConnectionWrapper(apiKey, baseUrl + url, METHOD_DELETE, expectedCode)) {
        } catch(ApiException e) {
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
