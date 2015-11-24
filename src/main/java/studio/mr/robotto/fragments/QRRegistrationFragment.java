package studio.mr.robotto.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import eu.livotov.zxscan.ScannerView;
import studio.mr.robotto.R;


public class QrRegistrationFragment extends Fragment implements View.OnClickListener, ScannerView.ScannerViewEventListener {

    private OnFragmentInteractionListener mListener;
    private ScannerView mScannerView;
    private boolean mScannerStarted = false;


    public QrRegistrationFragment() {
        // Required empty public constructor
    }

    public static QrRegistrationFragment newInstance() {
        QrRegistrationFragment fragment = new QrRegistrationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrregistration, container, false);

        mScannerView = (ScannerView) view.findViewById(R.id.scanner);
        mScannerView.setScannerViewEventListener(this);
        //mScannerView.startScanner();
        mScannerStarted = false;

        Button qrStartBtn = (Button) view.findViewById(R.id.start_qr_register_button);
        qrStartBtn.setOnClickListener(this);
        Button qrStopBtn = (Button) view.findViewById(R.id.stop_qr_register_button);
        qrStopBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                stopScanner();
            }
        }
    }

    private void stopScanner() {
        if (mScannerStarted) {
            mScannerView.stopScanner();
            mScannerStarted = false;
        }
    }

    private void startScanner() {
        if (!mScannerStarted) {
            mScannerView.startScanner();
            mScannerStarted = true;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_qr_register_button) {
            startScanner();
        } else if (view.getId() == R.id.stop_qr_register_button) {
            stopScanner();
        }
    }

    @Override
    public void onScannerReady() {

    }

    @Override
    public void onScannerFailure(int i) {

    }

    @Override
    public boolean onCodeScanned(String data) {
        mScannerView.stopScanner();
        Toast.makeText(getActivity(), "Data scanned: " + data, Toast.LENGTH_SHORT).show();
        return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
