package studio.mr.robotto.services;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import studio.mr.robotto.services.models.DeviceData;

/**
 * Created by aaron on 27/08/2015.
 */
public interface MainDevicesServices {
    @POST("/devices/")
    void registerDevice(@Body HashMap<String, String> params, Callback<DeviceData> callback);

    @GET("/devices/{id}/connect/")
    void connectDevice(@Path("id") int deviceId, Callback<String> callback);
}
