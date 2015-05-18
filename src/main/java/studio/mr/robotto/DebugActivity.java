/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aar�n Negr�n, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.Socket;

import mr.robotto.MrRobotto;
import mr.robotto.ui.MrSurfaceView;
import studio.mr.robotto.socketlayer.StudioContext;


public class DebugActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int READY_REQUEST = 1;

    private String mHost;
    private String mPort;
    private String mServerSocketPort;
    private ConnectionManager mConnectionManager;
    private ProgressBar mProgressBar;
    private StudioContext mStudioContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Button btn = (Button) findViewById(R.id.btnRefresh);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        btn.setOnClickListener(this);

        MrSurfaceView view = (MrSurfaceView) findViewById(R.id.robotto);
        MrRobotto engine = MrRobotto.getInstance();
        engine.setContext(this);
        engine.setSurfaceView(view);

        mHost = getIntent().getStringExtra("host");
        mPort = getIntent().getStringExtra("port");
        mServerSocketPort = getIntent().getStringExtra("server_socket");
        mConnectionManager = new ConnectionManager(this, mHost, mPort);
        //mConnectionManager.poll();

        startListenerSocket();
    }

    private void startListenerSocket() {
        Thread thread = new Thread(new ListenerSocket(this));
        thread.start();
    }

    public void requestUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionManager.requestUpdate();
            }
        });
    }

    private class ListenerSocket implements Runnable {

        private DebugActivity mDebugActivity;
        public ListenerSocket(DebugActivity debugActivity) {
            mDebugActivity = debugActivity;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(mHost, Integer.parseInt(mServerSocketPort));
                mStudioContext = new StudioContext(mDebugActivity, socket);
                mStudioContext.initContext();
                mStudioContext.processInputData();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        mStudioContext.closeContext();
        super.onDestroy();
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
            mConnectionManager.requestUpdate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRefresh) {
            mConnectionManager.requestUpdate();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
