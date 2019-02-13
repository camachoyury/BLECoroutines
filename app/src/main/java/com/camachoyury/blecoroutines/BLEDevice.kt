package com.camachoyury.blecoroutines

import android.bluetooth.BluetoothDevice

class BLEDevice(bluetoothDevice: BluetoothDevice) {


    private var device: BluetoothDevice? = null
    private var isConnected: Boolean = false


    fun setDevice(device: BluetoothDevice) {
        this.device = device
    }


    fun getDevice(): BluetoothDevice? {
        return this.device
    }

    fun isConnected(): Boolean {
        return isConnected
    }

    fun setConnected(connected: Boolean) {
        isConnected = connected
    }
}