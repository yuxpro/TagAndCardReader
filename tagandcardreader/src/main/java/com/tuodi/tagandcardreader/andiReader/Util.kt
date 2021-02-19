package com.tuodi.tagandcardreader.andiReader

import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.rfid.api.ADReaderInterface
import com.rfid.api.BluetoothCfg

/**
 * @ClassName:      Util$
 * @Description:     java类作用描述
 * @Author:         yuan xin
 * @CreateDate:     2021/1/27 0027$
 * @UpdateUser:     更新者：
 * @UpdateDate:     2021/1/27 0027$
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */

/**
 * ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 * 安的读写器专用工具类
 * ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */

/**
 * ByteArray to String
 * String to ByteArray
 */
private val DIGITS_LOWER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
private val DIGITS_UPPER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

fun encodeHex(data: ByteArray): CharArray {
    return encodeHex(data, true)
}

fun encodeHex(data: ByteArray, dataLen: Int): CharArray {
    return encodeHex(data, dataLen, true)
}

fun encodeHex(data: ByteArray, toLowerCase: Boolean): CharArray {
    return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}

fun encodeHex(data: ByteArray, dataLen: Int, toLowerCase: Boolean): CharArray {
    return encodeHex(data, dataLen, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}

fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
    val len = data.size
    val out = CharArray(len shl 1)
    var i = 0
    var var5 = 0
    while (i < len) {
        out[var5++] = toDigits[240 and data[i].toInt() ushr 4]
        out[var5++] = toDigits[15 and data[i].toInt()]
        ++i
    }
    return out
}

fun encodeHex(data: ByteArray, dataLen: Int, toDigits: CharArray): CharArray {
    val out = CharArray(dataLen shl 1)
    var i = 0
    var var6 = 0
    while (i < dataLen) {
        out[var6++] = toDigits[240 and data[i].toInt() ushr 4]
        out[var6++] = toDigits[15 and data[i].toInt()]
        ++i
    }
    return out
}

fun encodeHexStr(data: ByteArray): String {
    return encodeHexStr(data, false)
}

fun encodeHexStr(data: ByteArray, dataLen: Int): String {
    return encodeHexStr(data, dataLen, false)
}

