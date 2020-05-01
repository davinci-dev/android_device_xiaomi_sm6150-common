/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.doze;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import org.lineageos.settings.sensors.GxzwSensor;
import org.lineageos.settings.sensors.PickupSensor;
import org.lineageos.settings.sensors.ProximitySensor;

public class DozeService extends Service {
    private static final String TAG = "DozeService";
    private static final boolean DEBUG = false;

    private ProximitySensor mProximitySensor;
    private PickupSensor mPickupSensor;
    private GxzwSensor mGxzwSensor;
    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                onDisplayOn();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                onDisplayOff();
            }
        }
    };

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
        mGxzwSensor = new GxzwSensor(this);
        mProximitySensor = new ProximitySensor(this);
        mPickupSensor = new PickupSensor(this);

        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        super.onDestroy();
        this.unregisterReceiver(mScreenStateReceiver);
        mGxzwSensor.disable();
        mProximitySensor.disable();
        mPickupSensor.disable();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onDisplayOn() {
        if (DEBUG) Log.d(TAG, "Display on");
        mGxzwSensor.disable();
        if (DozeUtils.isPickUpEnabled(this)) {
            mPickupSensor.disable();
        }
        if (DozeUtils.isHandwaveGestureEnabled(this) ||
                DozeUtils.isPocketGestureEnabled(this)) {
            mProximitySensor.disable();
        }
    }

    private void onDisplayOff() {
        if (DEBUG) Log.d(TAG, "Display off");
        mGxzwSensor.enable();
        if (DozeUtils.isPickUpEnabled(this)) {
            mPickupSensor.enable();
        }
        if (DozeUtils.isHandwaveGestureEnabled(this) ||
                DozeUtils.isPocketGestureEnabled(this)) {
            mProximitySensor.enable();
        }
    }
}
