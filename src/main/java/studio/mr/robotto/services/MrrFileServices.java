package studio.mr.robotto.services;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Streaming;
import studio.mr.robotto.services.models.MrrFileData;

/**
 * Created by aaron on 28/08/2015.
 */
public interface MrrFileServices {

    @GET("/api/mrrfiles/selected")
    void getSelectedMrr(Callback<MrrFileData> callback);

    @GET("/api/mrrfiles/{id}/download")
    @Streaming
    void downloadSelected(@Path("id") int id, Callback<Response> callback);
}
