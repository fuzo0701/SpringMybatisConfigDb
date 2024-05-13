package kr.hrd.wallet.common.util;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class HttpClient {


    static final MediaType JSON_TYPE = MediaType.parse("application/json");
    static final int TIME_OUT = 3600; // 1 hour

    final OkHttpClient client = new OkHttpClient.Builder()
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .callTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build();

    public String requestGET(String url, String token) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        request = addToken(request, token);
        return raw(request);
    }

    public String requestGET(String url, String token, Map<String, String>headerMap) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        request = addToken(request, token);
        request = addHeader(request, headerMap);
        return raw(request);
    }

    public String requestGET(String url, String token, String version, String clientId, String apiKey) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        request = addToken(request, token);
        request = addHeader(request, "gwVersion", version);
        request = addHeader(request, "gwClientID", clientId);
        request = addHeader(request, "apiKey", apiKey);
        return raw(request);
    }

    public String requestPOST(String url, String token, String json) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, JSON_TYPE))
                .build();
        request = addToken(request, token);
        return raw(request);
    }

    public String requestPOST(String url, String token, String json, String version, String clientId, String apiKey) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, JSON_TYPE))
                .build();
        request = addToken(request, token);
        request = addHeader(request, "gwVersion", version);
        request = addHeader(request, "gwClientID", clientId);
        request = addHeader(request, "apiKey", apiKey);
        return raw(request);
    }

    public String requestPOST(String url, String token, String json, Map<String, String>headerMap) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .post(RequestBody.create(json, JSON_TYPE))
                .build();
        request = addToken(request, token);
        request = addHeader(request, headerMap);
        return raw(request);
    }

    public String requestPOST(String url, String token, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        request = addToken(request, token);
        return raw(request);
    }

    public String requestPOSTBasicAuth(String url, String username, String password, String form) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(form, MediaType.parse("application/x-www-form-urlencoded")))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        request = addBasic(request, username, password);
        return raw(request);
    }

    public String requestPUT(String url, String token, String json) {
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(json, JSON_TYPE))
                .build();
        request = addToken(request, token);
        return raw(request);
    }

    public String requestPUT(String url, String token, String json, Map<String, String>headerMap) {
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(json, JSON_TYPE))
                .build();
        request = addToken(request, token);
        request = addHeader(request, headerMap);
        return raw(request);
    }

    public String requestDELETE(String url, String token) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        request = addToken(request, token);
        return raw(request);
    }

    public String requestDELETE(String url, String token, Map<String, String>headerMap) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        request = addToken(request, token);
        request = addHeader(request, headerMap);
        return raw(request);
    }

    public String requestPATCH(String url, String token, String json) {
        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(json, JSON_TYPE))
                .build();
        request = addToken(request, token);
        return raw(request);
    }

    String raw(Request req) {
        String result = null;
        Call newCall = client.newCall(req);
        try (Response resp = newCall.execute()) {
            if (resp.isSuccessful() && resp.body() != null) {
                byte[] data = resp.body().bytes();
                MediaType contentType = resp.body().contentType();
                if (contentType != null && contentType.toString().toLowerCase().contains("euc-kr")) {
                    result = new String(data, "euc-kr"); //resp.body().string();
                }else {
                    result = new String(data, "utf-8"); //resp.body().string();
                }
            } else if (!resp.isSuccessful()) {
                log.error("code={} message={}", resp.code(), resp.message());
                result = JsonPath.parse("{ error : "+resp.code() +resp.message()+"}").jsonString();
            }
        } catch (IOException e) {
            result = JsonPath.parse("{ error : "+e+"}").jsonString();
        }
        return result;
    }

    Request addToken(Request pRequest, String token) {
        Request request = pRequest;
        if (token != null && !token.equals(""))
            request = request.newBuilder().addHeader("Authorization", "Bearer " + token).build();
        return request;
    }

    Request addHeader(Request pRequest, String header, String value) {
        Request request = pRequest;
        if (header != null && !header.equals("") && value != null && !value.equals(""))
            request = request.newBuilder().addHeader(header, value).build();
        return request;
    }

    Request addHeader(Request pRequest, Map<String, String>headerMap) {
        Request request = pRequest;
        Iterator<String> it = headerMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = headerMap.get(key);
            request = request.newBuilder().addHeader(key, value).build();
        }
        return request;
    }

    static Request addBasic(Request pRequest, String username, String password) {
        Request request = pRequest;
        if (username != null && !username.equals("") && password != null && !password.equals("")) {
            String credential = Credentials.basic(username, password);
            request = request.newBuilder().addHeader("Authorization", credential).build();
        }
        return request;
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    public static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }
}
