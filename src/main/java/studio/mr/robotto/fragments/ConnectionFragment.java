package studio.mr.robotto.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import studio.mr.robotto.R;
import studio.mr.robotto.services.DevicesServices;
import studio.mr.robotto.services.models.DeviceData;
import studio.mr.robotto.services.models.SessionData;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConnectionFragment extends BaseConnectionFragment implements View.OnClickListener {

    public ConnectionFragment() {
    }

    public static ConnectionFragment newInstance() {
        Bundle args = new Bundle();
        //args.putInt(ARG_PAGE, page);
        ConnectionFragment fragment = new ConnectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        Button connectBtn = (Button) view.findViewById(R.id.connect_button);
        connectBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_button) {
            connect();
        }
    }

    private void connect() {
        SessionData sessionData = getStudioSessionData();
        DeviceData deviceData = getStudioDeviceData();
        DevicesServices devicesServices = createDevicesService(sessionData);
        connectStudio(devicesServices, deviceData);
    }
}
