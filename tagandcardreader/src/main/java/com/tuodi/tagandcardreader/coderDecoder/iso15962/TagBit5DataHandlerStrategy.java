package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import android.util.Log;

import com.example.commonlib.utils.CommonUtil;


public class TagBit5DataHandlerStrategy implements TagDataHandlerStrategy {
    private static final String TAG = "TagBit5DataHandlerStrat";
    public static String GIVE_UP_BIT_5_FIRST = "010";

    @Override
    public String handleData(byte[] data, int type) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            stringBuffer.append(CommonUtil.hexStringToBinary(CommonUtil.Byte2Hex(data[i])));
        }
        String dataString = stringBuffer.toString();

        return handlerBit5(dataString);

    }

    @Override
    public String processData(String data) {

        return processDataBit5(data);
    }


    private String handlerBit5(String dataString) {
        int dataLenght = dataString.length();
        int index = dataLenght / 5 + ((dataLenght % 5) > 0 ? 1 : 0);
        String bitData = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i <= index; i++) {
            int endIndex = i * 5;
            bitData = dataString.substring((i - 1) * 5, endIndex > dataLenght ? dataLenght : endIndex);
            if (i == index) {
                if (!bitData.contains("1"))
                    continue;
            }
            bitData = GIVE_UP_BIT_5_FIRST + bitData;
            stringBuffer.append(CommonUtil.binaryStringToHex(bitData));
        }

        return CommonUtil.convertHexToString(stringBuffer.toString());
    }

    private static int minValue = 65;
    private static int maxValue = 95;

    public String processDataBit5(String data) {
        if ("".equals(data) || data == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        byte[] dataBytes = data.getBytes();
        for (byte dataByte : dataBytes) {
            int intNum = CommonUtil.byte2int(dataByte);
            if (intNum >= minValue || intNum <= maxValue) {
                String binaryString = CommonUtil.hexStringToBinary(CommonUtil.Byte2Hex(dataByte));
                stringBuffer.append(binaryString.substring(3, binaryString.length()));
            } else {
                Log.e(TAG, "processDataBit5: 数据格式错误");
                //                break;
                return "";
            }
        }
        String tranString = stringBuffer.toString();
        int lenght = tranString.length();
        int remainder = (lenght % 8);
        int index = lenght / 8;
        //判断余数添加0
        if (remainder > 0) {
            index++;
            for (int i = 0; i < 8 - remainder; i++) {
                tranString += "0";
            }
        }

        lenght += 8 - remainder;
        String bitData = "";
        stringBuffer.setLength(0);
        for (int i = 1; i <= index; i++) {
            int endIndex = i * 8;
            bitData = tranString.substring((i - 1) * 8, endIndex > lenght ? lenght : endIndex);
            stringBuffer.append(CommonUtil.binaryStringToHex(bitData));
        }
        return stringBuffer.toString();
    }
}
