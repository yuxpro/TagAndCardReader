package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import com.rfid.api.GFunction;

import java.util.Arrays;

/**
 * Role:
 * Author: Pbin
 * Date: 2020/8/11 9:20
 */
public class BaseTagModel {
    protected int tagType;

    /**
     * tagType为1:RFID , 为2:IC , 为3:超高频
     */
    public BaseTagModel(int tagType) {
        this.tagType = tagType;
    }

    //uuid
    //apiId
    public static final int ISO15693 = 1;
    public static final int ISO14443A = 2;

    //tagId
    public static final int NXP_ICODE_SLI = 1;//SL2S2001
    public static final int Tag_it_HF_I_plus = 2;
    public static final int ST_M24LRxx = 3;//ST M24LR 系列
    public static final int Fujitsu_MB89R118C = 4;
    public static final int ST_M24LR64 = 5;
    public static final int ST_M24LR16E = 6;
    public static final int NXP_ICODE_SLIX = 7;//SL2S2002 / SL2S2102
    public static final int Tag_it_HF_I_Standard = 8;
    public static final int Tag_it_HF_I_Pro = 9;

    private byte[] uuid;

    private long rtnID;

    private long aipId;//协议类型

    private long tagId;//标签类型

    //获取协议类型字符串
    public String getAipStr() {
        switch ((int) aipId) {
            case ISO15693:
                return "ISO15693";
            case ISO14443A:
                return "ISO14443A";
            default:
                return "";
        }
    }

    //获取标签类型字符串
    public String getTagStr() {
        switch ((int) tagId) {
            case NXP_ICODE_SLI:
                return "NXP ICODE SLI";
            case Tag_it_HF_I_plus:
                return "Tag-it HF-I plus";
            case ST_M24LRxx:
                return "ST M24LRxx";
            case Fujitsu_MB89R118C:
                return "Fujitsu MB89R118C";
            case ST_M24LR64:
                return "ST M24LR64";
            case ST_M24LR16E:
                return "ST M24LR16E";
            case NXP_ICODE_SLIX:
                return "NXP ICODE SLIX";
            case Tag_it_HF_I_Standard:
                return "Tag-it HF-I Standard";
            case Tag_it_HF_I_Pro:
                return "Tag-it HF-I Pro";
            default:
                return "";
        }
    }

    public BaseTagModel(byte[] uuid, long rtnID) {

        this.uuid = uuid;
        this.rtnID = rtnID;
    }

    public BaseTagModel(byte[] uuid) {

        this.uuid = uuid;
    }

    public long getRtnID() {
        return rtnID;
    }

    public void setRtnID(long rtnID) {
        this.rtnID = rtnID;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }

    public long getAipId() {
        return aipId;
    }

    public void setAipId(long aipId) {
        this.aipId = aipId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj)
            return false;

        if (!(obj instanceof BaseTagModel)) {
            return false;
        }

        BaseTagModel rawUuid = (BaseTagModel) obj;
        return Arrays.equals(uuid, rawUuid.uuid);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(uuid);
    }

    //uhf
    public String epc;
    public String tag;
    public long dataLenght;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        //        return null == uuid ? "" : StringUtil.ByteArrToHexToNoNULL(uuid);
        if (tagType == 3) {
            return tag;
        }
        return null == uuid ? "" : GFunction.encodeHexStr(uuid);
    }

}