package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import com.example.commonlib.utils.CommonUtil;

public class OidDataModel {

    /**
     * 格式:1byte前导字节+ (1byte oid补充长度字节) + (1byte 偏移x长度字节)+ 1byte数据长度y + y个byte具体数据段 + (x个byte偏移补充数据块)
     * 前导字节:1byte=8bit , 高→低, 1 2 3 4 5 6 7 8 ,1位标识是否偏移(偏移置为"1"),
     * 2-4位标识数据解析类型,5-8位标识oid值(若5-8均为"1",前导字节下一字节为oid值补充,oid为15也需补充0x00),
     * oid补充长度:补充前导字节中的oid值,数据段oid值=前导字节oid值+补充oid值
     * 偏移:偏移使数据段长度为4的倍数
     * 偏移x长度字节:当前导字节最高位置为"1",偏移长度字节就必须有,根据最终偏移量决定偏移值,可为0x00
     * 数据长度字节:标识数据段中用于解析的数据块长度,不包括偏移补充数据
     * 具体数据段:长度为数据长度字节标识,解析方式由前导字节的2-4位标识
     * 偏移补充数据块:长度为偏移长度字节标识
     * 例子:61095444303030303436310201B8030A1B81E320601A01AC751905011266010067010000000000000000000000000000000000
     */
    private String leadCode;
    private String oidSupplement;
    private String offsetLength;
    private String dataLength;
    private String data;
    private String offsetDataSupplement;

    public String getLeadCode() {
        return leadCode;
    }

    public void setLeadCode(String leadCode) {
        this.leadCode = leadCode;
    }

    public String getOidSupplement() {
        return oidSupplement;
    }

    public void setOidSupplement(String oidSupplement) {
        this.oidSupplement = oidSupplement;
    }

    public String getOffsetLength() {
        return offsetLength;
    }

    public void setOffsetLength(String offsetLength) {
        this.offsetLength = offsetLength;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOffsetDataSupplement() {
        return offsetDataSupplement;
    }

    public void setOffsetDataSupplement(String offsetDataSupplement) {
        this.offsetDataSupplement = offsetDataSupplement;
    }


    public void handlerOidData(ProcessDataModel processDataModel) {
        if (processDataModel == null || this == null) {
            return;
        }

        int oid = processDataModel.getOid();
        int oidOffest = oid - 15;
        int dataType = processDataModel.getDataType();
        boolean offset = processDataModel.isOffset();

        if (oidOffest > 0) {
            oidOffest = oidOffest > 15 ? 15 : oidOffest;
            this.setOidSupplement(CommonUtil.Byte2Hex((byte) oidOffest));
            oid = 15;
        }


        if (offset) {
            dataType += 8;
        }
        this.setLeadCode(CommonUtil.Byte1Hex(dataType) + CommonUtil.Byte1Hex(oid));
    }

    @Override
    public String toString() {
        return "OidDataModel{" +
                "leadCode='" + leadCode + '\'' +
                ", oidSupplement='" + oidSupplement + '\'' +
                ", offsetLength='" + offsetLength + '\'' +
                ", dataLength='" + dataLength + '\'' +
                ", data='" + data + '\'' +
                ", offsetDataSupplement='" + offsetDataSupplement + '\'' +
                '}';
    }

    public String format() {
        return leadCode + oidSupplement + offsetLength + dataLength + data + offsetDataSupplement;
    }

}
