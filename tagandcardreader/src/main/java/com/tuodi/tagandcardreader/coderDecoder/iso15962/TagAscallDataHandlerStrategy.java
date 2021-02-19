package com.tuodi.tagandcardreader.coderDecoder.iso15962;


import com.example.commonlib.utils.CommonUtil;

public class TagAscallDataHandlerStrategy implements TagDataHandlerStrategy {
    @Override
    public String handleData(byte[] data, int type) {
        return new String(data);
    }

    @Override
    public String processData(String data) {
        if ("".equals(data) || data == null) {
            return "";
        }
        return CommonUtil.ByteArrToHexToNoNULL(
                data.getBytes());
    }

   /* private void oidDataHandler(ProcessDataModel processDataModel, OidDataModel oidDataModel) {
        if (processDataModel == null || oidDataModel == null) {
            return;
        }
        String data = processDataModel.getData();
        String offsetData = processDataModel.getOffsetData();
        if (!TextUtils.isEmpty(offsetData)) {
            oidDataModel.setOffsetLength(StringUtil.Byte2Hex((byte) HardwareUtil.getByteLenght(offsetData)));
            oidDataModel.setOffsetDataSupplement(StringUtil.ByteArrToHexToNoNULL(
                    offsetData.getBytes()));
        }

        if (!TextUtils.isEmpty(data)) {
            oidDataModel.setDataLength(StringUtil.Byte2Hex((byte) HardwareUtil.getByteLenght(data)));
            oidDataModel.setData(StringUtil.ByteArrToHexToNoNULL(
                    data.getBytes()));
        }
    }*/

}

