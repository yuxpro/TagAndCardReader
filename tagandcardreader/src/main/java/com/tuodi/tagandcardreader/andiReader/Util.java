package com.tuodi.tagandcardreader.andiReader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @ClassName: Util$
 * @Description: java类作用描述
 * @Author: yuan xin
 * @CreateDate: 2021/1/6 0006$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2021/1/6 0006$
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Util {
    public static boolean isBlueConnected(String blueName) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        Set<BluetoothDevice> set = adapter.getBondedDevices();
        BluetoothDevice device = null;
        for (BluetoothDevice dev : set) {
            if (dev.getName().equalsIgnoreCase(blueName)) {
                device = dev;
                break;
            }
        }
        if (device == null) {
            return false;
        }
        //得到BluetoothDevice的Class对象
        Class<BluetoothDevice> bluetoothDeviceClass = BluetoothDevice.class;
        try {//得到连接状态的方法
            Method method = bluetoothDeviceClass.getDeclaredMethod("isConnected", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            boolean isConnected = (boolean) method.invoke(device, (Object[]) null);
            return isConnected;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
