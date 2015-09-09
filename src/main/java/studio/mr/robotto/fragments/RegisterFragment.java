package studio.mr.robotto.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import studio.mr.robotto.R;
import studio.mr.robotto.commons.Constants;
import studio.mr.robotto.services.DevicesServices;
import studio.mr.robotto.services.LoginServices;
import studio.mr.robotto.services.models.DeviceData;
import studio.mr.robotto.services.models.SessionData;

public class RegisterFragment extends BaseConnectionFragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private DevicesServices mDevicesServices;
    private LoginServices mLoginServices;
    private SharedPreferences mPreferences;
    private DeviceData mDevice;
    private SessionData mSession;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button manualBtn = (Button) view.findViewById(R.id.manual_register_button);
        manualBtn.setOnClickListener(this);
        setRegisterDeviceData(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.manual_register_button) {
            registerDeviceBtnEvent();
        }
    }

    private String[] getRegisterDeviceData(View view) {
        EditText urlView = (EditText) view.findViewById(R.id.urlText);
        EditText usernameView = (EditText) view.findViewById(R.id.usernameText);
        EditText passwordView = (EditText) view.findViewById(R.id.passwordText);
        String url = urlView.getText().toString();
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        return new String[]{url, username, password};
    }

    private void setRegisterDeviceData(View view) {
        SharedPreferences preferences = getSharedPreferences();
        String sessionStr = preferences.getString(Constants.SESSION_KEY, null);
        String deviceStr = preferences.getString(Constants.DEVICE_KEY, null);
        if (sessionStr == null) {
            return;
        }
        Gson gson = new Gson();
        SessionData session = gson.fromJson(sessionStr, SessionData.class);
        DeviceData device = gson.fromJson(deviceStr, DeviceData.class);
        EditText urlView = (EditText) view.findViewById(R.id.urlText);
        EditText usernameView = (EditText) view.findViewById(R.id.usernameText);
        urlView.setText(session.getUrl());
        usernameView.setText(device.getUser());
    }

    private void registerDeviceBtnEvent() {
        String[] data = getRegisterDeviceData(getView());
        String url = data[0];
        String username = data[1];
        String password = data[2];
        LoginServices loginServices = createLoginService(url);

        loginUser(loginServices, url, username, password);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
