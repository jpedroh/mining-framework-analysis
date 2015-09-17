package io.swagger.client.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class ApiKeyAuthorization implements Interceptor {
    private final String location;
    private final String paramName;

    private String apiKey;

    public ApiKeyAuthorization(String location, String paramName) {
        this.location = location;
        this.paramName = paramName;
    }

    public String getLocation() {
        return location;
    }

    public String getParamName() {
        return paramName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String paramValue;
        Request request = chain.request();

        if (location == "query") {
            String newQuery = request.uri().getQuery();
            paramValue = paramName + "=" + apiKey;
            if (newQuery == null) {
                newQuery = paramValue;
            } else {
                newQuery += "&" + paramValue;  
            }

            URI newUri;
            try {
                newUri = new URI(request.uri().getScheme(), request.uri().getAuthority(),
                        request.uri().getPath(), newQuery, request.uri().getFragment());
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }

            request = request.newBuilder().url(newUri.toURL()).build();
        } else if (location == "header") {
            request = request.newBuilder()
                    .addHeader(paramName, apiKey)
                    .build();
        }
        return chain.proceed(request);
    }
}