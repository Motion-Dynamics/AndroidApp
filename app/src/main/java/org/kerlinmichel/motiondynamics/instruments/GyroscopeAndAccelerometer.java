package org.kerlinmichel.motiondynamics.instruments;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GyroscopeAndAccelerometer {

    private static SensorManager sensorManager;
    private static Sensor gyroscopeSensor;
    private static SensorEventListener gyroscopeListener;
    private static SensorEventListener accelerometerListener;
    private static Sensor accelerometerSensor;

    private static long gyroTime;
    private static float gyroX;
    private static float gyroY;
    private static float gyroZ;
    private static float gyroAcc;

    private static long acclTime;
    private static float acclX;
    private static float acclY;
    private static float acclZ;
    private static float acclAcc;

    public static void init(Activity activity) {
        sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                gyroTime = event.timestamp;
                gyroX = event.values[0];
                gyroY = event.values[1];
                gyroZ = event.values[2];
                gyroAcc = event.accuracy;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        accelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                acclTime = event.timestamp;
                acclX = event.values[0];
                acclY = event.values[1];
                acclZ = event.values[2];
                acclAcc = event.accuracy;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    public static void turnOff() {
        sensorManager.unregisterListener(gyroscopeListener);
        sensorManager.unregisterListener(accelerometerListener);
    }

    public static long getGyroTime() {
        return gyroTime;
    }

    public static float getGyroX() {
        return gyroX;
    }

    public static float getGyroY() {
        return gyroY;
    }

    public static float getGyroZ() {
        return gyroZ;
    }

    public static float getGyroAcc() {
        return gyroAcc;
    }

    public static long getAcclTime() {
        return acclTime;
    }

    public static float getAcclX() {
        return acclX;
    }

    public static float getAcclY() {
        return acclY;
    }

    public static float getAcclZ() {
        return acclZ;
    }

    public static float getAcclAcc() {
        return acclAcc;
    }
}