package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import android.util.Log;

import com.example.commonlib.utils.CommonUtil;

import java.util.List;

public class TagStrategyController {
    private TagDataHandlerStrategy msgStrategy;
    private static final String TAG = "TagStrategyController";

    public TagStrategyController() {
    }

    public String handleMsg(byte[] data, int type) {
        msgStrategy = TagStrategyFactory.getInstance().creator(String.valueOf(type));
        return this.msgStrategy.handleData(data, type);
    }

    public byte[] processData(List<ProcessDataModel> processDataModels) {
        OidDataModel oidDataModel = null;
        StringBuffer stringBuffer = new StringBuffer();
        for (ProcessDataModel processDataModel : processDataModels) {
            msgStrategy = TagStrategyFactory.getInstance().creator(String.valueOf(processDataModel.getDataType()));
            oidDataModel = new OidDataModel();
            oidDataModel.handlerOidData(processDataModel);
            oidDataHandler(processDataModel, oidDataModel, msgStrategy);
            Log.i(TAG, "processData: " + oidDataModel.format());
            stringBuffer.append(oidDataModel.format());
        }
        Log.i(TAG, "processData: " + CommonUtil.hex2byte(stringBuffer.toString().replace("null", "")));
        return CommonUtil.hex2byte(stringBuffer.toString().replace("null", ""));
    }

    private void oidDataHandler(ProcessDataModel processDataModel, OidDataModel oidDataModel, TagDataHandlerStrategy msgStrategy) {
        if (processDataModel == null || oidDataModel == null) {
            return;
        }
        String data = processDataModel.getData();
        /**
         * 偏移不应这样计算,长度应最后定,数据填充使用0x00填充
         */
        /*String offsetData = processDataModel.getOffsetData();
        if (!("".equals(offsetData)||offsetData==null)) {
            oidDataModel.setOffsetLength(StringUtil.Byte2Hex((byte) HardwareUtil.getByteLenght(offsetData)));
            oidDataModel.setOffsetDataSupplement(msgStrategy.processData(
                    offsetData));
        }*/

        int allDataLength = 2;//前导字节1+数据长度标识1
        if (!("".equals(data) || data == null)) {
            //oidDataModel.setDataLength(StringUtil.Byte2Hex((byte) HardwareUtil.getByteLenght(data)));
            oidDataModel.setData(msgStrategy.processData(
                    data));
            allDataLength += oidDataModel.getData().length() / 2;
            byte length = (byte) (oidDataModel.getData().length() / 2);
            oidDataModel.setDataLength(Byte2Hex(length));
            Log.i(TAG, "oidDataHandler: ");
            //oidDataModel.setDataLength(oidDataModel.getData().length()/2<10?"0"+oidDataModel.getData().length()/2:oidDataModel.getData().length()/2+"");
        }

        if (processDataModel.isOffset()) {
            allDataLength++;//偏移长度标识
            if (processDataModel.getOid() > 14) {
                allDataLength++;//oid超出长度标识
            }
            int offsetLength = 4 - (allDataLength % 4) == 4 ? 0 : 4 - (allDataLength % 4);
            oidDataModel.setOffsetLength("0" + offsetLength);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < offsetLength; i++) {
                sb.append("00");
            }
            oidDataModel.setOffsetDataSupplement(sb.toString());
        }
    }

    public static String Byte2Hex(byte b) {
        String strHex = Integer.toHexString(b & 0xFF).toUpperCase();
        return strHex.length() == 1 ? "0" + strHex : strHex;

    }

    public static String ByteArrToHex(byte[] datas) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte data : datas) {
            stringBuilder.append(Byte2Hex(data));
        }
        return stringBuilder.toString();
    }


}
