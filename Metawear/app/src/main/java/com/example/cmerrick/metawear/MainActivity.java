package com.example.cmerrick.metawear;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mbientlab.metawear.api.MetaWearBleService;
import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.Module;
import com.mbientlab.metawear.api.controller.Debug;


public class MainActivity extends ActionBarActivity implements ServiceConnection, DeviceInfoFragment.MetaWearManager {

    Menu menu;

    Debug debugController;

    @Override
    public MetaWearController getCurrentController() {
        return null;
    }

    @Override
    public boolean hasController() {
        return false;
    }

    @Override
    public boolean controllerReady() {

        if(mwCtrllr.isConnected()){
            mwCtrllr.readDeviceInformation();
        }

        debugController= (Debug) mwCtrllr.getModuleController(Module.DEBUG);

        return false;
    }

    private MetaWearBleService mwService = null;

    private final String MW_MAC_ADDRESS = "CF:7D:45:44:9B:B6";

    private MetaWearController mwCtrllr;


    DeviceInfoFragment deviceInfoFragment;


    private MetaWearController.DeviceCallbacks dCallbacks = new MetaWearController.DeviceCallbacks() {
        @Override
        public void connected() {
            Log.e("ExampleActivity", "A Bluetooth LE connection has been established!");
            Toast.makeText(getBaseContext(), "CONNECTED!!!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void disconnected() {
            Log.e("ExampleActivity", "Lost the Bluetooth LE connection!");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///< Bind the MetaWear service when the activity is created
        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
            this, Context.BIND_AUTO_CREATE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connect) {
            MenuItem connectMenuItem = menu.findItem(id);

            if(mwCtrllr.isConnected()){
                connectMenuItem.setTitle(getString(R.string.action_connect));
                mwCtrllr.close(true);
            }else{
                connectMenuItem.setTitle(getString(R.string.action_disconnect));
                mwCtrllr.connect();
            }
            return true;
        }
        else if (id == R.id.action_device_info) {
            deviceInfoFragment = DeviceInfoFragment.newInstance();
            if(mwCtrllr != null){
                deviceInfoFragment.controllerReady(mwCtrllr);
            }
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.module_detail_container, (Fragment) deviceInfoFragment)
                .commit();
        }
        else if(id == R.id.action_accelerometer){
            AccelerometerFragment accelerometerFragment = new AccelerometerFragment();

            accelerometerFragment.addTriggers(mwCtrllr);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.module_detail_container, (Fragment) accelerometerFragment)
                .commit();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(MetaWearBleService.getMetaWearBroadcastReceiver(),
            MetaWearBleService.getMetaWearIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MetaWearBleService.getMetaWearBroadcastReceiver());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mwService = ((MetaWearBleService.LocalBinder) service).getService();

        final BluetoothManager btManager =
            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice mwBoard = btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        mwCtrllr = mwService.getMetaWearController(mwBoard);

        Log.e("onServiceCOnnected", "service connected!");

        mwCtrllr.addDeviceCallback(dCallbacks);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }




}
