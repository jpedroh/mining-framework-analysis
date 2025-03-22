package com.github.scribejava.apis.service;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.AbstractRequest;
import com.github.scribejava.core.model.AccessToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth20Service;

public class TutByOAuthServiceImpl extends OAuth20Service {

    public TutByOAuthServiceImpl(final DefaultApi20 api, final OAuthConfig config) {
        super(api, config);
    }

    @Override
<<<<<<< /usr/src/app/output/scribejava/scribejava/b4e5895a364746923caa75cfbcaf05772b9fa927/scribejava-apis/src/main/java/com/github/scribejava/apis/service/TutByOAuthServiceImpl.java/left.java
    public void signRequest(AccessToken accessToken, final AbstractRequest request) {
||||||| /usr/src/app/output/scribejava/scribejava/b4e5895a364746923caa75cfbcaf05772b9fa927/scribejava-apis/src/main/java/com/github/scribejava/apis/service/TutByOAuthServiceImpl.java/base.java
    public void signRequest(Token accessToken, final AbstractRequest request) {
=======
    public void signRequest(final Token accessToken, final AbstractRequest request) {
>>>>>>> /usr/src/app/output/scribejava/scribejava/b4e5895a364746923caa75cfbcaf05772b9fa927/scribejava-apis/src/main/java/com/github/scribejava/apis/service/TutByOAuthServiceImpl.java/right.java
        request.addQuerystringParameter(OAuthConstants.TOKEN, accessToken.getToken());
    }

}
