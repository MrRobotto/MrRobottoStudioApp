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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import mr.robotto.MrRobotto;

/**
 * Created by aaron on 22/04/2015.
 */
public class ConnectionManager {
    public static final String UA = "MrRobottoStudio";

    private String mHost;
    private String mPort;
    private String mServerSocketPort;
    private static URL sUrlRoot;
    private static URL sUrlDisconnect;
    private static URL sUrlUpdate;
    private Activity mActivity;

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
            sUrlRoot = new URL("http://" + mHost + ":" + mPort+"/android/connect");
            sUrlDisconnect = new URL("http://"+mHost+":"+mPort+"/android/disconnect");
            sUrlUpdate = new URL("http://" + mHost + ":" + mPort+"/android/update/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static void setStudioUA(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent",UA);
    }

    public void connect() {
        AsyncTask<URL, Void, Integer> task = new AsyncTask<URL, Void, Integer>() {
            @Override
            protected Integer doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    setStudioUA(urlConnection);
                    urlConnection.setConnectTimeout(5000);
                    mServerSocketPort = urlConnection.getHeaderField("server_socket_port");
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
                Toast.makeText(mActivity,"Server not active", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 200) {
                    Intent intent = new Intent(mActivity, DebugActivity.class);
                    intent.putExtra("host",mHost);
                    intent.putExtra("port",mPort);
                    intent.putExtra("server_socket", mServerSocketPort);
                    //mActivity.startActivity(intent);
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

    public void disconnect() {
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
                Toast.makeText(mActivity,"Server not active", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 200) {
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

    public void requestUpdate() {
        AsyncTask<URL, Void, Void> task = new AsyncTask<URL, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ProgressBar progressBar = (ProgressBar)mActivity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(URL... params) {
                URL url = params[0];
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    setStudioUA(urlConnection);
                    urlConnection.setReadTimeout(10000);
                    if (urlConnection.getResponseCode() == 200) {
                        final InputStream in = urlConnection.getInputStream();
                        MrRobotto.getInstance().loadSceneTree(in);
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
            }
        };
        if (sUrlUpdate == null) {
            throw new RuntimeException("The host and port has not been set");
        }
        task.execute(sUrlUpdate);
    }

    public void startListenerSocket() {
        Thread thread = new Thread(new ListenerSocket());
        thread.start();
    }

    private class ListenerSocket implements Runnable {

        @Override
        public void run() {
            InputStream inputStream;
            OutputStream outputStream;
            try {
                Socket socket = new Socket(mHost, Integer.valueOf(mServerSocketPort));
                socket.setSoTimeout(500);
                inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {

            }
        }
    }
}
