package studio.mr.robotto.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import studio.mr.robotto.R;
import studio.mr.robotto.commons.Constants;
import studio.mr.robotto.services.DevicesServices;
import studio.mr.robotto.services.TokenAdder;
import studio.mr.robotto.services.models.DeviceData;
import studio.mr.robotto.services.models.SessionData;
import studio.mr.robotto.utils.Devices;

public class ConnectionActivity extends ActionBarActivity implements View.OnClickListener {

    private SharedPreferences mPreferences;

    private DevicesServices mDevicesServices;
    private DeviceData mDevice;
    private SessionData mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        //Button qrRegistration = (Button) findViewById(R.id.qr_register_button);
        //Button manualRegistration = (Button) findViewById(R.id.manual_register_button);
        //Button connectButton = (Button) findViewById(R.id.connect_button);
        //qrRegistration.setOnClickListener(this);
        //manualRegistration.setOnClickListener(this);
        //connectButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String deviceString = mPreferences.getString(Constants.DEVICE_KEY, null);
        String sessionString = mPreferences.getString(Constants.SESSION_KEY, null);
        if (deviceString != null && sessionString != null) {
            Gson gson = new Gson();
            mDevice = gson.fromJson(deviceString, DeviceData.class);
            mSession = gson.fromJson(sessionString, SessionData.class);
            createRestAdapter(mSession);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.connect_button) {
            connectStudio();
        } else if (v.getId() == R.id.qr_register_button) {
            startQRRegistration();
        } else if (v.getId() == R.id.manual_register_button) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        }*/
    }


    private void startQRRegistration() {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, Constants.QR_INTENT);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.QR_INTENT) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                try {
                    JSONObject o = (JSONObject) new JSONTokener(contents).nextValue();
                    String token = o.getString("token");
                    String baseUrl = o.getString("base_url");
                    int attempId = o.getInt("attemp_id");
                    SessionData sessionData = new SessionData(baseUrl, token);
                    createRestAdapter(sessionData);
                    registerDevice(sessionData, attempId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(this, contents, Toast.LENGTH_LONG).show();
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    private void createRestAdapter(SessionData sessionData) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(sessionData.getUrl())
                .setRequestInterceptor(new TokenAdder(sessionData.getToken()))
                .build();

        mDevicesServices = restAdapter.create(DevicesServices.class);
    }

    private void connectStudio() {
        final Context context = this;
        if (mDevicesServices == null || mDevice == null) {
            Toast.makeText(context, "Device not registered", Toast.LENGTH_LONG).show();
            return;
        }
        mDevicesServices.connectDevice(mDevice.getId(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Intent intent = new Intent(context, DebugActivity.class);
                startActivity(intent);
                //Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                logError(context, error);
            }
        });
    }

    private void registerDevice(final SessionData sessionData, int attempId) {
        final Context context = this;
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String name = Devices.getDeviceName();
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("android_id", deviceId);
        params.put("attemp_id", String.valueOf(attempId));

        mDevicesServices.registerDevice(params, new Callback<DeviceData>() {
            @Override
            public void success(DeviceData device, Response response) {
                mDevice = device;
                mSession = sessionData;
                Gson gson = new Gson();
                String jsonDevice = gson.toJson(mDevice);
                String jsonSession = gson.toJson(mSession);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(Constants.DEVICE_KEY, jsonDevice);
                editor.putString(Constants.SESSION_KEY, jsonSession);
                editor.commit();
                connectStudio();
            }

            @Override
            public void failure(RetrofitError error) {
                logError(context, error);
            }
        });
    }

    private void logError(Context context, RetrofitError error) {
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