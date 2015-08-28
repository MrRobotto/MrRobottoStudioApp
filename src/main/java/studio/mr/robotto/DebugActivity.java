/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aar�n Negr�n, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import mr.robotto.MrRobottoEngine;
import mr.robotto.engine.ui.MrSurfaceView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import studio.mr.robotto.services.MrrFileServices;
import studio.mr.robotto.services.TokenAdder;
import studio.mr.robotto.services.models.DeviceData;
import studio.mr.robotto.services.models.MrrFileData;
import studio.mr.robotto.services.models.SessionData;


public class DebugActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int READY_REQUEST = 1;

    private ProgressBar mProgressBar;
    private MrRobottoEngine mEngine;

    private MrrFileServices mMrrFileServices;
    private SharedPreferences mPreferences;
    private MrrFileData mMrrFile;
    private DeviceData mDevice;
    private SessionData mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Button btn = (Button) findViewById(R.id.refresh_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        btn.setOnClickListener(this);

        MrSurfaceView view = (MrSurfaceView) findViewById(R.id.robotto);
        mEngine = new MrRobottoEngine(this, view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.barRefresh) {
            //mConnectionManager.requestUpdate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh_button) {
            //mConnectionManager.requestUpdate();
            requestSelectedMrr();
        }
    }

    @Override
    protected void onResume() {
        //mConnectionManager.connect();
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
    protected void onPause() {
        //mConnectionManager.disconnect();
        super.onPause();
    }

    private void createRestAdapter(SessionData sessionData) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(sessionData.getUrl())
                .setRequestInterceptor(new TokenAdder(sessionData.getToken()))
                .build();

        mMrrFileServices = restAdapter.create(MrrFileServices.class);
    }

    private void requestSelectedMrr() {
        final Context context = this;
        mMrrFileServices.getSelectedMrr(new Callback<MrrFileData>() {
            @Override
            public void success(MrrFileData mrrFileData, Response response) {
                mMrrFile = mrrFileData;
                Toast.makeText(context, mrrFileData.toString(), Toast.LENGTH_LONG).show();
                requestMrrFile();
                //requestUpdate();
            }

            @Override
            public void failure(RetrofitError error) {
                logError(context, error);
            }
        });
    }

    private void requestMrrFile() {
        final Context context = this;
        mMrrFileServices.downloadSelected(mMrrFile.getId(), new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                try {
                    InputStream in = s.getBody().in();
                    mEngine.loadSceneTreeAsync(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
