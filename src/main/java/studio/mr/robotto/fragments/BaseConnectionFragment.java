package studio.mr.robotto.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import studio.mr.robotto.activities.DebugActivity;
import studio.mr.robotto.commons.Constants;
import studio.mr.robotto.services.DevicesServices;
import studio.mr.robotto.services.LoginServices;
import studio.mr.robotto.services.TokenAdder;
import studio.mr.robotto.services.models.DeviceData;
import studio.mr.robotto.services.models.SessionData;
import studio.mr.robotto.services.models.UserData;
import studio.mr.robotto.utils.Devices;

/**
 * Created by aaron on 06/09/2015.
 */
public class BaseConnectionFragment extends Fragment {

    protected SessionData getStudioSessionData() {
        SharedPreferences preferences = getSharedPreferences();
        Gson gson = new Gson();
        String sessionStr = preferences.getString(Constants.SESSION_KEY, null);
        if (sessionStr == null) {
            return null;
        }
        return gson.fromJson(sessionStr, SessionData.class);
    }

    protected DeviceData getStudioDeviceData() {
        SharedPreferences preferences = getSharedPreferences();
        Gson gson = new Gson();
        String deviceStr = preferences.getString(Constants.DEVICE_KEY, null);
        if (deviceStr == null) {
            return null;
        }
        return gson.fromJson(deviceStr, DeviceData.class);
    }

    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    protected LoginServices createLoginService(String url) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();

        return restAdapter.create(LoginServices.class);
    }

    protected DevicesServices createDevicesService(SessionData sessionData) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(sessionData.getUrl())
                .setRequestInterceptor(new TokenAdder(sessionData.getToken()))
                .build();

        return restAdapter.create(DevicesServices.class);
    }

    protected void loginUser(LoginServices loginServices, final String url, String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        loginServices.loginUser(params, new Callback<UserData>() {
            @Override
            public void success(UserData userData, Response response) {
                SessionData sessionData = new SessionData(url, userData.getToken());
                Gson gson = new Gson();
                String jsonSession = gson.toJson(sessionData);
                SharedPreferences.Editor editor = getSharedPreferences().edit();
                editor.putString(Constants.SESSION_KEY, jsonSession);
                editor.apply();
                DevicesServices devicesServices = createDevicesService(sessionData);
                registerDevice(devicesServices);
            }

            @Override
            public void failure(RetrofitError error) {
                logError(error);
            }
        });
    }

    protected void registerDevice(final DevicesServices devicesServices) {
        HashMap<String, String> params = new HashMap<>();
        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String name = Devices.getDeviceName();
        params.put("name", name);
        params.put("android_id", deviceId);
        devicesServices.registerDevice(params, new Callback<DeviceData>() {
            @Override
            public void success(DeviceData deviceData, Response response) {
                Gson gson = new Gson();
                String jsonDevice = gson.toJson(deviceData);
                SharedPreferences.Editor editor = getSharedPreferences().edit();
                editor.putString(Constants.DEVICE_KEY, jsonDevice);
                editor.apply();
                connectStudio(devicesServices, deviceData);
            }

            @Override
            public void failure(RetrofitError error) {
                logError(error);
            }
        });
    }

    protected void connectStudio(DevicesServices devicesServices, DeviceData device) {
        final Context context = getActivity();
        if (devicesServices == null || device == null) {
            Toast.makeText(context, "Device not registered", Toast.LENGTH_LONG).show();
            return;
        }
        devicesServices.connectDevice(device.getId(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Intent intent = new Intent(context, DebugActivity.class);
                startActivity(intent);
                //Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                logError(error);
            }
        });
    }

    private void logError(RetrofitError error) {
        Context context = getActivity();
        try {
            String message = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            Log.e("failure", message);
            return;
        } catch (NullPointerException ignored) {

        }
        try {
            String message = error.getMessage();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            Log.e("failure", message);
        } catch (NullPointerException ignored) {

        }
    }
}
