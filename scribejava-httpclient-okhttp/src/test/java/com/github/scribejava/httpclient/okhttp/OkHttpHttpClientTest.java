package com.github.scribejava.httpclient.okhttp;

import com.github.scribejava.core.AbstractClientTest;
import com.github.scribejava.core.httpclient.HttpClient;
import okhttp3.OkHttpClient;

public class OkHttpHttpClientTest extends AbstractClientTest {
<<<<<<< /usr/src/app/output/scribejava/scribejava/2f5d9b10ee3604387e16d355cfb95af096d3c51c/scribejava-httpclient-okhttp/src/test/java/com/github/scribejava/httpclient/okhttp/OkHttpHttpClientTest.java/left.java
    private OAuthService oAuthService;
||||||| /usr/src/app/output/scribejava/scribejava/2f5d9b10ee3604387e16d355cfb95af096d3c51c/scribejava-httpclient-okhttp/src/test/java/com/github/scribejava/httpclient/okhttp/OkHttpHttpClientTest.java/base.java
    private OAuthService<?> oAuthService;
=======
>>>>>>> /usr/src/app/output/scribejava/scribejava/2f5d9b10ee3604387e16d355cfb95af096d3c51c/scribejava-httpclient-okhttp/src/test/java/com/github/scribejava/httpclient/okhttp/OkHttpHttpClientTest.java/right.java

    @Override
    protected HttpClient createNewClient() {
        return new OkHttpHttpClient(new OkHttpClient());
    }
}
