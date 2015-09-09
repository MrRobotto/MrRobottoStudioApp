package studio.mr.robotto.services;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import studio.mr.robotto.services.models.UserData;

/**
 * Created by aaron on 05/09/2015.
 */
public interface LoginServices {
    @POST("/api/login/")
    void loginUser(@Body HashMap<String, String> params, Callback<UserData> callback);
}
