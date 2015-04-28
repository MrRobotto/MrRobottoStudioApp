/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aar�n Negr�n, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package debugger.app.mr.robotto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ConnectActivity extends ActionBarActivity implements View.OnClickListener {

    private ConnectionManager mConnectionManager = null;
    private SharedPreferences mPreferences;
    private String packageName = "debugger.app.mr.robotto";
    private String hostKey = "host";
    private String hostDefault = "192.168.1.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        //mPreferences = this.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        String host = mPreferences.getString(hostKey, hostDefault);
        EditText hostView = (EditText) findViewById(R.id.host);
        hostView.setText(host);
        Button connectionButton = (Button) findViewById(R.id.btn_connect);
        connectionButton.setOnClickListener(this);
        mConnectionManager = new ConnectionManager(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
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
        EditText hostView = (EditText) findViewById(R.id.host);
        EditText portView = (EditText) findViewById(R.id.port);
        String host = hostView.getText().toString();
        String port = portView.getText().toString();
        mPreferences.edit().putString(hostKey, host).apply();
        mConnectionManager.setHostPort(host, port);
        mConnectionManager.connect();
    }
}
