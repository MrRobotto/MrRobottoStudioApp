/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aar�n Negr�n, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mr.robotto.MrRobottoEngine;
import mr.robotto.utils.MrStreamReader;

/**
 * Created by aaron on 22/04/2015.
 */
public class ConnectionManager {
    public static final String UA = "MrRobottoStudio";

    private String mHost;
    private String mPort;
    private static URL sUrlRoot;
    private static URL sUrlConnect;
    private static URL sUrlDisconnect;
    private static URL sUrlUpdate;
    private static URL sNeedUpdate;
    private Activity mActivity;
    private NeedUpdateTask mUpdater;

    private MrRobottoEngine mEngine;

    public ConnectionManager(Activity activity, String host, String port) {
        mActivity = activity;
        setHostPort(host, port);
    }

    public ConnectionManager(Activity activity) {
        mActivity = activity;
    }

    public void setHostPort(String host, String port) {
        mHost = host;
        mPort = port;
        try {
            sUrlRoot = new URL("http://" + mHost + ":" + mPort+"/");
            sUrlConnect = new URL("http://" + mHost + ":" + mPort+"/android/connect");
            sUrlDisconnect = new URL("http://"+mHost+":"+mPort+"/android/disconnect");
            sUrlUpdate = new URL("http://" + mHost + ":" + mPort+"/android/update");
            sNeedUpdate = new URL("http://" + mHost + ":" + mPort+"/android/need-update");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public MrRobottoEngine getEngine() {
        return mEngine;
    }

    public void setEngine(MrRobottoEngine engine) {
        mEngine = engine;
    }

    private void setAndroidHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent", UA);
        String androidId = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        connection.setRequestProperty("Android-Id", androidId);
    }

    public void isServerActive() {
        AsyncTask<URL, Void, Integer> task = new AsyncTask<URL, Void, Integer>() {
            @Override
            protected Integer doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    return urlConnection.getResponseCode();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    cancel(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return -1;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                Toast.makeText(mActivity,"Server not active", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 200) {
                    Intent intent = new Intent(mActivity, DebugActivity.class);
                    intent.putExtra("host",mHost);
                    intent.putExtra("port",mPort);
                    mActivity.startActivityForResult(intent, ConnectActivity.CONNECT_ID);
                } else {
                    Toast.makeText(mActivity,"Bad request", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (sUrlRoot == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sUrlRoot);
    }

    public void connect() {
        AsyncTask<URL, Void, Integer> task = new AsyncTask<URL, Void, Integer>() {

            private String getPostData() throws JSONException {
                String androidId = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
                JSONObject obj = new JSONObject();
                obj.put("android_id", androidId);
                return obj.toString();
            }

            @Override
            protected Integer doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    setAndroidHeaders(urlConnection);
                    urlConnection.setRequestMethod("POST");
                    /*String data = getPostData();
                    OutputStream os = urlConnection.getOutputStream();
                    PrintWriter writer = new PrintWriter(os);
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    os.close();*/
                    urlConnection.setConnectTimeout(5000);
                    return urlConnection.getResponseCode();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    cancel(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return -1;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                Toast.makeText(mActivity,"Server not active", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 200) {
                    startNeedUpdateRequests();
                } else {
                    Toast.makeText(mActivity,"Bad request", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (sUrlConnect == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sUrlConnect);
    }

    public void disconnect() {
        AsyncTask<URL, Void, Integer> task = new AsyncTask<URL, Void, Integer>() {
            @Override
            protected Integer doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    setAndroidHeaders(urlConnection);
                    urlConnection.setConnectTimeout(5000);
                    int responseCode = urlConnection.getResponseCode();
                    urlConnection.disconnect();
                    return responseCode;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    cancel(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
                return -1;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                Toast.makeText(mActivity,"Server not active", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 200) {
                    stopNeedUpdateRequests();
                } else {
                    Toast.makeText(mActivity,"Bad request", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (sUrlDisconnect == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sUrlDisconnect);
    }

    private class NeedUpdateTask extends AsyncTask<URL, Void, Boolean> {

        private boolean doesNeedUpdate(String response) throws JSONException {
            JSONObject r = (JSONObject) new JSONTokener(response).nextValue();
            return r.getBoolean("need_update");
        }

        @Override
        protected Boolean doInBackground(URL... params) {
            URL url = params[0];
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                setAndroidHeaders(urlConnection);
                urlConnection.setReadTimeout(1000);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            while (!isCancelled()) {
                try {
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        String response = MrStreamReader.read(urlConnection.getInputStream());
                        Thread.sleep(1000);
                        return doesNeedUpdate(response);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                requestUpdate();
            } else {
                startNeedUpdateRequests();
            }
        }
    }

    private void cancelUpdater() {
        if (mUpdater == null) {
            return;
        }
        if (!mUpdater.isCancelled()) {
            mUpdater.cancel(true);
        }
        while (!mUpdater.isCancelled()) {}
        mUpdater = null;
    }

    public void startNeedUpdateRequests() {
        cancelUpdater();
        mUpdater = new NeedUpdateTask();
        mUpdater.execute(sNeedUpdate);
    }

    public void stopNeedUpdateRequests() {
        cancelUpdater();
    }

    public void requestUpdate() {
        AsyncTask<URL, Void, Void> task = new AsyncTask<URL, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                stopNeedUpdateRequests();
                ProgressBar progressBar = (ProgressBar)mActivity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    setAndroidHeaders(urlConnection);
                    urlConnection.setReadTimeout(10000);
                    if (urlConnection.getResponseCode() == 200) {
                        final InputStream in = urlConnection.getInputStream();
                        getEngine().loadSceneTree(in);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (result != null) {
                    Toast.makeText(mActivity,"Error loading", Toast.LENGTH_SHORT).show();
                }
                ProgressBar progressBar = (ProgressBar)mActivity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                startNeedUpdateRequests();
            }
        };
        if (sUrlUpdate == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sUrlUpdate);
    }
}
