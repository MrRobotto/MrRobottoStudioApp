package studio.mr.robotto.services;

import retrofit.RequestInterceptor;

/**
 * Created by aaron on 27/08/2015.
 */
public class TokenAdder implements RequestInterceptor {
    private String mToken;

    public TokenAdder(String token) {
        mToken = token;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Authorization", " Token " + mToken);
        request.addHeader("Content-Type", "application/json");
    }
}
