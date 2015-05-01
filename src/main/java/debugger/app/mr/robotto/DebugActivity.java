/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aar�n Negr�n, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package debugger.app.mr.robotto;

import android.app.ActionBar;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import mr.robotto.MrRobotto;
import mr.robotto.ui.MrSurfaceView;


public class DebugActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int READY_REQUEST = 1;

    private String mHost;
    private String mPort;
    private ConnectionManager mConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Button btn = (Button) findViewById(R.id.btnRefresh);
        btn.setOnClickListener(this);

        MrSurfaceView view = (MrSurfaceView) findViewById(R.id.robotto);
        MrRobotto engine = MrRobotto.getInstance();
        engine.setContext(this);
        engine.setSurfaceView(view);

        mHost = getIntent().getStringExtra("host");
        mPort = getIntent().getStringExtra("port");
        mConnectionManager = new ConnectionManager(this, mHost, mPort);
        //mConnectionManager.poll();
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
        } else if (id == R.id.barRefresh) {
            mConnectionManager.requestFastUpdate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRefresh) {
            mConnectionManager.requestFastUpdate();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
