package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import android.text.TextUtils;
import android.util.Log;

import com.example.commonlib.utils.CommonUtil;


public class TagBit7DataHandlerStrategy implements TagDataHandlerStrategy {
    private static final String TAG = "TagBit7DataHandlerStrat";
    public static String GIVE_UP_BIT_7_FIRST = "0";

    @Override
    public String handleData(byte[] data, int type) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            stringBuffer.append(CommonUtil.hexStringToBinary(CommonUtil.Byte2Hex(data[i])));
        }
        String dataString = stringBuffer.toString();

        return handlerBit7(dataString);

    }

    @Override
    public String processData(String data) {
        return processDataBit7(data);
    }

    private String handlerBit7(String dataString) {
        int dataLenght = dataString.length();
        int index = dataLenght / 7 + ((dataLenght % 7) > 0 ? 1 : 0);
        String bitData = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i <= index; i++) {
            int endIndex = i * 7;
            bitData = dataString.substring((i - 1) * 7, endIndex > dataLenght ? dataLenght : endIndex);
            if (i == index) {
                if (TextUtils.equals("1", bitData.length()>0?bitData.substring(0,1):bitData))
                    continue;
            }
            bitData = GIVE_UP_BIT_7_FIRST + bitData;
            stringBuffer.append(CommonUtil.binaryStringToHex(bitData));
        }
        return CommonUtil.convertHexToString(stringBuffer.toString());
        //return stringBuffer.toString();
    }

    private static int minValue = 0;
    private static int maxValue = 126;
    public String processDataBit7(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        byte[] dataBytes = data.getBytes();
        for (byte dataByte : dataBytes) {
            int intNum = CommonUtil.byte2int(dataByte);
            if (intNum >= minValue || intNum <= maxValue) {
                String binaryString = CommonUtil.hexStringToBinary(CommonUtil.Byte2Hex(dataByte));
                stringBuffer.append(binaryString.substring(1, binaryString.length()));
            } else {
                Log.e(TAG, "processDataBit5: 数据格式错误");
//                break;
                return "";
            }
        }
        String tranString = stringBuffer.toString();
        int lenght = tranString.length();
        int remainder = 8-(lenght % 8);
        int index = lenght / 8;
        //判断余数添加0
        if (remainder > 0) {
            index++;
            for (int i = 0; i < remainder; i++) {
                tranString += "1";
            }
        }
        lenght=tranString.length();
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
