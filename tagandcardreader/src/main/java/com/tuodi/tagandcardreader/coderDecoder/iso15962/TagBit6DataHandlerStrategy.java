package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import android.util.Log;

import com.example.commonlib.utils.CommonUtil;


public class TagBit6DataHandlerStrategy implements TagDataHandlerStrategy {
    private static final String TAG = "TagBit6DataHandlerStrat";

    public static String GIVE_UP_BIT_6_1 = "10";
    public static String GIVE_UP_BIT_6_2 = "1000";
    public static String GIVE_UP_BIT_6_3 = "100000";
    public static String GIVE_UP_BIT_6_FIRST_1 = "1";
    public static String GIVE_UP_BIT_6_FIRST_0 = "0";
    public static String GIVE_UP_BIT_6_FIRST_00 = "00";
    public static String GIVE_UP_BIT_6_FIRST_01 = "01";

    @Override
    public String handleData(byte[] data, int type) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            stringBuffer.append(CommonUtil.hexStringToBinary(CommonUtil.Byte2Hex(data[i])));
        }
        String dataString = stringBuffer.toString();

        return handlerBit6(dataString);

    }

    @Override
    public String processData(String data) {
        return processDataBit6(data);
    }

    private String handlerBit6(String dataString) {
        int dataLenght = dataString.length();
        //int index = dataLenght / 6 + (dataLenght % 6) > 0 ? 1 : 0;//判断不明作用
        int index = dataLenght / 6 + ((dataLenght % 6) > 0 ? 1 : 0);
        String bitData = "";
        String bitDatafirst = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i <= index; i++) {
            int endIndex = i * 6;
            bitData = dataString.substring((i - 1) * 6, endIndex > dataLenght ? dataLenght : endIndex);
            if (i == index) {
                //判断是否遇到最后填充数据(结束标识)
                if (bitData.equals(GIVE_UP_BIT_6_1) || bitData.equals(GIVE_UP_BIT_6_2) ||
                        bitData.equals(GIVE_UP_BIT_6_3)) {

                    continue;
                }

            }
            bitDatafirst = bitData.substring(0, 1);
            //            if (TextUtils.equals(bitDatafirst, GIVE_UP_BIT_6_FIRST_1)) {
            //如第一位bit为"1",前补"00",如为"0",前补"01"
            if (bitDatafirst.equals(GIVE_UP_BIT_6_FIRST_1)) {
                bitData = GIVE_UP_BIT_6_FIRST_00 + bitData;
            } else {
                bitData = GIVE_UP_BIT_6_FIRST_01 + bitData;
            }
            stringBuffer.append(CommonUtil.binaryStringToHex(bitData));
        }
        return CommonUtil.convertHexToString(stringBuffer.toString());
        //return stringBuffer.toString();
    }

    private static int minValue = 32;
    private static int maxValue = 95;

    public String processDataBit6(String data) {
        if ("".equals(data) || data == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        byte[] dataBytes = data.getBytes();
        for (byte dataByte : dataBytes) {
            int intNum = CommonUtil.byte2int(dataByte);
            if (intNum >= minValue || intNum <= maxValue) {
                String binaryString = CommonUtil.hexStringToBinary(CommonUtil.Byte2Hex(dataByte));
                //截取最高位两位bit
                stringBuffer.append(binaryString.substring(2, binaryString.length()));
            } else {
                Log.e(TAG, "processDataBit6: 数据格式错误");
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
            //补位
            tranString += GIVE_UP_BIT_6_3.substring(0, 8 - remainder);
            lenght += 8 - remainder;//补位后补充长度
        }

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
