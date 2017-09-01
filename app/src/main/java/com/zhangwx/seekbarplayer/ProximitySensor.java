package com.zhangwx.seekbarplayer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by zhangweixiong on 2017/9/1.
 */

public class ProximitySensor implements SensorEventListener {

    private final static float PROXIMITYT_LIMIT = 0.5f;

    private final SensorManager mManager;
    private final Sensor mProximitySensor;

    private float mRealMaxRange;
    private boolean mSensorValidChecked;
    private boolean mIsProximityValid;
    private OnSensorEventListener mSensorListener;


    public ProximitySensor (Context context) {
        mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    public void registryEventListener (OnSensorEventListener listener) {
        mManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorListener = listener;
    }

    public void unRegistryEventListener () {
        mManager.unregisterListener(this);
        mSensorListener = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if ( event.values[0] >= event.sensor.getMaximumRange() && event.values[0] > mRealMaxRange ){
            mRealMaxRange = event.values[0];
        }

        float distance = event.values[0];
        float maximum = event.sensor.getMaximumRange();

        //for some device, proximity sensor does not work, we need to check it
        if (!mSensorValidChecked) {
            mIsProximityValid = (distance > 0);
            if (mIsProximityValid) {
                mSensorValidChecked = true;
            }
        }

        boolean active = false;
        if (mIsProximityValid) {
            active = (distance >= 0 && distance < PROXIMITYT_LIMIT && distance < maximum);
            // 这里针对魅族2距离感应失效问题单独判断（魅族2的距离反应器只有3.0/7.0两个值）
            if (distance < maximum && distance == mRealMaxRange - maximum) {
                active = true;
            }
        }
        if (mSensorListener!=null) {
            mSensorListener.onSensorChange(active);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OnSensorEventListener {
        public void onSensorChange (boolean isCloseActive);
    }
}
