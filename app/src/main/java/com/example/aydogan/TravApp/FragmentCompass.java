package com.example.aydogan.TravApp;

/**
 * Edited 01.05.2018 by Aydogan Musa
 * Deze fragment zorgt voor onze compass binnen de app deze wordt opgeroepen vanaf de Mainactivity
 * Deze hele klasse is analoog geschreven naar de beschrijvingen op de androiddev pagina https://developer.android.com/reference/android/hardware/SensorEvent
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class FragmentCompass extends Fragment implements SensorEventListener {
    protected ImageView image;
    protected SensorManager mSensorManager;
    protected float[] mGeomagnetic = new float[3];
    protected float[] mGravity = new float[3];
    protected float azimuth = 0f;
    protected float currentAzimuth = 0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_compass, container, false);
        image = myFragmentView.findViewById(R.id.imageViewCompass);
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Compass");
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.getUserVisibleHint()) {
            this.registerSensorListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterSensorListener();
    }

    private void registerSensorListener() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * sensorEvent.values[0]; //SensorEvent.values[0] = Acceleration minus Gx on the x-axis
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * sensorEvent.values[1]; //SensorEvent.values[1]: Acceleration minus Gy on the y-axis
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * sensorEvent.values[2]; //SensorEvent.values[2]: Acceleration minus Gz on the z-axis

            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * sensorEvent.values[0]; //All values are in micro-Tesla (uT) and measure the ambient magnetic field in the X, Y and Z axis.
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * sensorEvent.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * sensorEvent.values[2];

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean succes = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic); //rotatiematrix bepalen adhv van Magnetischveld in XYZ en versnellingen rond XYZ-assen
            if (succes) {
                //deze array moet volgens AndoidDev 3 lang zijn deze gaat gevuld worden met parameters die orientatie van toestel uitdrukken
                float orientation[] = new float[3];
                //de SensorManager.getOrientation(float[] R,float[] values) vult float[] values met parameters die orientatie van toestel uitdrukken
                //deze parameter zijn berekent adhv float[] R
                SensorManager.getOrientation(R, orientation);
                //We de orientation[0] is gevuld met de waarde van de zogenaamde azimuth
                //AndroidDev zegt het volgende erover: "Azimuth, angle of rotation about the z axis. This value represents the angle between the device's y axis and the magnetic north pole."
                //orientation[0] geeft de azimuth terug in radialen we reken deze om naar graden
                azimuth = (float) Math.toDegrees(orientation[0]);
                //we rekenen vervolgens deze graden om want RotateAnimation kan niet werken met graden
                azimuth = (azimuth + 360) % 360;
                //we creeren de animatie
                Animation anim = new RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                //we slaan vorige waarde van de azimuth op want dit word onze referentie voor de volgende computatie
                currentAzimuth = azimuth;
                //duur en herhaling van de animatie hiermee kan getweeked worden
                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);
                //We starten de animatie
                image.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}