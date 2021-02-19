package com.tuodi.tagandcardreader.andiReader

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.rfid.api.*
import com.rfid.def.ApiErrDefinition
import com.rfid.def.RfidDef
import com.rfid.spec.SpecAIPInvenParamISO14443A
import com.tuodi.tagandcardreader.Reader
import com.tuodi.tagandcardreader.andiReader.Util.isBlueConnected
import kotlinx.coroutines.*

/**
 * @ClassName:      TagReader$
 * @Description:     java类作用描述
 * @Author:         yuan xin
 * @CreateDate:     2020/12/11 0011$
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/12/11 0011$
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
class AndiTagReader(activityOrFragment: Any) : Reader {

    private var launchScope: CoroutineScope = when (activityOrFragment) {
        is FragmentActivity -> {
            activityOrFragment.lifecycleScope
        }
        is Fragment -> {
            activityOrFragment.lifecycleScope
        }
        else -> {
            throw IllegalArgumentException("请传入Activity或Fragment的实例")
        }
    }

    companion object {
        val readerInterface: ADReaderInterface = ADReaderInterface()
        val iso15693Interface: ISO15693Interface = ISO15693Interface()
        val iso14443AInterface: ISO14443AInterface = ISO14443AInterface()
    }

    private var inventoryJob: Job? = null
    private var scanJob: Job? = null

    private var pauseInventory = false
    private var pauseScan = false

    override fun openCom(
        devType: String,
        comPath: String,
        baudRate: String,
        parityVerify: String,
        busAddress: String,
        callback: (isOpen: Boolean) -> Unit
    ) {
        val openAndiCommand = Reader.AndiCommandBuilder()
            .setDevType(devType)
            .setSerialPath(comPath)
            .setSerialBaudRate(baudRate)
            .setParityVerify(parityVerify)
            .setBusAddress(busAddress)
            .setCommunicationType(Reader.AndiCommunicationType.COM)
        println("打开指令：" + openAndiCommand.build())
        readerInterface.RDR_Open(openAndiCommand.build())
        callback.invoke(readerInterface.isReaderOpen)
    }

    override fun openBlue(
        devType: String,
        bluetoothName: String,
        callback: (isOpen: Boolean) -> Unit
    ) {
        val openAndiCommand = Reader.AndiCommandBuilder()
            .setDevType(devType)
            .setBlueName(bluetoothName)
            .setCommunicationType(Reader.AndiCommunicationType.BLUETOOTH)
        println("打开指令：" + openAndiCommand.build())
        readerInterface.RDR_Open(openAndiCommand.build())
        callback.invoke(readerInterface.isReaderOpen)
    }

    override fun openNet(
        devType: String,
        netIp: String,
        netPort: String,
        callback: (isOpen: Boolean) -> Unit
    ) {
        val openAndiCommand = Reader.AndiCommandBuilder()
            .setDevType(devType)
            .setDevNetIp(netIp)
            .setDevNetPort(netPort)
            .setCommunicationType(Reader.AndiCommunicationType.NET)
        println("打开指令：" + openAndiCommand.build())
        readerInterface.RDR_Open(openAndiCommand.build())
        callback.invoke(readerInterface.isReaderOpen)
    }

    override fun openUsb(
        context: Context?,
        devType: String,
        usbIndex: Int,
        callback: (isOpen: Boolean) -> Unit
    ) {
        val openAndiCommand = Reader.AndiCommandBuilder()
            .setContext(context)
            .setDevType(devType)
            .setUsbIndexStr(usbIndex.toString())
            .setCommunicationType(Reader.AndiCommunicationType.USB)
        println("打开指令：" + openAndiCommand.build())
        readerInterface.RDR_Open(openAndiCommand.build())
        callback.invoke(readerInterface.isReaderOpen)
    }

    override fun openUSB2COM(
        context: Context?,
        devType: String,
        ZTekPort: String,
        baudRate: String,
        parityVerify: String,
        busAddress: String,
        callback: (isOpen: Boolean) -> Unit
    ) {
        val openAndiCommand = Reader.AndiCommandBuilder()
            .setContext(context)
            .setDevType(devType)
            .setDevZTEKPort(ZTekPort)
            .setSerialBaudRate(baudRate)
            .setParityVerify(parityVerify)
            .setBusAddress(busAddress)
            .setCommunicationType(Reader.AndiCommunicationType.USB2COM)
        println("打开指令：" + openAndiCommand.build())
        readerInterface.RDR_Open(openAndiCommand.build())
        callback.invoke(readerInterface.isReaderOpen)
    }

    override fun isDevOpen(): Boolean {
        return readerInterface.isReaderOpen
    }

    override fun closeDev() {
        inventoryJob?.cancel()
        scanJob?.cancel()
    }

    /**
     * 关闭硬件设备与安卓板的连接
     */
    fun closeHardwareDev() {
        readerInterface.RDR_Close()
    }

    override fun inventoryPolling(
        intervalMillis: Long,
        pollingCount: Int,
        antennaIds: ByteArray,
        callback: (isConnected: Boolean, tagList: ArrayList<Tag>) -> Unit
    ) {
        super.inventoryPolling(intervalMillis, pollingCount, antennaIds, callback)
        inventoryJob = launchScope.launch(Dispatchers.IO) {
            var inventoryType = RfidDef.AI_TYPE_NEW
            val tag = ADReaderInterface.RDR_CreateInvenParamSpecList()//存储标签或卡信息的对象
            //创建具体的存储标签或卡信息的对象
            val createISO15693Result = ISO15693Interface.ISO15693_CreateInvenParam(
                tag,
                0.toByte(),//0表示读写器所有的天线都盘点标签或卡，1~N（N为正数）为对应的天线id
                false,//是否匹配AFI
                0.toByte(),//匹配的AFI值
                0.toByte()//读写器盘点的间隔时隙
            )
            var createISO14443Result: SpecAIPInvenParamISO14443A? = null
            if (createISO15693Result == null) {
                createISO14443Result = ISO14443AInterface.ISO14443A_CreateInvenParam(
                    tag,
                    0.toByte()//0表示读写器所有的天线都盘点标签或卡，1~N（N为正数）为对应的天线id
                )
            }
            //具体的存储标签或卡信息的对象无论是否创建成功，都可以继续盘点
            //获取所有天线，天线id从1开始，若传入的
            var antennaId = antennaIds
            var allAntenna = false
            //天线id从1开始，小于1的都是非法的
            antennaIds.forEach {
                if (it.toInt() <= 0) {
                    allAntenna = true
                    return@forEach
                }
            }
            //没有指定天线，默认全部天线一起盘点
            if (antennaIds.isEmpty()) {
                allAntenna = true
            }
            if (allAntenna) {
                var antennaAmount: Int =
                    readerInterface.RDR_GetAntennaInterfaceCount()//获取读写器的天线数量，M60为4个、RD5100为30个、RD5200不定，其他类型的读写器都为1
                antennaId = ByteArray(antennaAmount)
                while (antennaAmount > 0) {
                    antennaId[antennaAmount - 1] = antennaAmount.toByte()
                    antennaAmount--
                }
            }
//            val startTime = System.currentTimeMillis()
//            var tempTime = startTime
//            var i = 0
//            while (i < 100) {
//                if (System.currentTimeMillis() >= tempTime) {
//                    println("isActive: ${isActive}")
//                    println("count time: ${i++}")
//                    tempTime += 500L
//                }
//            }
            //600ms轮询获取标签数据
            var counts = pollingCount
            var pollingForever = (counts != 0)
            while (isActive && pollingForever) {
                if (counts > 0) {
                    pollingForever = (counts--) != 0
                }
                if (!pauseInventory) {
                    delay(intervalMillis)
                    if (!readerInterface.isReaderOpen) {
                        println("readerInterface：$readerInterface")
                        launch(Dispatchers.Main) {
                            callback.invoke(false, ArrayList())
                        }
                        break
                    }
                    val inventoryResult = readerInterface.RDR_TagInventory(
                        inventoryType,//AI_TYPE_NEW 表示盘点所有标签或卡，AI_TYPE_CONTINUE 表示不盘点处于Quiet状态的标签或卡
                        antennaId,//天线数组，如果传null则表示使用默认天线
                        0,//盘点超时时间
                        tag
                    )
                    if (inventoryResult == ApiErrDefinition.NO_ERROR || inventoryResult == -ApiErrDefinition.ERR_STOPTRRIGOCUR) {
                        inventoryType = RfidDef.AI_TYPE_NEW
                        if (inventoryResult == -ApiErrDefinition.ERR_STOPTRRIGOCUR) {
                            inventoryType = RfidDef.AI_TYPE_CONTINUE
                        }
                        var tagReport =
                            readerInterface.RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST)
                        val tagList = ArrayList<Tag>()
                        while (tagReport != null) {
                            //先尝试以ISO15693Tag方式读取，若失败，以ISO14443ATag方式读取
                            val tag15693 = ISO15693Tag()
                            val result =
                                ISO15693Interface.ISO15693_ParseTagDataReport(tagReport, tag15693)
                            if (result == ApiErrDefinition.NO_ERROR) {
                                tagList.add(
                                    Tag(
                                        tag15693.uid,
                                        encodeHexStr(tag15693.uid),
                                        tag15693.ant_id,
                                        ISO15693Interface.GetTagNameById(tag15693.tag_id),
                                        tag15693.tag_id
                                    )
                                )
                                tagReport =
                                    readerInterface.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT)
                                continue
                            }

                            val tag14443A = ISO14443ATag()
                            val results =
                                ISO14443AInterface.ISO14443A_ParseTagDataReport(
                                    tagReport,
                                    tag14443A
                                )
                            if (results == ApiErrDefinition.NO_ERROR) {
                                tagList.add(
                                    Tag(
                                        tag14443A.uid,
                                        encodeHexStr(tag14443A.uid),
                                        tag14443A.ant_id,
                                        ISO14443AInterface.GetTagNameById(tag14443A.tag_id),
                                        tag14443A.tag_id
                                    )
                                )
                                tagReport =
                                    readerInterface.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT)
                                continue
                            }
                        }
                        launch(Dispatchers.Main) {
                            callback.invoke(true, tagList)
                        }
                    }
                }
            }
        }
    }

    override fun pauseInventory() {
        super.pauseInventory()
        pauseInventory = true
    }

    override fun blueScanPolling(
        bluetoothName: String,
        intervalMillis: Long,
        pollingCount: Int,
        callback: (isBlueConnected: Boolean, tagList: ArrayList<Tag>) -> Unit
    ) {
        super.blueScanPolling(bluetoothName, intervalMillis, pollingCount, callback)
        var gFlag = 0x00.toByte()
        scanJob = launchScope.launch(Dispatchers.IO) {
            var counts = pollingCount
            var pollingForever = (counts != 0)
            while (isActive && pollingForever) {
                if (counts > 0) {
                    pollingForever = (counts--) != 0
                }
                if (!pauseScan) {
                    delay(intervalMillis)
                    val isBlueConnected = isBlueConnected(bluetoothName)
                    val tagList = ArrayList<Tag>()
                    if (!isBlueConnected) {
                        launch(Dispatchers.Main) {
                            callback.invoke(isBlueConnected, tagList)
                        }
                        break
                    }
                    val result = readerInterface.RDR_BuffMode_FetchRecords(gFlag)
                    if (result != ApiErrDefinition.NO_ERROR) {
                        gFlag = 0x00
                        continue
                    }
                    gFlag = 0x01
                    var tagReport = readerInterface.RDR_GetTagDataReport(RfidDef.RFID_SEEK_FIRST)
                    while (tagReport != null) {
                        val uid = ByteArray(32)
                        val uidLength = IntArray(1)
                        uidLength[0] = uid.size
                        if (ADReaderInterface.RDR_ParseTagDataReportRaw(
                                tagReport,
                                uid,
                                uidLength
                            ) == 0
                        ) {
                            if (uidLength[0] > 0) {
                                tagList.add(Tag(uid, encodeHexStr(uid, uidLength[0])))
                            }
                        }
                        tagReport = readerInterface.RDR_GetTagDataReport(RfidDef.RFID_SEEK_NEXT)
                    }
                    launch(Dispatchers.Main) {
                        callback.invoke(isBlueConnected, tagList)
                    }
                }
            }
        }
    }

    override fun pauseBlueScan() {
        super.pauseBlueScan()
        pauseScan = true
    }

    override fun clearScanCache(bluetoothName: String): Boolean {
        val isBlueConnected = isBlueConnected(bluetoothName)
        if (!isBlueConnected) {
            return false
        }
        return when (readerInterface.RPAN_ClearScanRecord()) {
            ApiErrDefinition.NO_ERROR -> true
            else -> false
        }
    }

    override fun getTagDataBlock(
        tag: Tag,
        addressMode: Int,
        callback: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
        var isSuccess = false
        var failReason = ""
        val tagInfo = TagInfo()
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            callback.invoke(isSuccess, failReason, tagInfo)
            return
        }
        launchScope.launch {
            if (!tag.tagType.contains("Mifare")) {//ISO15693
                //连接标签或卡
                if (!connectISO15693Tag(tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                //获取标签或卡信息
                val infoUid = ByteArray(8)
                val dsfid = ByteArray(1)
                val afi = ByteArray(1)
                val blkSize = LongArray(1)
                val numOfBloks = LongArray(1)
                val icRef = ByteArray(1)
                iso15693Interface.ISO15693_GetSystemInfo(
                    infoUid, dsfid, afi, blkSize,
                    numOfBloks, icRef
                )
                tagInfo.tag = tag
                tagInfo.afi = afi[0]
                tagInfo.blockSize = blkSize[0]
                tagInfo.numOfBlocks = numOfBloks[0]
                tagInfo.dsfid = dsfid[0]
                tagInfo.icRef = icRef[0]
                //获取标签数据块ByteArray
                val bufBlocks = ByteArray(tagInfo.blockSize.toInt() * tagInfo.numOfBlocks.toInt())
                val result = iso15693Interface.ISO15693_ReadMultiBlocks(
                    false,
                    0,
                    tagInfo.numOfBlocks.toInt(),
                    0,
                    bufBlocks,
                    0L
                )
                tagInfo.dataBlock = bufBlocks
                //断开与标签或卡的连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "获取标签/卡的数据块失败"
                    return@launch
                }
                isSuccess = true
            }
        }
        callback.invoke(isSuccess, failReason, tagInfo)
    }

    @JvmOverloads
    override fun getTagInfo(
        tag: Tag,
        addressMode: Int,
        callback: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
        var isSuccess = false
        var failReason = ""
        val tagInfo = TagInfo()
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            callback.invoke(isSuccess, failReason, tagInfo)
            return
        }
        launchScope.launch {
            //设置天线id，即指定哪个天线操作标签数据
            readerInterface.RDR_SetAcessAntenna(tag.antennaId.toByte())
            if (!tag.tagType.contains("Mifare")) {//ISO15693
                //连接标签或卡
                if (!connectISO15693Tag(tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                //获取标签或卡信息
                val infoUid = ByteArray(8)
                val dsfid = ByteArray(1)
                val afi = ByteArray(1)
                val blkSize = LongArray(1)
                val numOfBloks = LongArray(1)
                val icRef = ByteArray(1)
                val result = iso15693Interface.ISO15693_GetSystemInfo(
                    infoUid, dsfid, afi, blkSize,
                    numOfBloks, icRef
                )
                tagInfo.tag = tag
                tagInfo.afi = afi[0]
                tagInfo.blockSize = blkSize[0]
                tagInfo.numOfBlocks = numOfBloks[0]
                tagInfo.dsfid = dsfid[0]
                tagInfo.icRef = icRef[0]
                //断开与标签或卡的连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "获取标签/卡的信息失败"
                    return@launch
                }
                isSuccess = true
            }
            //由于ISO14443A类型的标签或卡只能读到uuid，没有其他有用的标签或卡信息，所以不读取，直接返回
            if (tag.tagType.contains("Mifare")) {//ISO14443A
                isSuccess = true
            }
        }
        callback.invoke(isSuccess, failReason, tagInfo)
    }

    @JvmOverloads
    override fun getEas(
        tag: Tag,
        addressMode: Int,
        callback: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
        super.getEas(tag, addressMode, callback)
        var isSuccess = false
        var failReason = ""
        val easByteArray = ByteArray(1) { -1 }
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            callback.invoke(isSuccess, failReason, TagInfo(tag = tag, eas = easByteArray[0]))
        }
        launchScope.launch { //设置天线id，即指定哪个天线操作标签数据
            readerInterface.RDR_SetAcessAntenna(tag.antennaId.toByte())
            if (!tag.tagType.contains("Mifare")) {//ISO15693
                //连接标签或卡
                if (!connectISO15693Tag(tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                //获取EAS
                val result = iso15693Interface.NXPICODESLI_EASCheck(easByteArray[0])
                //断开与标签或卡的连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "获取EAS失败"
                    return@launch
                }
                isSuccess = true
            }
        }
        callback.invoke(isSuccess, failReason, TagInfo(tag = tag, eas = easByteArray[0]))
    }

    override fun modifyTagInfo(
        tagInfo: TagInfo,
        data: String,
        iso15693StartBlockAddress: Int,
        s50StartBlockAddress: Int,
        s50Key: String,
        s50KeyType: String,
        addressMode: Int,
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
        super.modifyTagInfo(
            tagInfo,
            data,
            iso15693StartBlockAddress,
            s50StartBlockAddress,
            s50Key,
            s50KeyType,
            addressMode,
            modifyResult
        )
        var isSuccess = false
        var failReason = ""
        if (!readerInterface.isReaderOpen || tagInfo.tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            modifyResult.invoke(
                isSuccess,
                failReason,
                tagInfo,
                data,
                iso15693StartBlockAddress,
                s50StartBlockAddress,
                s50Key,
                s50KeyType,
                addressMode
            )
            return
        }
        launchScope.launch {
            if (tagInfo.tag.uuidStr.isEmpty()) {
                failReason = "标签或卡uuid为空"
                return@launch
            }
            val dataByte = decodeHex(data)
            //设置天线id，即指定哪个天线操作标签数据
            readerInterface.RDR_SetAcessAntenna(tagInfo.tag.antennaId.toByte())
            if (!tagInfo.tag.tagType.contains("Mifare")) {//ISO15693
                if (dataByte.size != 4 * tagInfo.numOfBlocks.toInt()) {
                    failReason = "数据块大小不是区块数的4倍"
                    return@launch
                }
                //连接标签或卡
                if (!connectISO15693Tag(tagInfo.tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                //写入数据
                val result = iso15693Interface.ISO15693_WriteMultipleBlocks(
                    iso15693StartBlockAddress,
                    tagInfo.numOfBlocks.toInt(),
                    dataByte
                )
                //断开连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "写入数据块失败"
                    return@launch
                }
                isSuccess = true
            }
            if (tagInfo.tag.tagType.contains("Mifare")) {//ISO14443A
                val uuid = decodeHex(tagInfo.tag.uuidStr)
                if (tagInfo.tag.tagType.contains("Ultralight")) {
                    if (dataByte.size != 4 * tagInfo.numOfBlocks.toInt()) {
                        failReason = "数据块大小不是区块数的4倍"
                        return@launch
                    }
                    //连接标签或卡
                    var result = iso14443AInterface.ULTRALIGHT_Connect(readerInterface, uuid)
                    if (result != ApiErrDefinition.NO_ERROR) {
                        failReason = "与标签/卡通信失败"
                        return@launch
                    }
                    //写入数据
                    result = iso14443AInterface.ULTRALIGHT_WriteMultiplePages(
                        s50StartBlockAddress,
                        tagInfo.numOfBlocks.toInt(),
                        dataByte,
                        dataByte.size
                    )
                    //断开连接
                    iso14443AInterface.ISO14443A_Disconnect()
                    if (result != ApiErrDefinition.NO_ERROR) {
                        failReason = "写入数据块失败"
                        return@launch
                    }
                    isSuccess = true
                }
                if (tagInfo.tag.tagType.contains("S50")) {
                    val key = decodeHex(s50Key)
                    if (key.size != 6) {
                        failReason = "S50标签/卡的秘钥错误"
                        return@launch
                    }
                    if (dataByte.size != 16) {
                        failReason = "数据块大小不是16"
                        return@launch
                    }
                    //连接标签或卡
                    var result = iso14443AInterface.MFCL_Connect(readerInterface, 0.toByte(), uuid)
                    if (result != ApiErrDefinition.NO_ERROR) {
                        failReason = "与标签/卡通信失败"
                        return@launch
                    }
                    //认证标签或卡
                    result = iso14443AInterface.MFCL_Authenticate(
                        s50StartBlockAddress.toByte(),
                        s50KeyType.toByte(),
                        key
                    )
                    if (result != ApiErrDefinition.NO_ERROR) {
                        //断开连接
                        iso14443AInterface.ISO14443A_Disconnect()
                        failReason = "与标签/卡认证失败"
                        return@launch
                    }
                    //写入数据
                    result =
                        iso14443AInterface.MFCL_WriteBlock(s50StartBlockAddress.toByte(), dataByte)
                    //断开连接
                    iso14443AInterface.ISO14443A_Disconnect()
                    if (result != ApiErrDefinition.NO_ERROR) {
                        failReason = "写入数据块失败"
                        return@launch
                    }
                    isSuccess = true
                }
                if (tagInfo.tag.tagType.contains("S70")) {//暂时没有相关方法处理
                    failReason = "标签/卡的类型为S70，暂时没有相关方法处理"
                }
            }
        }

        modifyResult.invoke(
            isSuccess,
            failReason,
            tagInfo,
            data,
            iso15693StartBlockAddress,
            s50StartBlockAddress,
            s50Key,
            s50KeyType,
            addressMode
        )
    }

    override fun modifyAfi(
        tag: Tag,
        afi: String,
        addressMode: Int,
        modifyResult: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
        super.modifyAfi(tag, afi, addressMode, modifyResult)
        var isSuccess = false
        var failReason = ""
        val tagInfo = TagInfo(tag = tag)
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            modifyResult.invoke(isSuccess, failReason, tagInfo)
            return
        }
        launchScope.launch { //设置天线id，即指定哪个天线操作标签数据
            readerInterface.RDR_SetAcessAntenna(tag.antennaId.toByte())
            if (!tag.tagType.contains("Mifare")) {//ISO15693
                var tagAfi: Byte? = null
                //读取AFI，检查是否与要写入的AFI不同，如果相同则不修改直接返回
                getTagInfo(tag, addressMode) { isSuccess, failReason, tagInf ->
                    tagAfi =
                        if (isSuccess) {
                            tagInf.afi
                        } else {
                            null
                        }
                }
                if (afi.toByte(16) == tagAfi) {
                    isSuccess = true
                    return@launch
                }
                //连接标签或卡
                if (!connectISO15693Tag(tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                //写入AFI
                val afiByte = decodeHex(afi)
                val result = iso15693Interface.ISO15693_WriteAFI(afiByte[0])
                //断开与标签或卡的连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "写入AFI失败"
                    return@launch
                }
                tagInfo.afi = afiByte[0]
                isSuccess = true
            }
        }
        modifyResult.invoke(isSuccess, failReason, tagInfo)
    }

    override fun modifyEas(
        tag: Tag,
        eas: Byte,
        addressMode: Int,
        modifyResult: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
        super.modifyEas(tag, eas, addressMode, modifyResult)
        var isSuccess = false
        var failReason = ""
        val tagInfo = TagInfo(tag = tag)
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            modifyResult.invoke(isSuccess, failReason, tagInfo)
            return
        }
        launchScope.launch { //设置天线id，即指定哪个天线操作标签数据
            readerInterface.RDR_SetAcessAntenna(tag.antennaId.toByte())
            if (!tag.tagType.contains("Mifare")) {//ISO15693
                //连接标签或卡
                if (!connectISO15693Tag(tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                //写入EAS
                val result = when (eas) {
                    1.toByte() -> {
                        iso15693Interface.NXPICODESLI_EableEAS()
                    }
                    0.toByte() -> {
                        iso15693Interface.NXPICODESLI_DisableEAS()
                    }
                    else -> {
                        0
                    }
                }
                //断开与标签或卡的连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "写入EAS失败"
                    return@launch
                }
                tagInfo.eas = eas
                isSuccess = true
            }
        }
        modifyResult.invoke(isSuccess, failReason, tagInfo)
    }

    override fun modifyAfiAndEas(
        tag: Tag,
        afi: String,
        eas: Byte,
        addressMode: Int,
        modifyResult: (isSuccess: Boolean, failReason: String, tagInfo: TagInfo) -> Unit
    ) {
        super.modifyAfiAndEas(tag, afi, eas, addressMode, modifyResult)
        var isSuccess = false
        var failReason = ""
        val tagInfo = TagInfo(tag = tag)
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            failReason = "读写器没有打开，或标签/卡的uuid为空"
            modifyResult.invoke(isSuccess, failReason, tagInfo)
            return
        }
        launchScope.launch {
            //设置天线id，即指定哪个天线操作标签数据
            readerInterface.RDR_SetAcessAntenna(tag.antennaId.toByte())
            if (!tag.tagType.contains("Mifare")) {//ISO15693
                //连接标签或卡
                if (!connectISO15693Tag(tag, addressMode)) {
                    failReason = "与标签/卡通信失败"
                    return@launch
                }
                var result: Int

                //写入AFI
                val afiByte = decodeHex(afi)
                result = iso15693Interface.ISO15693_WriteAFI(afiByte[0])
                //断开与标签或卡的连接
                if (result != ApiErrDefinition.NO_ERROR) {
                    iso15693Interface.ISO15693_Disconnect()
                    failReason = "写入AFI失败"
                    return@launch
                }
                tagInfo.afi = afiByte[0]

                //写入EAS
                result = when (eas) {
                    1.toByte() -> {
                        iso15693Interface.NXPICODESLI_EableEAS()
                    }
                    0.toByte() -> {
                        iso15693Interface.NXPICODESLI_DisableEAS()
                    }
                    else -> {
                        ApiErrDefinition.NO_ERROR
                    }
                }
                //断开与标签或卡的连接
                iso15693Interface.ISO15693_Disconnect()
                if (result != ApiErrDefinition.NO_ERROR) {
                    failReason = "写入EAS失败"
                    return@launch
                }
                tagInfo.eas = eas
                isSuccess = true
            }
        }
        modifyResult.invoke(isSuccess, failReason, tagInfo)
    }

    /**
     * 连接ISO15693类型的标签或卡
     */
    private fun connectISO15693Tag(tag: Tag, addressMode: Int): Boolean {
        if (!readerInterface.isReaderOpen || tag.uuidStr.isEmpty()) {
            return false
        }
        //设置天线id，即指定哪个天线操作标签数据
        readerInterface.RDR_SetAcessAntenna(tag.antennaId.toByte())
        if (!tag.tagType.contains("Mifare")) {//ISO15693
            var tagType = RfidDef.RFID_ISO15693_PICC_ICODE_SLI_ID
            if (addressMode == 1 && tag.tagTypeLong > 0) {
                tagType = tag.tagTypeLong
            }
            var result = iso15693Interface.ISO15693_Connect(
                readerInterface,
                tagType,
                addressMode.toByte(),
                tag.rawUuid
            )
            if (result != ApiErrDefinition.NO_ERROR) {
                return false
            }
            if (addressMode == 0) {//无地址模式要重置
                result = iso15693Interface.ISO15693_Reset()
                if (result != ApiErrDefinition.NO_ERROR) {
                    return false
                }
            }
            return true
        }
        return false
    }

}