fun encodeHexStr(data: ByteArray, toLowerCase: Boolean): String {
    return encodeHexStr(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}

fun encodeHexStr(data: ByteArray, dataLen: Int, toLowerCase: Boolean): String {
    return encodeHexStr(data, dataLen, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}

fun encodeHexStr(data: ByteArray, toDigits: CharArray): String {
    return String(encodeHex(data, toDigits))
}

fun encodeHexStr(data: ByteArray, dataLen: Int, toDigits: CharArray): String {
    return String(encodeHex(data, dataLen, toDigits))
}

fun decodeHex(data: CharArray): ByteArray {
    val len = data.size
    return if (len and 1 != 0) {
        ByteArray(0)
    } else {
        val out = ByteArray(len shr 1)
        var i = 0
        var j = 0
        while (j < len) {
            val f1 = toDigit(data[j], j) shl 4
            if (f1 < 0) {
                return ByteArray(0)
            }
            ++j
            val f2 = toDigit(data[j], j)
            if (f2 < 0) {
                return ByteArray(0)
            }
            ++j
            out[i] = (f1 or f2 and 255).toByte()
            ++i
        }
        out
    }
}

fun decodeHex(data: CharArray, dataLen: Int): ByteArray {
    return if (dataLen and 1 != 0) {
        ByteArray(0)
    } else {
        val out = ByteArray(dataLen shr 1)
        var i = 0
        var j = 0
        while (j < dataLen) {
            val f1 = toDigit(data[j], j) shl 4
            if (f1 < 0) {
                return ByteArray(0)
            }
            ++j
            val f2 = toDigit(data[j], j)
            if (f2 < 0) {
                return ByteArray(0)
            }
            ++j
            out[i] = (f1 or f2 and 255).toByte()
            ++i
        }
        out
    }
}

fun decodeHex(sData: String): ByteArray {
    return if (sData == "") {
        ByteArray(0)
    } else {
        val data = sData.toCharArray()
        val len = data.size
        if (len and 1 != 0) {
            ByteArray(0)
        } else {
            val out = ByteArray(len shr 1)
            var i = 0
            var j = 0
            while (j < len) {
                val t1 = toDigit(data[j], j) shl 4
                if (t1 < 0) {
                    return ByteArray(0)
                }
                ++j
                val t2 = toDigit(data[j], j)
                if (t2 < 0) {
                    return ByteArray(0)
                }
                ++j
                out[i] = (t1 or t2 and 255).toByte()
                ++i
            }
            out
        }
    }
}

fun toDigit(ch: Char, index: Int): Int {
    return Character.digit(ch, 16)
}

/**
 * ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 * usb相关
 * ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */
/**
 * 获取安迪开发板上的usb设备信息
 */
fun Context.andiUsbList() = run {
    val manager = getSystemService(Context.USB_SERVICE) as UsbManager
    val usbList = arrayListOf<UsbDevice>()
    manager.deviceList.forEach {
        it.value?.let { device ->
            if (device.vendorId == 65534 && device.productId == 145) {
                usbList.add(device)
            }
        }
    }
    usbList
}

/**
 * 获取usb设备信息
 */
fun Context.usbList() = run {
    val manager = getSystemService(Context.USB_SERVICE) as UsbManager
    val usbList = arrayListOf<UsbDevice>()
    manager.deviceList.forEach {
        it.value?.let { device ->
            usbList.add(device)
        }
    }
    usbList
}

/**
 * usb是否有对应的权限
 */
fun Context.hasUsbPermission(usb: UsbDevice) = run {
    val manager = getSystemService(Context.USB_SERVICE) as UsbManager
    manager.hasPermission(usb)
}

/**
 * 申请usb访问权限
 */
fun Context.requestUsbPermission(usb: UsbDevice) = run {
    if (hasUsbPermission(usb)) {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val mPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent("com.android.example.USB_PERMISSION"),
                0
        )
        manager.requestPermission(usb, mPendingIntent)
        hasUsbPermission(usb)
    } else {
        false
    }
}

/**
 * ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 * 蓝牙相关
 * ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */
/**
 * 获取配对的所有蓝牙名字
 */
fun getPairBluetoothName(): ArrayList<String> {
    val adapter = BluetoothAdapter.getDefaultAdapter()
    val bluetoothList = arrayListOf<String>()

    return if (adapter == null) {
        bluetoothList
    } else {
        val deviceList = adapter.bondedDevices
        val var4: Iterator<*> = deviceList.iterator()
        while (var4.hasNext()) {
            val bluetoothDevice = var4.next() as BluetoothDevice
            bluetoothList.add(bluetoothDevice.name)
        }
        bluetoothList
    }
}

/**
 * 获取配对的所有蓝牙信息
 */
fun getPairBluetoothDevices(): ArrayList<BluetoothCfg>? {
    val adapter = BluetoothAdapter.getDefaultAdapter()
    return if (adapter == null) {
        null
    } else {
        val bluetoothList = arrayListOf<BluetoothCfg>()
        val deviceList = adapter.bondedDevices
        val var4: Iterator<*> = deviceList.iterator()
        while (var4.hasNext()) {
            val bluetoothDevice = var4.next() as BluetoothDevice
            val bluetoothCfg = BluetoothCfg()
            bluetoothCfg.SetName(bluetoothDevice.name)
            bluetoothCfg.SetAddr(bluetoothDevice.address)
            bluetoothList.add(bluetoothCfg)
        }
        bluetoothList
    }
}

/**
 * 检查是否有蓝牙设备连接，由于java的反射效率比kotlin高，所以写在{@link Util#isBlueConnected}
 */
//fun isBlueConnected(blueName: String) = run {
//    val adapter = BluetoothAdapter.getDefaultAdapter()
//    if (adapter != null) {
//        val deviceList = adapter.bondedDevices
//        var dev: BluetoothDevice? = null
//        deviceList.forEach {
//            if (it.name.equals(blueName, ignoreCase = true)) {
//                dev = it
//            }
//        }
//        if (dev != null) {
//            val bluetoothDeviceClass = BluetoothDevice::class
//            try { //得到连接状态的方法
//                val methods = bluetoothDeviceClass.declaredFunctions
//                var method: KFunction<*>? = null
//                methods.forEach {
//                    if (it.name == "isConnected") {
//                        method = it
//                    }
//                }
//                method?.let {
//                    //打开权限
//                    it.isAccessible = true
////                    it.javaMethod?.invoke(bluetoothDeviceClass)
//                    return it.call(bluetoothDeviceClass) as Boolean
//                }
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//    false
//}

/**
 * 获取Andi设备的端口路径
 */
fun getAndiDevicePorts()=  ADReaderInterface.GetSerialPortPath()
