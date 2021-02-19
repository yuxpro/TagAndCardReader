package com.tuodi.tagandcardreader.coderDecoder.iso15962;

public class ProcessDataModel {
    private int oid;
    private int dataType;
    private boolean offset;
    private String data;
    private String offsetData = "";

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "ProcessDataModel{" +
                "oid=" + oid +
                ", dataType=" + dataType +
                ", offset=" + offset +
                '}';
    }

    public boolean isOffset() {
        return offset;
    }

    public void setOffset(boolean offset) {
        this.offset = offset;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOffsetData() {
        return offsetData;
    }

    public void setOffsetData(String offsetData) {
        this.offsetData = offsetData;
    }
}
