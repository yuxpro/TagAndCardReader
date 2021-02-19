package com.tuodi.tagandcardreader.andiReader

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @ClassName:      data$
 * @Description:     java类作用描述
 * @Author:         yuan xin
 * @CreateDate:     2020/12/10 0010$
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/12/10 0010$
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */

@Parcelize
data class Tag(
        var rawUuid: ByteArray = ByteArray(8),
        var uuidStr: String = "",
        var antennaId:Long=0L,//哪个天线盘点到该标签
        var tagType: String = "",//转换的标签类型
        var tagTypeLong: Long = -1//原始标签类型：1L~10L,20L
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tag) return false

        if (!rawUuid.contentEquals(other.rawUuid)) return false
        if (uuidStr != other.uuidStr) return false
        if (antennaId != other.antennaId) return false
        if (tagType != other.tagType) return false
        if (tagTypeLong != other.tagTypeLong) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rawUuid.contentHashCode()
        result = 31 * result + uuidStr.hashCode()
        result = 31 * result + antennaId.hashCode()
        result = 31 * result + tagType.hashCode()
        result = 31 * result + tagTypeLong.hashCode()
        return result
    }

}

data class TagInfo(
    var tag: Tag = Tag(),
    var dataBlock: ByteArray = ByteArray(8),//标签/卡的数据块
    var afi: Byte = 0,
    var eas: Byte = -1,
    var blockSize: Long = 0,
    var numOfBlocks: Long = 0,
    var dsfid: Byte = 0,
    var icRef: Byte = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TagInfo) return false

        if (tag != other.tag) return false
        if (!dataBlock.contentEquals(other.dataBlock)) return false
        if (afi != other.afi) return false
        if (eas != other.eas) return false
        if (blockSize != other.blockSize) return false
        if (numOfBlocks != other.numOfBlocks) return false
        if (dsfid != other.dsfid) return false
        if (icRef != other.icRef) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + dataBlock.contentHashCode()
        result = 31 * result + afi
        result = 31 * result + eas
        result = 31 * result + blockSize.hashCode()
        result = 31 * result + numOfBlocks.hashCode()
        result = 31 * result + dsfid
        result = 31 * result + icRef
        return result
    }

}

