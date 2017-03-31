package com.hypers.www.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

/**
 * Created by renbo on 2017/3/31.
 */
public class MockServerCallBack extends BluetoothGattServerCallback {
    private static final String TAG = "BleServer";
    private byte[] mAlertLevel = new byte[]{(byte) 0x00};
    private Activity mActivity;
    private boolean mIsPushStatic = false;
    private BluetoothGattServer mGattServer;
    private BluetoothGattCharacteristic mDateChar;
    private BluetoothDevice btClient;
    private BluetoothGattCharacteristic mHeartRateChar;
    private BluetoothGattCharacteristic mTemperatureChar;
    private BluetoothGattCharacteristic mBatteryChar;
    private BluetoothGattCharacteristic mManufacturerNameChar;
    private BluetoothGattCharacteristic mModuleNumberChar;
    private BluetoothGattCharacteristic mSerialNumberChar;
    private UUID mUUID = UUID.fromString("D0611E78-BBB4-4591-A5F8-487910AE4366");

    public void setupServices(BluetoothGattServer gattServer) throws InterruptedException {

        Log.d(TAG, "mUUID =" + mUUID.toString());
        if (gattServer == null) {
            throw new IllegalArgumentException("gattServer is null");
        }
        mGattServer = gattServer;
        // 设置一个GattService以及BluetoothGattCharacteristic
        {
            //immediate alert
            BluetoothGattService ias = new BluetoothGattService(mUUID,
                    BluetoothGattService.SERVICE_TYPE_PRIMARY);
            //alert level char.
            BluetoothGattCharacteristic alc = new BluetoothGattCharacteristic(
                    mUUID,
                    BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                    BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
            alc.setValue("");
            ias.addCharacteristic(alc);
            if (mGattServer != null && ias != null)
                mGattServer.addService(ias);
        }
    }

    //当添加一个GattService成功后会回调改接口。
    public void onServiceAdded(int status, BluetoothGattService service) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "onServiceAdded status=GATT_SUCCESS service=" + service.getUuid().toString());
        } else {
            Log.d(TAG, "onServiceAdded status!=GATT_SUCCESS");
        }
    }

    //BLE连接状态改变后回调的接口
    public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status,
                                        int newState) {
        Log.d(TAG, "onConnectionStateChange status=" + status + "->" + newState);
    }

    //当有客户端来读数据时回调的接口
    public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device,
                                            int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "onCharacteristicReadRequest requestId="
                + requestId + " offset=" + offset);
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
    }

    //当有客户端来写数据时回调的接口
    @Override
    public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device,
                                             int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                                             boolean responseNeeded, int offset, byte[] value) {
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
    }

    //当有客户端来写Descriptor时回调的接口
    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

        btClient = device;
        Log.d(TAG, "onDescriptorWriteRequest");
        // now tell the connected device that this was all successfull
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
    }
}