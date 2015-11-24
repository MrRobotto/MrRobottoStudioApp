package studio.mr.robotto.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import studio.mr.robotto.R;
import studio.mr.robotto.commons.Constants;
import studio.mr.robotto.fragments.PagesAdapter;
import studio.mr.robotto.services.DevicesServices;
import studio.mr.robotto.services.TokenAdder;
import studio.mr.robotto.services.models.DeviceData;
import studio.mr.robotto.services.models.SessionData;
import studio.mr.robotto.utils.Devices;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    private DevicesServices mDevicesServices;
    private DeviceData mDevice;
    private SessionData mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagesAdapter(getSupportFragmentManager(), LoginActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
