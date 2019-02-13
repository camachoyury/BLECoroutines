package com.camachoyury.blecoroutines

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class ScanService(val bluetoothManager: BluetoothManager) {

    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothLEScanner: BluetoothLeScanner

    val devices = hashMapOf<String, BluetoothDevice>()

    private val executor = Executors.newSingleThreadScheduledExecutor {
        Thread(it, "scheduler").apply { isDaemon = true }
    }
    suspend fun scanDevices(): HashMap<String, BluetoothDevice> = suspendCoroutine { cont ->

        bluetoothAdapter = bluetoothManager.adapter;
        val filters = arrayListOf<ScanFilter>()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()


        val bleScanCallback = object : ScanCallback() {


            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Log.d("ScanDeviceActivity", "onScanResult(): ${result?.device?.address} - ${result?.device?.name}")
                addScanResult(result)
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                for (result in results) {
                    addScanResult(result)
                }
            }

            override fun onScanFailed(errorCode: Int) {
//            Log.e(TAG, "BLE Scan Failed with code $errorCode")
            }

            private fun addScanResult(result: ScanResult) {
                result.let {
                    val device = result.device
                    val deviceAddress = device.getAddress()
                    Log.d("device", result?.device.toString())
                    Log.d("deviceAddress", device.address)
                    devices.put(deviceAddress, device)
                                    }

            }

        }
        bluetoothLEScanner = bluetoothAdapter.bluetoothLeScanner
        bluetoothLEScanner.startScan(filters, settings, bleScanCallback)
        Log.d("ScanService", "Scanning......")
        executor.schedule(
            { cont.resume(devices) },
            10000, TimeUnit.MILLISECONDS)


//        bluetoothLEScanner.startScan(filters, settings, bleScanCallback)
        Log.d("ScanService", "Stoping......")

        bluetoothLEScanner.stopScan(bleScanCallback);

//        cont.resume(devices)

//        Log.d("ScanService", "Stop......")
    }
}


