package com.example.antonellab.busola;

import android.os.Bundle;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.StrictMode;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

//// SENSOR SIMULATOR
//import org.openintents.sensorsimulator.hardware.Sensor;
//import org.openintents.sensorsimulator.hardware.SensorEvent;
//import org.openintents.sensorsimulator.hardware.SensorEventListener;
//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

public class MainActivity extends Activity implements SensorEventListener {

    private ImageView imgCompass;
    private TextView tvHeading;
    private SensorManager mSensorManager;

//    // SENSOR SIMULATOR
//    private SensorManagerSimulator mSensorManager;
//    private ConnectionToSensorSimulator conn;

    private Sensor accelerometer, magnetometer;
    private float[] lastAccel = new float[3];
    private float[] lastMagn = new float[3];
    private boolean setAccel = false;
    private boolean setMagn = false;
    private float degrees = 0f;
    private float[] rotation = new float[9];
    private float[] orientation = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgCompass = (ImageView) findViewById(R.id.busola);
        tvHeading = (TextView) findViewById(R.id.textView);

        // SENSOR SIMULATOR
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
//        mSensorManager.connectSimulator();

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    protected void onResume() {
        super.onResume();

        // SENSOR SIMULATOR
//        mSensorManager.registerListener(this,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManagerSimulator.SENSOR_DELAY_NORMAL);
//
//        mSensorManager.registerListener(this,
//                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//                SensorManagerSimulator.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {

            // SENSOR SIMULATOR
            //int sensor = event.type;

                if (event.sensor == accelerometer) {
                    float degreeZ = Math.round(event.values[0]);// get the angle around the z-axis rotated
                    float degreeY = Math.round(event.values[1]);// get the angle around the x-axis rotated
                    float degreeX = Math.round(event.values[2]);// get the angle around the y-axis rotated
                    //tvHeading.setText("Heading: " + Float.toString(degreeX) + ", " + Float.toString(degreeY) + ", " +  Float.toString(degreeZ) + " degrees");

                    System.arraycopy(event.values, 0, lastAccel, 0, event.values.length);
                    setAccel = true;
                } else if (event.sensor == magnetometer) {
                    System.arraycopy(event.values, 0, lastMagn, 0, event.values.length);
                    setMagn = true;
                }
                if (setAccel && setMagn) {
                    SensorManager.getRotationMatrix(rotation, null, lastAccel, lastMagn);
                    SensorManager.getOrientation(rotation, orientation);
                    float azimuthInRadians = orientation[0];
                    float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
                    RotateAnimation ra = new RotateAnimation(degrees, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                    ra.setDuration(250);
                    ra.setFillAfter(true);

                    imgCompass.startAnimation(ra);
                    degrees = -azimuthInDegress;
                    degrees += 360;
                    tvHeading.setText("Heading: " + Float.toString(degrees) + " degrees");

                }
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }

}