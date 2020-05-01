/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2020 The LineageOS Project
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

package org.lineageos.settings.sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.RemoteException;

import vendor.xiaomi.hardware.touchfeature.V1_0.ITouchFeature;

public final class SensorsUtils {
    private static ITouchFeature mTouchFeature = null;

    private SensorsUtils() {
        try {
            mTouchFeature = ITouchFeature.getService();
        } catch (RemoteException e) {
            // Do nothing
        }
    }

    public static Sensor getSensor(SensorManager sm, String type) {
        for (Sensor sensor : sm.getSensorList(Sensor.TYPE_ALL)) {
            if (type.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }

    public static int setTouchMode(int mode, int value) {
        try {
            return mTouchFeature.setTouchMode(mode, value);
        } catch (RemoteException e) {
            // Do nothing
        }
        return 0;

    }
}
