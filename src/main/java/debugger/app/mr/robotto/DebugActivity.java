/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package debugger.app.mr.robotto;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import mr.robotto.MrRobotto;
import mr.robotto.ui.MrSurfaceView;


public class DebugActivity extends ActionBarActivity {

    public static final int READY_REQUEST = 1;

    private String mHost;
    private String mPort;
    private ConnectionManager mConnectionManager;
    private Thread mThread;

    private Handler mHandler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DebugActivity.READY_REQUEST:
                    // Invalidar vista para repintado
                    //miVista.invalidate();
                    break;
            }
            super.handleMessage(msg);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        MrSurfaceView view = (MrSurfaceView) findViewById(R.id.robotto);
        MrRobotto engine = MrRobotto.getInstance();
        engine.setContext(this);
        engine.setSurfaceView(view);

        mHost = getIntent().getStringExtra("host");
        mPort = getIntent().getStringExtra("port");
        mConnectionManager = new ConnectionManager(this, mHost, mPort);
        mConnectionManager.poll();
        //mConnectionManager.requestUpdate();

        /*mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean update;
                while(true) {
                    synchronized (mConnectionManager) {
                        update = mConnectionManager.isReadyUpdate();
                    }
                    if (update) {
                        mConnectionManager.requestUpdate();
                        synchronized (mConnectionManager) {
                            mConnectionManager.setReadyUpdate(false);
                        }
                    }
                }
            }
        });*/
        //mThread.start();
    }

    @Override
    protected void onDestroy() {
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
