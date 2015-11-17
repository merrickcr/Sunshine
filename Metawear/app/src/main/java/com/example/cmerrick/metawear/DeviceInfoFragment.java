package com.example.cmerrick.metawear;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mbientlab.metawear.api.GATT;
import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.characteristic.Battery;
import com.mbientlab.metawear.api.characteristic.DeviceInformation;

import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the {@link
 * DeviceInfoFragment.OnFragmentInteractionListener} interface to handle interaction events. Use the {@link DeviceInfoFragment#newInstance}
 * factory method to create an instance of this fragment.
 */
public class DeviceInfoFragment extends android.support.v4.app.Fragment {

    private MetaWearController.DeviceCallbacks dCallbacks = new MetaWearController.DeviceCallbacks() {
        @Override
        public void connected() {
            Log.e("ExampleActivity", "A Bluetooth LE connection has been established!");
            Toast.makeText(getActivity(), "CONNECTED!!!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void disconnected() {
            Log.e("ExampleActivity", "Lost the Bluetooth LE connection!");
        }

        @Override
        public void receivedGATTCharacteristic(
            GATT.GATTCharacteristic characteristic, byte[] data) {

            values.put(characteristic, new String(data));

            final Integer viewId = views.get(characteristic);

            if(viewId != null && isVisible()){
                ((TextView) getView().findViewById(viewId)).setText(values.get(characteristic));
            }

            if (characteristic == Battery.BATTERY_LEVEL) {
                values.put(characteristic, String.format(Locale.US, "%s", data[0]));
            } else {
                values.put(characteristic, new String(data));
            }

        }
    };

    private HashMap<GATT.GATTCharacteristic, String> values= new HashMap<>();
    private final static HashMap<GATT.GATTCharacteristic, Integer> views= new HashMap<>();
    static {
        views.put(DeviceInformation.MANUFACTURER_NAME, R.id.manufacturer_name);
        views.put(DeviceInformation.SERIAL_NUMBER, R.id.serial_number);
        views.put(DeviceInformation.FIRMWARE_VERSION, R.id.firmware_version);
        views.put(DeviceInformation.HARDWARE_VERSION, R.id.hardware_version);
        views.put(Battery.BATTERY_LEVEL, R.id.battery_level);
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceInfoFragment newInstance() {
        DeviceInfoFragment fragment = new DeviceInfoFragment();
        return fragment;
    }

    public DeviceInfoFragment() {
        // Required empty public constructor
    }

    protected MetaWearManager mwManager;

    public interface MetaWearManager {
        public MetaWearController getCurrentController();
        public boolean hasController();
        public boolean controllerReady();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof MetaWearManager)) {
            throw new IllegalStateException(
                "Activity must implement fragment's callbacks.");
        }
        mwManager= (MetaWearManager) activity;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_info, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((Button) view.findViewById(R.id.battery_level_button)).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mwManager.controllerReady()) {
                        mwManager.getCurrentController().readBatteryLevel();
                    }
                }
            }
        );

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be
     * communicated to the activity and potentially other fragments contained in that activity. <p> See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html" >Communicating with Other Fragments</a> for more
     * information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void controllerReady(MetaWearController mwController){
        mwController.addDeviceCallback(dCallbacks);
    }

}
