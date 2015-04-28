/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package debugger.app.mr.robotto;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mr.robotto.MrRobotto;
import mr.robotto.utils.MrReader;

/**
 * Created by aaron on 22/04/2015.
 */
public class ConnectionManager {
    private String mHost;
    private String mPort;
    private static URL sURL;
    private static URL sUrlUpdate;
    private Context mContext;
    private boolean mReadyUpdate = true;

    public ConnectionManager(Context context, String host, String port) {
        mContext = context;
        setHostPort(host, port);
    }

    public ConnectionManager(Context context) {
        mContext = context;
    }

    public void setHostPort(String host, String port) {
        mHost = host;
        mPort = port;
        try {
            sURL = new URL("http://" + mHost + ":" + mPort+"/");
            sUrlUpdate = new URL("http://" + mHost + ":" + mPort+"/android-update/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static class RequestUpdater extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String s = MrReader.read(in);
                    return s;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONObject obj = (JSONObject)new JSONTokener(s).nextValue();
                    System.out.println("Tokenizo");
                    MrRobotto r = MrRobotto.getInstance();
                    r.loadSceneTree(obj);
                    System.out.println("Delego a la escena");
                    RequestUpdater task = new RequestUpdater();
                    task.execute(sUrlUpdate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connect() {
        AsyncTask<URL, Void, Integer> task = new AsyncTask<URL, Void, Integer>() {
            @Override
            protected Integer doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.disconnect();
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
                Toast.makeText(mContext,"Server not active", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 201) {
                    Intent intent = new Intent(mContext, DebugActivity.class);
                    intent.putExtra("host",mHost);
                    intent.putExtra("port",mPort);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext,"Bad request", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (sURL == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sURL);
    }

    public void poll() {
        RequestUpdater updater = new RequestUpdater();
        updater.execute(sUrlUpdate);
    }

    public void requestUpdate() {
        AsyncTask<URL, Void, String> task = new AsyncTask<URL, Void, String>() {
            @Override
            protected String doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection.getResponseCode() == 200) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String s = MrReader.read(in);
                        return s;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    try {
                        JSONObject obj = (JSONObject)new JSONTokener(s).nextValue();
                        System.out.println("Tokenizo");
                        MrRobotto r = MrRobotto.getInstance();
                        r.loadSceneTree(obj);
                        System.out.println("Delego a la escena");
                        mReadyUpdate = true;
                        //synchronized (mReadyUpdate) {
                        //    mReadyUpdate.setBoolean(false);
                        //}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (sUrlUpdate == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sUrlUpdate);
    }
}
