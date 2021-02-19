package com.tuodi.tagandcardreader

import android.content.Context
import androidx.annotation.IntRange
import com.tuodi.tagandcardreader.andiReader.Tag
import com.tuodi.tagandcardreader.andiReader.TagInfo
import com.tuodi.tagandcardreader.andiReader.andiUsbList
import com.tuodi.tagandcardreader.andiReader.requestUsbPermission

/**
 * @ClassName:      TagReader$
 * @Description:     java类作用描述
 * @Author:         yuan xin
 * @CreateDate:     2020/12/10 0010$
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/12/10 0010$
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */


interface Reader {

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * 串口：RDType=读写器类型;CommType=COM;ComPath=串口路径（如：/dev/ttyS1，{@see ADReaderInterface#GetSerialPortPath()}）;Baund=38400;Frame=8E1;Addr=255
     * <p>
     * 蓝牙：RDType=RPAN;CommType=BLUETOOTH;Name=蓝牙设备名称，获取配对的蓝牙{@see Util#getPairBluetoothName}
     * <p>
     * 网络：RDType=RPAN;CommType=NET;RemoteIp=192.168.1.88;RemotePort=4800
     * <p>
     * USB：RDType=读写器类型;CommType=USB;Description=USB列表索引号，{@see Util#andiUsbList}
     * <p>
     * USB转串口：RDType=%s;CommType=Z-TEK;port=1;Baund=%s;Frame=%s;Addr=255
     */
    fun openDev(
        devType: String,//设备类型，
        blueName: String,//蓝牙名字
        devNetIp: String,//设备网络IP
        devNetPort: String,//设备网络port
        devZTEKPort: String,//USB转COM的 port
        andiCommunicationType: AndiCommunicationType,//通信类型，5种：COM、BLUETOOTH、NET、USB、Z-TEK
        usbIndexStr: String,//usb列表的索引地址，默认为第一个
        serialPath: String,//串口路径
        baudRate: String,//波特率
        parityVerify: String,//奇偶校验，奇校验：8O1；偶校验：8E1；不校验：8N1
        busAddress: String,//总线地址
        callback: (isOpen: Boolean) -> Unit
    ) {
    }

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * 串口：RDType=读写器类型;CommType=COM;ComPath=串口路径（如：/dev/ttyS1，{@see ADReaderInterface#GetSerialPortPath()}）;Baund=38400;Frame=8E1;Addr=255
     *
     * @param devType 读写器设备类型，具体类型参考 {@link AndiReaderType}
     * @param comPath 串口路径，形如/dev/ttyS1，可以通过{@link ADReaderInterface#GetSerialPortPath()}方法获取
     * @param baudRate 波特率
     * @param parityVerify 奇偶校验，默认偶校验
     * @param busAddress 总线地址，默认255
     * @param callback 打开结果回调，是否打开成功
     */
    fun openCom(
        devType: String,//设备类型，
        comPath: String,//
        baudRate: String,//波特率
        parityVerify: String = "8E1",//奇偶校验，奇校验：8O1；偶校验：8E1；不校验：8N1
        busAddress: String = "255", //总线地址
        callback: (isOpen: Boolean) -> Unit
    ) {
    }

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * 蓝牙：RDType=读写器类型;CommType=BLUETOOTH;Name=蓝牙设备名称，获取配对的蓝牙{@see Util#getPairBluetoothName()}
     *
     * @param devType 读写器设备类型，具体类型参考 {@link AndiReaderType}
     * @param bluetoothName 要连接的蓝牙设备名称，可以通过{@link Util#getPairBluetoothName()}方法获取已经配对的蓝牙设备名称
     * @param callback 打开结果回调，是否打开成功
     */
    fun openBlue(
        devType: String,//设备类型，
        bluetoothName: String,//蓝牙名字
        callback: (isOpen: Boolean) -> Unit
    ) {
    }

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * 网络：RDType=读写器类型;CommType=NET;RemoteIp=192.168.1.88;RemotePort=4800
     *
     * @param devType 读写器设备类型，具体类型参考 {@link AndiReaderType}
     * @param netIp 要连接的设备的网络地址
     * @param netPort 要连接的设备的网络端口
     * @param callback 打开结果回调，是否打开成功
     */
    fun openNet(
        devType: String,//设备类型，
        netIp: String,//设备IP
        netPort: String,//网络port
        callback: (isOpen: Boolean) -> Unit
    ) {
    }

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * USB：RDType=读写器类型;CommType=USB;Description=USB列表索引号，{@see Util#andiUsbList}
     *
     * @param devType 读写器设备类型，具体类型参考 {@link AndiReaderType}
     * @param usbIndex usb设备的索引号，默认为0
     * @param callback 打开结果回调，是否打开成功
     */
    fun openUsb(
        context: Context,//上下文
        devType: String,//设备类型，
        @IntRange(from = 0) usbIndex: Int,//默认索引号为0
        callback: (isOpen: Boolean) -> Unit
    ) {
    }

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * USB转串口：RDType=%s;CommType=Z-TEK;port=1;Baund=%s;Frame=%s;Addr=255
     *
     * @param devType 读写器设备类型，具体类型参考 {@link AndiReaderType}
     * @param ZTekPort USB转COM的端口号
     * @param baudRate 波特率
     * @param parityVerify 奇偶校验，默认偶校验
     * @param busAddress 总线地址，默认255
     * @param callback 打开结果回调，是否打开成功
     */
    fun openUSB2COM(
        context: Context,//上下文
        devType: String,//设备类型，
        ZTekPort: String,//Z-TEK port
        baudRate: String,//波特率
        parityVerify: String = "8E1",//奇偶校验，奇校验：8O1；偶校验：8E1；不校验：8N1
        busAddress: String = "255", //总线地址
        callback: (isOpen: Boolean) -> Unit
    ) {
    }

    fun isDevOpen(): Boolean

    /**
     * 关闭与设备的连接
     */
    fun closeDev()

    /**
     * 蓝牙以外的设备，盘点标签或卡轮询，获得uuid和tagType
     * <p>
     * ISO15693协议有11种类型：（1L~10L,20L）
     * <p>
     *      1L：NXP ICODE SLI
     *      2L：Ti HF-I Plus
     *      3L：ST M24LRxx
     *      4L：Fujitsu MB89R118C
     *      5L：ST M24LR64
     *      6L：ST M24LR16E
     *      7L：NXP ICODE SLIX
     *      8L：NXP ICODE SLIX2
     *      9L：Ti HF-I Standard
     *      10L：Ti HF-I Pro
     *      20L：ST 25DV04
     * <p>
     * ISO14443A协议有3种类型：（1L~3L）
     * <p>
     *      1L：Mifare Ultralight（不常用）
     *      2L：Mifare S50
     *      3L：Mifare S70（不常用）
     * <p>
     * 其他的标签都会被识别为 Unknown Tag
     * <p>
     * @注意：多天线读取会导致读取缓慢
     *
     * @param intervalMillis 轮询间隔时间，单位为毫秒，默认为600毫秒
     * @param pollingCount {@code true} 轮询，{@code false} 不轮询，盘点一次。默认轮询
     * @param antennaIds 指定天线去盘点标签，默认全部天线去盘点标签
     * @param callback 盘点结果回调
     */
    fun inventoryPolling(
        intervalMillis: Long = 600L,
        @IntRange(from = -1) pollingCount: Int = -1,
        antennaIds: ByteArray = ByteArray(0),
        callback: (isConnected: Boolean, tagList: ArrayList<Tag>) -> Unit
    ) {
    }

    /**
     * 蓝牙以外的设备，暂停盘点
     */
    fun pauseInventory() {}

    /**
     * 蓝牙主动扫描模式，获取uuid
     *
     * @param bluetoothName 要连接的蓝牙设备名称，可以通过{@link Util#getPairBluetoothName()}方法获取已经配对的蓝牙设备名称
     * @param intervalMillis 轮询间隔时间，单位为毫秒，默认为600毫秒
     * @param pollingCount {@code true} 轮询，{@code false} 不轮询，盘点一次。默认轮询
     * @param callback 蓝牙扫描结果回调
     */
    fun blueScanPolling(
        bluetoothName: String,
        intervalMillis: Long = 600L,
        @IntRange(from = -1) pollingCount: Int = -1,
        callback: (isBlueConnected: Boolean, tagList: ArrayList<Tag>) -> Unit
    ) {
    }

    /**
     * 蓝牙暂停扫描
     */
    fun pauseBlueScan() {}

    /**
     * 在蓝牙连接成功之后再调用，清除苍蝇拍盘点到的标签缓存
     *
     * @param bluetoothName 要连接的蓝牙设备名称，可以通过{@link Util#getPairBluetoothName()}方法获取已经配对的蓝牙设备名称
     */
    fun clearScanCache(bluetoothName: String): Boolean = true

    /**
     * 获取标签的数据块ByteArray
     *
     * @param tag 盘点到的标签或卡
     * @param addressMode 0：无地址模式；1：有地址模式。默认为有地址模式
     * @param callback 获取的标签或卡信息
     */
    fun getTagDataBlock(
        tag: Tag,
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        callback: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
    }

    /**
     * 需要盘点到标签或卡才能获取到标签或卡信息：
     * <p>
     * 1.连接标签或卡
     * <p>
     * 2.读取标签或卡信息：
     *      ISO15693类型的标签或卡可以读取到
     *          uuid（ByteArray）
     *          dsfid
     *          AFI
     *          blockSize
     *          numOfBlocks
     *          idRef
     *      ISO14443A类型的标签或卡可以读取到
     *          uuid（ByteArray）
     * <p>
     * 3.断开连接
     * <p>
     * 注意1：由于ISO14443A类型的标签或卡只能读到uuid，没有其他有用的标签或卡信息，所以不读取，直接返回
     * <p>
     * 注意2：只有ISO15693类型的标签或卡可以读写AFI和EAS
     *
     * @param tag 盘点到的标签或卡
     * @param addressMode 0：无地址模式；1：有地址模式。默认为有地址模式
     * @param callback 获取的标签或卡信息
     */
    fun getTagInfo(
        tag: Tag,
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        callback: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
    }

    /**
     * 获取标签或卡EAS
     * <p>
     * @只有ISO15693类型的标签或卡可以读写AFI和EAS
     */
    fun getEas(
        tag: Tag,
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        callback: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
    }

    /**
     * 修改标签或卡信息
     * <p>
     * s50Key:
     *      十六进制字符串，长度为6byte
     * <p>
     * s50 keyTpe:
     * <p>
     *      Key A
     *      Key B
     *
     * @param tagInfo 盘点到的标签或卡
     * @param data 写入的数据
     * @param iso15693StartBlockAddress ISO15693类型标签或卡的起始块地址
     * @param s50StartBlockAddress S50类型标签或卡的起始块地址
     * @param s50Key S50类型标签或卡的秘钥，长度为6byte，默认全为1
     * @param s50KeyType S50类型标签或卡的秘钥类型，分为两种：Kek A和Key B
     * @param addressMode 0：无地址模式；1：有地址模式。默认为有地址模式
     * @param modifyResult 修改后的标签或卡信息
     */
    fun modifyTagInfo(
        tagInfo: TagInfo,
        data: String = "",
        @IntRange(from = 0, to = 79) iso15693StartBlockAddress: Int = 0,
        @IntRange(from = 0, to = 63) s50StartBlockAddress: Int = 0,
        s50Key: String = "FFFFFFFFFFFF",
        s50KeyType: String = "Key A",
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        modifyResult: (
            isSuccess: Boolean,
            failReason: String,
            tagInfo: TagInfo,
            data: String,
            iso15693StartBlockAddress: Int,
            s50StartBlockAddress: Int,
            s50Key: String,
            s50KeyType: String,
            addressMode: Int
        ) -> Unit
    ) {
    }

    /**
     * 修改标签或卡AFI，AFI在馆：07，AFI借出：C2
     * <p>
     * @只有ISO15693类型的标签或卡可以读写AFI和EAS
     *
     * @param tag 盘点到的标签或卡
     * @param afi 写入的防盗位AFI
     * @param addressMode 0：无地址模式；1：有地址模式。默认为有地址模式
     * @param modifyResult 修改后的标签或卡信息
     */
    fun modifyAfi(
        tag: Tag,
        afi: String,
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        modifyResult: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
    }

    /**
     * 修改标签或卡AFI，AFI在馆：07，AFI借出：C2
     * <p>
     * 修改标签或卡EAS，EAS在馆：1，EAS借出：0
     * <p>
     * @只有ISO15693类型的标签或卡可以读写AFI和EAS
     *
     * @param tag 盘点到的标签或卡
     * @param eas 写入的防盗位EAS
     * @param addressMode 0：无地址模式；1：有地址模式。默认为有地址模式
     * @param modifyResult 修改后的标签或卡信息
     */
    fun modifyEas(
        tag: Tag,
        eas: Byte,
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        modifyResult: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
    }

    /**
     * 修改标签或卡EAS，EAS在馆：1，EAS借出：0
     * <p>
     * @只有ISO15693类型的标签或卡可以读写AFI和EAS
     *
     * @param tag 盘点到的标签或卡
     * @param afi 写入的防盗位AFI
     * @param eas 写入的防盗位EAS
     * @param addressMode 0：无地址模式；1：有地址模式。默认为有地址模式
     * @param modifyResult 修改后的标签或卡信息
     */
    fun modifyAfiAndEas(
        tag: Tag,
        afi: String,
        eas: Byte,
        @IntRange(from = 0, to = 1) addressMode: Int = 1,
        modifyResult: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
    }

    /**
     * @{安迪}的打开设备的命令：
     * <p>
     * 串口：RDType=读写器类型;CommType=COM;ComPath=串口路径（如：/dev/ttyS1，{@see ADReaderInterface#GetSerialPortPath()}）;Baund=38400;Frame=8E1;Addr=255
     * <p>
     * 蓝牙：RDType=RPAN;CommType=BLUETOOTH;Name=蓝牙设备名称，获取配对的蓝牙{@see Util#getPairBluetoothName}
     * <p>
     * 网络：RDType=RPAN;CommType=NET;RemoteIp=192.168.1.88;RemotePort=4800
     * <p>
     * USB：RDType=读写器类型;CommType=USB;Description=USB接口描述，{@see Util#andiUsbList}
     * <p>
     * USB转串口：RDType=%s;CommType=Z-TEK;port=1;Baund=%s;Frame=%s;Addr=255
     */
    class AndiCommandBuilder {
        private lateinit var context: Context//上下文
        private var devType: String = ""//设备类型，
        private var blueName: String = ""//蓝牙名字
        private var devNetIp: String = ""//设备IP
        private var devNetPort: String = ""//网络port
        private var devZTEKPort: String = ""//Z-TEK port
        private var andiCommunicationType: AndiCommunicationType =
            AndiCommunicationType.COM//通信类型，5种：COM、BLUETOOTH、NET、USB、Z-TEK
        private var usbIndexStr: String = "0"//usb列表的索引地址，默认为第一个
        private var serialPath: String = ""//串口路径
        private var baudRate: String = ""//波特率
        private var parityVerify: String = "8E1"//奇偶校验，奇校验：8O1；偶校验：8E1；不校验：8N1
        private var busAddress: String = "255"//总线地址

        fun setContext(context: Context): AndiCommandBuilder = run {
            this.context = context
            return this
        }

        fun setDevType(devType: String): AndiCommandBuilder = run {
            this.devType = devType
            return this
        }

        fun setBlueName(blueName: String): AndiCommandBuilder = run {
            this.blueName = blueName
            return this
        }

        fun setDevNetIp(devNetIp: String): AndiCommandBuilder = run {
            this.devNetIp = devNetIp
            return this
        }

        fun setDevNetPort(devNetPort: String): AndiCommandBuilder = run {
            this.devNetPort = devNetPort
            return this
        }

        fun setDevZTEKPort(devZTEKPort: String): AndiCommandBuilder = run {
            this.devZTEKPort = devZTEKPort
            return this
        }

        fun setCommunicationType(andiCommunicationType: AndiCommunicationType): AndiCommandBuilder =
            run {
                this.andiCommunicationType = andiCommunicationType
                return this
            }

        fun setUsbIndexStr(usbDescription: String): AndiCommandBuilder = run {
            this.usbIndexStr = usbDescription
            return this
        }

        fun setSerialPath(serialPath: String): AndiCommandBuilder = run {
            this.serialPath = serialPath
            return this
        }

        fun setSerialBaudRate(serialBaudRate: String): AndiCommandBuilder = run {
            this.baudRate = serialBaudRate
            return this
        }

        fun setParityVerify(parityVerify: String): AndiCommandBuilder = run {
            this.parityVerify = parityVerify
            return this
        }

        fun setBusAddress(busAddress: String): AndiCommandBuilder = run {
            this.busAddress = busAddress
            return this
        }

        fun build() = when (andiCommunicationType) {
            AndiCommunicationType.COM -> {
                "RDType=${devType};CommType=${andiCommunicationType.type};ComPath=${serialPath};Baund=${baudRate};Frame=${parityVerify};Addr=${busAddress}"
            }
            AndiCommunicationType.BLUETOOTH -> {
                "RDType=${devType};CommType=${andiCommunicationType.type};Name=${blueName}"
            }
            AndiCommunicationType.NET -> {
                "RDType=${devType};CommType=${andiCommunicationType.type};RemoteIp=${devNetIp};RemotePort=${devNetPort}"
            }
            AndiCommunicationType.USB -> {
                val usbs = context.andiUsbList()
                val index = usbIndexStr.toInt()
                if (usbs.size > index) {
                    if (context.requestUsbPermission(usbs[index])) {
                        "RDType=${devType};CommType=${andiCommunicationType.type};Description=${usbIndexStr}"
                    } else {
//                throw IOException("已连接的usb设备数量为0，无法连接")
                        ""
                    }
                } else {
//                throw IOException("已连接的usb设备数量为0，无法连接")
                    ""
                }
            }
            AndiCommunicationType.USB2COM -> {
                val usbs = context.andiUsbList()
                if (usbs.size > 0) {
                    "RDType=${devType};CommType=${andiCommunicationType.type};port=${devZTEKPort};Baund=${baudRate};Frame=${parityVerify};Addr=${busAddress}"
                } else {
//                throw IOException("已连接的usb设备数量为0，无法连接")
                    ""
                }
            }
        }
    }

    enum class AndiReaderType(val type: String) {
        RPAN("RPAN"),//苍蝇拍
        TPAD("TPAD"),
        M201("M201"),
        RD201("RD201"),
        RD5100("RD5100"),
        RL8000("RL8000"),
        AH2201("AH2201"),
        AH2202("AH2202"),
        AH2206("AH2206"),
        AH2208("AH2208")
    }

    enum class AndiCommunicationType(val type: String) {
        COM("COM"),
        BLUETOOTH("BLUETOOTH"),
        NET("NET"),
        USB("USB"),
        USB2COM("Z-TEK")
    }

}