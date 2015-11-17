package com.example.cmerrick.metawear;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mbientlab.metawear.api.MetaWearController;
import com.mbientlab.metawear.api.Module;
import com.mbientlab.metawear.api.controller.Accelerometer;
import com.mbientlab.metawear.api.controller.DataProcessor;
import com.mbientlab.metawear.api.controller.Logging;
import com.mbientlab.metawear.api.util.FilterConfigBuilder;
import com.mbientlab.metawear.api.util.LoggingTrigger;
import com.mbientlab.metawear.api.util.TriggerBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccelerometerFragment extends android.support.v4.app.Fragment {

    Boolean isStarted = false;

    MetaWearController mwController;

    Accelerometer accelCtrllr;

    DataProcessor dataProcessorController;

    public AccelerometerFragment() {
        // Required empty public constructor
    }

    private final DataProcessor.Callbacks dataProcessorCallbacks = new DataProcessor.Callbacks() {
        @Override
        public void receivedFilterOutput(byte filterId, byte[] output) {
           //\ ((TextView) getView().findViewById(R.id.filter_id)).setText(String.valueOf(filterId));
            short activityMilliG = ByteBuffer.wrap(output)
                .order(ByteOrder.LITTLE_ENDIAN).getShort();
            //((TextView) getView().findViewById(R.id.filter_value)).setText(String.valueOf(activityMilliG));
        }
        @Override
        public void receivedFilterId(byte filterId){
            //((TextView) getView().findViewById(R.id.filter_id)).setText(String.valueOf(filterId));
            dataProcessorController.enableFilterNotify(filterId);
        }
    };


    public void addTriggers(MetaWearController mwController) {
   /*
    * The board will start logging once all triggers have been registered.  This is done
    * by having the receivedTriggerId callback fn start the logger when the ID for the
    * Z axis has been received
    */
        this.mwController = mwController;

        accelCtrllr = (Accelerometer) mwController.getModuleController(Module.ACCELEROMETER);
        accelCtrllr.enableXYZSampling().withFullScaleRange(Accelerometer.SamplingConfig.FullScaleRange.FSR_8G)
            .withHighPassFilter((byte) 0).withOutputDataRate(Accelerometer.SamplingConfig.OutputDataRate.ODR_100_HZ);
            //.withSilentMode();
        this.mwController.addModuleCallback(accelerometerCallbacks);

        if(dataProcessorController == null) {
            dataProcessorController = (DataProcessor) mwController.getModuleController(Module.DATA_PROCESSOR);
        }

        Logging.Trigger accelerometerTrigger = TriggerBuilder.buildAccelerometerTrigger();

        DataProcessor.FilterConfig rms= new FilterConfigBuilder.RMSBuilder().withInputCount((byte) 3)
            .withSignedInput().withOutputSize(LoggingTrigger.ACCELEROMETER_X_AXIS.length())
            .withInputSize(LoggingTrigger.ACCELEROMETER_X_AXIS.length())
            .build();

        dataProcessorController.addFilter(accelerometerTrigger, rms);
        mwController.addModuleCallback(dataProcessorCallbacks);

    }



    private final Accelerometer.Callbacks accelerometerCallbacks = new Accelerometer.Callbacks() {
        @Override
        public void receivedDataValue(short x, short y, short z) {
            ((TextView) getView().findViewById(R.id.accelerometer_x)).setText(String.valueOf(x));
            ((TextView) getView().findViewById(R.id.accelerometer_y)).setText(String.valueOf(y));
            ((TextView) getView().findViewById(R.id.accelerometer_z)).setText(String.valueOf(z));
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.accelerometer_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((Button) getView().findViewById(R.id.accelerometer_start_stop)).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStarted) {
                        ((Button) v).setText("start accelerometer");
                        accelCtrllr.stopComponents();
                    } else {
                        ((Button) v).setText("stop accelerometer");
                        accelCtrllr.startComponents();
                    }
                }
            }
        );
    }
}
