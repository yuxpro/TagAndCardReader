package com.tuodi.tagandcardreader.andiReader;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xiaosheng on 2018/9/29.
 */

public class CommonUtil {
    /**
     * bytes数组顺序对换
     */
    public static byte[] byteReverse(byte[] datas) {
        int length = datas.length;
        if (length == 0) {
            return datas;
        }
        byte[] dataChange = new byte[length];
        int index = 0;
        for (int i = length - 1; i >= 0; i--) {
            dataChange[index] = datas[i];
            index++;
        }
        return dataChange;
    }

    //btye转int
    public static int byte2int(byte data) {
        return data & 0xff;
    }

    /**
     * 一个btye转化为String数组
     */
    public static String[] byteToStrings(byte data) {
        String[] hexStrings = new String[2];
        String hexString = Byte2Hex(data);
        for (int i = 0; i < 2; i++) {
            String mData = hexString.substring(i, i + 1);
            hexStrings[i] = mData;
        }
        return hexStrings;
    }

    /**
     * 16进制转ASCII码
     */
    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }

    /**
     * CRC16校验
     */
    public static byte[] getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        String CRCString = Integer.toHexString(CRC);
        byte[] data1 = hex2byte(CRCString);
        byte[] data2 = new byte[2];
        if (data1.length >= 2) {
            data2[0] = data1[1];
            data2[1] = data1[0];
        }
        return data2;
    }

    /**
     * 十六进制串转化为byte数组
     *
     * @return the array of byte
     */
    public static final byte[] hex2byte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    public static byte[] int2Bytes(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[4 - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    //判断两个byte是否相等
    public static boolean isByteEqual(byte a, byte b) {
        if (byte2int(a) == byte2int(b)) {
            return true;
        }
        return false;
    }

    public static String ByteArrToHex(byte[] datas) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte data : datas) {
            stringBuilder.append(Byte2Hex(data));
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static byte[] StringToHexByte(String byteData) {
        String hexString = byteData;
        byte[] hexByte;
        if (isOdd(byteData) == 1) {
            hexString = "0" + byteData;
        } else {
        }
        int byteLenght = hexString.length();
        hexByte = new byte[byteLenght / 2 + 1];
        int index = 0;
        for (int i = 0; i < byteLenght; i += 2) {
            hexByte[index] = HexToByte(hexString.substring(i, i + 2));
            index++;
        }
        return hexByte;
    }

    public static byte HexToByte(String substring) {
        return (byte) Integer.parseInt(substring, 16);
    }


    private static int isOdd(String byteData) {
        return byteData.length() & 0x1;
    }


    //十六转二
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));

            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static String ByteArrToHexToNoNULL(byte[] datas) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte data : datas) {
            stringBuilder.append(Byte2Hex(data));
        }
        return stringBuilder.toString();
    }

    /**
     * byte转kb mb
     */
    public static String humanReadableBytes(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.2f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * 8位二进制字符串转16进制字符串
     */
    public static String binaryStringToHex(String binary) {
        String result = "";
        if (binary.length() != 8) {
            return "00";
        }
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            char c = binary.charAt(i);
            //2的幂次方
            sum += ((int) c - 48) * Math.pow(2, Math.abs(3 - (i > 3 ? i - 4 : i)));
            if ((i + 1) % 4 == 0) {
                result += Integer.toHexString(sum);
                sum = 0;
            }
        }
        return result.toUpperCase();
    }

    /**
     * ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     * 安的读写器专用 GFunction
     * ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, int dataLen) {
        return encodeHex(data, dataLen, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static char[] encodeHex(byte[] data, int dataLen, boolean toLowerCase) {
        return encodeHex(data, dataLen, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int len = data.length;
        char[] out = new char[len << 1];
        int i = 0;

        for (int var5 = 0; i < len; ++i) {
            out[var5++] = toDigits[(240 & data[i]) >>> 4];
            out[var5++] = toDigits[15 & data[i]];
        }

        return out;
    }

    protected static char[] encodeHex(byte[] data, int dataLen, char[] toDigits) {
        int len = dataLen;
        char[] out = new char[dataLen << 1];
        int i = 0;

        for (int var6 = 0; i < len; ++i) {
            out[var6++] = toDigits[(240 & data[i]) >>> 4];
            out[var6++] = toDigits[15 & data[i]];
        }

        return out;
    }

    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, false);
    }

    public static String encodeHexStr(byte[] data, int dataLen) {
        return encodeHexStr(data, dataLen, false);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String encodeHexStr(byte[] data, int dataLen, boolean toLowerCase) {
        return encodeHexStr(data, dataLen, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    protected static String encodeHexStr(byte[] data, int dataLen, char[] toDigits) {
        return new String(encodeHex(data, dataLen, toDigits));
    }

    public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 1) != 0) {
            return null;
        } else {
            byte[] out = new byte[len >> 1];
            int i = 0;

            for (int j = 0; j < len; ++i) {
                int f1 = toDigit(data[j], j) << 4;
                if (f1 < 0) {
                    return null;
                }

                ++j;
                int f2 = toDigit(data[j], j);
                if (f2 < 0) {
                    return null;
                }

                ++j;
                out[i] = (byte) ((f1 | f2) & 255);
            }

            return out;
        }
    }

    public static byte[] decodeHex(char[] data, int dataLen) {
        int len = dataLen;
        if ((dataLen & 1) != 0) {
            return null;
        } else {
            byte[] out = new byte[dataLen >> 1];
            int i = 0;

            for (int j = 0; j < len; ++i) {
                int f1 = toDigit(data[j], j) << 4;
                if (f1 < 0) {
                    return null;
                }

                ++j;
                int f2 = toDigit(data[j], j);
                if (f2 < 0) {
                    return null;
                }

                ++j;
                out[i] = (byte) ((f1 | f2) & 255);
            }

            return out;
        }
    }

    public static byte[] decodeHex(String sData) {
        if (sData.equals("")) {
            return null;
        } else {
            char[] data = sData.toCharArray();
            int len = data.length;
            if ((len & 1) != 0) {
                return null;
            } else {
                byte[] out = new byte[len >> 1];
                int i = 0;

                for (int j = 0; j < len; ++i) {
                    int t1 = toDigit(data[j], j) << 4;
                    if (t1 < 0) {
                        return null;
                    }

                    ++j;
                    int t2 = toDigit(data[j], j);
                    if (t2 < 0) {
                        return null;
                    }

                    ++j;
                    out[i] = (byte) ((t1 | t2) & 255);
                }

                return out;
            }
        }
    }

    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        return digit;
    }

    /**
     * 十六转二进制
     *
     * @param hex 十六进制字符串
     * @return 二进制字符串
     */
    public static String hexStringToBinary(String hex) {
        hex = hex.toUpperCase();
        String result = "";
        int max = hex.length();
        for (int i = 0; i < max; i++) {
            char c = hex.charAt(i);
            switch (c) {
                case '0':
                    result += "0000";
                    break;
                case '1':
                    result += "0001";
                    break;
                case '2':
                    result += "0010";
                    break;
                case '3':
                    result += "0011";
                    break;
                case '4':
                    result += "0100";
                    break;
                case '5':
                    result += "0101";
                    break;
                case '6':
                    result += "0110";
                    break;
                case '7':
                    result += "0111";
                    break;
                case '8':
                    result += "1000";
                    break;
                case '9':
                    result += "1001";
                    break;
                case 'A':
                    result += "1010";
                    break;
                case 'B':
                    result += "1011";
                    break;
                case 'C':
                    result += "1100";
                    break;
                case 'D':
                    result += "1101";
                    break;
                case 'E':
                    result += "1110";
                    break;
                case 'F':
                    result += "1111";
                    break;
            }
        }
        return result;
    }

    //int转byte
    public static byte[] IntToByteArray(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static String hexStr2Ascii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static int hexStr2int(String hexStr) {
        int a;
        switch (hexStr) {
            case "A":
                a = 10;
                break;
            case "B":
                a = 11;
                break;
            case "C":
                a = 12;
                break;
            case "D":
                a = 13;
                break;
            case "E":
                a = 14;
                break;
            case "F":
                a = 15;
                break;
            default:
                a = Integer.parseInt(hexStr);
                break;
        }
        return a;
    }

    public static int ByteToInt32(byte[] src) {
        if ((src != null) && (src.length <= 4)) {
            byte[] array = new byte[4];
            System.arraycopy(src, 0, array, 0, src.length);
            return bytesToInt(array, 0);
        }
        return 0;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (((src[offset] & 0x000000FF) << 24)
                + ((src[offset + 1] & 0x000000FF) << 16)
                + ((src[offset + 2] & 0x000000FF) << 8)
                + ((src[offset + 3] & 0x000000FF)));
        return value;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    public static String bytes2string(byte[] bs) {
        char[] cs = new char[bs.length];
        for (int p = 0; p < bs.length; p++) {
            cs[p] = (char) (bs[p] & 0xFF);
        }
        return new String(cs);
    }

    //判断字符串仅有数字字母
    public static boolean isLetterDigit(String str) {
        if (str == null) {
            return false;
        }
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    public static String Byte2Hex(byte inByte) {
        String strHex = Integer.toHexString(inByte & 0xFF).toUpperCase();
        return strHex.length() == 1 ? "0" + strHex : strHex;
    }

    public static byte[] replaceZero(byte[] bytes) {
        int size = 0;
        byte[] datas = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0x00) {
                //buffer.put(bytes[i]);
                datas[size] = bytes[i];
                size++;
            }
        }

        byte[] after = new byte[size];
        System.arraycopy(datas, 0, after, 0, after.length);
        return after;
    }

    public static List<String> getDiffrentStrList(List<String> list1, List<String> list2) {
        List<String> diff = new ArrayList<String>();
        List<String> maxList = list1;
        List<String> minList = list2;
        if (list2.size() > list1.size()) {
            maxList = list2;
            minList = list1;
        }
        Map<String, Integer> map = new HashMap<String, Integer>(maxList.size());
        for (String string : maxList) {
            map.put(string, 1);
        }
        for (String string : minList) {
            if (map.get(string) != null) {
                map.put(string, 2);
                continue;
            }
            diff.add(string);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        return diff;

    }

    public static byte[] subByteArray(byte[] data, int start) {
        return subByteArray(data, start, data.length);
    }

    public static byte[] subByteArray(byte[] data, int start, int end) {
        byte[] bytes = new byte[end - start];
        System.arraycopy(data, start, bytes, 0, bytes.length);
        return bytes;
    }

    public static byte[] clearZero(byte[] data) {
        String hexStr = encodeHexStr(data);
        hexStr = hexStr.replaceAll("(00)+$", "");
        return decodeHex(hexStr);
    }

    public static String int2hexStr(int oidInt) {
        String oidStr;
        switch (oidInt) {
            case 10:
                oidStr = "a";
                break;
            case 11:
                oidStr = "b";
                break;
            case 12:
                oidStr = "c";
                break;
            case 13:
                oidStr = "d";
                break;
            case 14:
                oidStr = "e";
                break;
            case 15:
                oidStr = "f";
                break;
            default:
                oidStr = oidInt + "";
                break;
        }
        return oidStr;
    }

    /**
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    public static byte[] combiningByteArray(byte[]... arrays) {
        int length = 0;
        for (byte[] item : arrays) {
            length += item.length;
        }
        byte[] result = new byte[length];
        length = 0;
        for (byte[] item : arrays) {
            System.arraycopy(item, 0, result, length, item.length);
            length += item.length;
        }
        return result;
    }

    /**
     * 获取已关门锁编号
     */
    public static List<String> getDoorClosedNo(byte data) {
        List<String> list = new ArrayList<>();
        String myDtat = Byte2Hex(data);
        char[] chars = hexString2binaryString(myDtat).toCharArray();
        int length = chars.length;
        for (int j = 0; j < length; j++) {
            if (String.valueOf(chars[j]).equals("0")) {
                list.add(String.valueOf(length - j));
            }
        }

        return list;
    }

    //int转byte，int值要少于16，转换成一位16进制byte
    public static String Byte1Hex(int inByte) {
        inByte = Math.min(inByte, 15);
        return String.format("%01x", inByte).toUpperCase();
    }

    public static int getByteLenght(String data) {
        int dataLenght = 0;
        if (!TextUtils.isEmpty(data)) {
            int lenght = data.length();
            dataLenght = lenght / 2;
            if ((lenght & 0x1) == 1) {
                dataLenght++;
            }
        }
        return dataLenght;
    }


    /// <summary>
    /// 设置字节的某位值
    /// </summary>
    /// <param name="data">原始字节数据</param>
    /// <param name="index">要设置的索引位（0-7）</param>
    /// <param name="b">要设置的值（true-1 false-0）</param>
    /// <returns></returns>
    public static byte SetByte(byte data, int index, boolean b) {
        int iret = -1;
        if (index < 0 || index > 7)
            return data;
        byte mask = (byte) (0x01 << index);
        if (b) {
            data = (byte) (data | mask);
        } else {
            mask = (byte) ~mask;
            data = (byte) (data & mask);
        }
        return data;
    }

    public static byte InversionBit(byte data, int index) {
        byte newData = data;
        if (index < 0 || index > 7)
            return data;
        byte mask = (byte) (0x01 << index);
        int tmp = newData & mask;
        if (tmp > 0) {
            mask = (byte) ~mask;
            newData = (byte) (newData & mask);
        } else {
            newData = (byte) (newData | mask);
        }
        return newData;
    }

    /// <summary>
    /// 检查字节的某位为true还是false
    /// </summary>
    /// <param name="data"></param>
    /// <param name="index"></param>
    /// <returns></returns>
    public static boolean CheckByte(byte data, int index) {
        if (index == 7) {
            byte mask = (byte) 0x80;
            //System.out.println(mask);
            data = (byte) (data & mask);
            /*System.out.println( ""
                    + (byte) ((mask >> 7) & 0x1) + (byte) ((mask >> 6) & 0x1)
                    + (byte) ((mask >> 5) & 0x1) + (byte) ((mask >> 4) & 0x1)
                    + (byte) ((mask >> 3) & 0x1) + (byte) ((mask >> 2) & 0x1)
                    + (byte) ((mask >> 1) & 0x1) + (byte) ((mask >> 0) & 0x1));
            System.out.println( ""
                    + (byte) ((data >> 7) & 0x1) + (byte) ((data >> 6) & 0x1)
                    + (byte) ((data >> 5) & 0x1) + (byte) ((data >> 4) & 0x1)
                    + (byte) ((data >> 3) & 0x1) + (byte) ((data >> 2) & 0x1)
                    + (byte) ((data >> 1) & 0x1) + (byte) ((data >> 0) & 0x1));*/
        } else {
            byte mask = (byte) (0x01 << index);
            data = (byte) (data & mask);
            /*System.out.println( ""
                    + (byte) ((mask >> 7) & 0x1) + (byte) ((mask >> 6) & 0x1)
                    + (byte) ((mask >> 5) & 0x1) + (byte) ((mask >> 4) & 0x1)
                    + (byte) ((mask >> 3) & 0x1) + (byte) ((mask >> 2) & 0x1)
                    + (byte) ((mask >> 1) & 0x1) + (byte) ((mask >> 0) & 0x1));
            System.out.println( ""
                    + (byte) ((data >> 7) & 0x1) + (byte) ((data >> 6) & 0x1)
                    + (byte) ((data >> 5) & 0x1) + (byte) ((data >> 4) & 0x1)
                    + (byte) ((data >> 3) & 0x1) + (byte) ((data >> 2) & 0x1)
                    + (byte) ((data >> 1) & 0x1) + (byte) ((data >> 0) & 0x1));*/
        }
        if (data < 0) {
            return true;
        }
        return data > 0 ? true : false;
    }

    public static boolean EvenParityCheck(byte data) {
        int num = 0;
        for (int i = 0; i < 8; i++) {
            if (CheckByte(data, i))
                num++;
        }
        return num % 2 == 0;
    }

    /**
     * //////////////////////////////////////////////////////////////////////////////////////
     * FEC
     * /////////////////////////////////////////////////////////////////////////////////////
     */
    static byte[] FEC_Tbl = new byte[]{0x38, (byte) 0xcb, 0x5d, (byte) 0xae};

    public static byte[] CardNo2Bytes(String cardNo) {
        byte[] bytes = new byte[24];
        byte[] array = new byte[16];
        if (cardNo == null || cardNo.length() == 0) {
            return null;
        }
        try {
            byte[] tmp = cardNo.getBytes("US-ASCII");
            if (tmp.length > 16) {
                System.arraycopy(tmp, 0, array, 0, array.length);
            } else if (tmp.length <= 16) {/*交织数据不是16的倍数，先补足为16整数倍，奇数位填0xFF，偶数位填0x00*/
                System.arraycopy(tmp, 0, array, 0, tmp.length);
                int num = 16 - tmp.length;
                if (num % 2 == 0) {
                    for (int i = 0; i < num; i++) {
                        array[15 - i] = 0x00;
                    }
                } else {
                    for (int i = 0; i < num; i++) {
                        array[15 - i] = (byte) 0xff;
                    }
                }
            }
            array = FECEncode(array);
            if (array.length == 24) {
                array = phyInterweave(array);
                if (array.length == 24) {
                    array = phyWhitingBuff(array);
                    if (array.length == 24) {
                        bytes = array;
                        //LogHelper.WriteLog($"{GetEnumDescription(CardParserType.FEC)} 读者证号转字节数组成功->{array.ToHexString()}");
                        //iret = 0;
                        System.out.println("FEC交织白化编码后数据: " + ByteArrToHex(bytes));
                        return bytes;
                    }
                }
            }
        } catch (Exception ex) {
            //LogHelper.WriteLog($"{GetEnumDescription(CardParserType.FEC)} 读者证号转字节数组异常->{str}", ex);
        }
        return null;
    }

    /// <summary>
    /// 交织编码
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    private static byte[] phyInterweave(byte[] data) {
        System.out.println("交织编码接受到的数据: " + ByteArrToHex(data));
        //int iret = -1;
        if (data == null || data.length != 24)
            return new byte[0];
        List<Boolean> list = new ArrayList<>();
        List<Byte> array = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 8; j++) {
                list.add(CheckByte(data[i], j));
            }
        }
        for (int i = 0; i < 12; i++) {
            byte tmp = 0;
            int k = 0;
            for (int j = 0; j < 16; j++) {
                boolean b = list.get(12 * j + i);
                tmp = SetByte(tmp, k++, b);
                if (k == 8) {
                    array.add(tmp);
                    k = 0;
                }
            }
        }
        data = toPrimitives(array.toArray(new Byte[array.size()]));
        System.out.println("交织编码后数据: " + ByteArrToHex(data));
        //iret = 0;
        return data;
    }

    /// <summary>
    /// 白化编解码（相同的逻辑）
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    private static byte[] phyWhitingBuff(byte[] data) {
        int iret = -1;
        int Whiting_PN9;
        Whiting_PN9 = 0x1FF;
        if (data == null || data.length != 24)
            return new byte[1];
        for (int i = 0; i < data.length; i++) {
            data[i] ^= (byte) Whiting_PN9;
            for (int j = 0; j < 8; j++) {
                byte c = (byte) (Whiting_PN9 & 0x21);
                Whiting_PN9 >>= 1;
                if ((c == 0x21) || (c == 0)) {
                    Whiting_PN9 &= 0xff;
                } else {
                    Whiting_PN9 |= 0x100;
                }
            }
        }
        iret = 0;
        System.out.println("白化编解码后数据: " + ByteArrToHex(data));
        return data;
    }

    private static byte[] FECEncode(byte[] data) {
        int iret = -1;
        if (data == null || data.length != 16)
            return new byte[0];
        List<Boolean> list = new ArrayList<>();
        List<Byte> array = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 8; j++) {
                list.add(CheckByte(data[i], j));
            }
            byte tmp = (byte) (data[i] & FEC_Tbl[0]);
            list.add(EvenParityCheck(tmp));
            tmp = (byte) (data[i] & FEC_Tbl[1]);
            list.add(EvenParityCheck(tmp));
            tmp = (byte) (data[i] & FEC_Tbl[2]);
            list.add(EvenParityCheck(tmp));
            tmp = (byte) (data[i] & FEC_Tbl[3]);
            list.add(EvenParityCheck(tmp));
        }
        for (int i = 0; i < 24; i++) {
            byte tmp = 0;
            for (int j = 0; j < 8; j++) {
                tmp = SetByte(tmp, j, list.get(j + (i * 8)));
            }
            array.add(tmp);
        }
        data = toPrimitives(array.toArray(new Byte[array.size()]));
        System.out.println("FEC编码后数据: " + ByteArrToHex(data));
        iret = 0;
        return data;
    }

    private static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

    public static String Bytes2CardNo(byte[] array) {
        String cardNo;
        String str;
        try {
            array = phyWhitingBuff(array);
            if (array.length == 24) {
                array = phyReversedInterweave(array);
                if (array.length == 24) {
                    array = FECDecode(array);
                    if (array.length != 0) {
                        //cardNo = Encoding.ASCII.GetString(array).Substring(0, CardNoLen);
                        //cardNo=StringUtil.btye2Str(array);
                        StringBuffer tStringBuf = new StringBuffer();
                        char[] tChars = new char[array.length];
                        for (int i = 0; i < array.length; i++) {
                            tChars[i] = (char) array[i];
                        }
                        tStringBuf.append(tChars);
                        cardNo = tStringBuf.toString();
                        //cardNo = cardNo.ToString().Trim(new char[] { '\n', ' ', '\t', '\r', '\0' });
                        cardNo = cardNo.replace("\n", "");
                        cardNo = cardNo.replace(" ", "");
                        cardNo = cardNo.replace("　", "");
                        cardNo = cardNo.replace("\t", "");
                        cardNo = cardNo.replace("\r", "");
                        cardNo = cardNo.replace("\0", "");
                        cardNo = cardNo.trim();
                        return cardNo;
                        //LogHelper.WriteLog($"{GetEnumDescription(CardParserType.FEC)} 读者证号解析成功->{cardNo}");
                        //iret = 0;
                    }
                }
            }
        } catch (Exception ex) {
            //LogHelper.WriteLog($"{GetEnumDescription(CardParserType.FEC)} 读者证号解析异常->{str}", ex);
        }
        return "";
    }

    /// <summary>
    /// 交织译码
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    private static byte[] phyReversedInterweave(byte[] data) {
        int iret = -1;
        if (data == null || data.length != 24)
            return new byte[1];
        List<Boolean> list1 = new ArrayList<>();
        List<Boolean> list2 = new ArrayList<>();
        List<Byte> array = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 8; j++) {
                list1.add(CheckByte(data[i], j));
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 12; j++) {
                list2.add(list1.get(j * 16 + i));
            }
        }
        for (int i = 0; i < 24; i++) {
            byte tmp = 0;
            for (int j = 0; j < 8; j++) {
                tmp = SetByte(tmp, j, list2.get(i * 8 + j));
            }
            array.add(tmp);
        }
        data = toPrimitives(array.toArray(new Byte[array.size()]));
        iret = 0;
        return data;
    }

    private static byte[] FECDecode(byte[] data) {
        int iret = -1;
        if (data == null || data.length != 24)
            return new byte[1];
        List<Boolean> list = new ArrayList<>();
        List<Byte> array = new ArrayList<>();
        Map<Integer, Byte> dic = new TreeMap<>();
        for (int i = 0; i < 8; i++) {
            byte tmp = 0;
            boolean b = CheckByte(FEC_Tbl[0], i);
            tmp = SetByte(tmp, 0, b);
            b = CheckByte(FEC_Tbl[1], i);
            tmp = SetByte(tmp, 1, b);
            b = CheckByte(FEC_Tbl[2], i);
            tmp = SetByte(tmp, 2, b);
            b = CheckByte(FEC_Tbl[3], i);
            tmp = SetByte(tmp, 3, b);
            dic.put(i, tmp);
        }

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 8; j++) {
                list.add(CheckByte(data[i], j));
            }
        }
        for (int i = 0; i < 16; i++) {
            byte tmp = 0;
            for (int j = 0; j < 8; j++) {
                tmp = SetByte(tmp, j, list.get(j + (i * 12)));
            }
            byte fec = 0x00;
            for (int k = 0; k < 4; k++) {
                fec = SetByte(fec, k, list.get(k + 8 + (i * 12)));
            }
            byte index = 0;
            boolean b1 = EvenParityCheck((byte) (tmp & FEC_Tbl[0]));
            boolean b2 = EvenParityCheck((byte) (fec & 0x01));
            boolean a0 = (b1 ^ b2);
            b1 = EvenParityCheck((byte) (tmp & FEC_Tbl[1]));
            b2 = EvenParityCheck((byte) (fec & (0x01 << 1)));
            boolean a1 = (b1 ^ b2);
            b1 = EvenParityCheck((byte) (tmp & FEC_Tbl[2]));
            b2 = EvenParityCheck((byte) (fec & (0x01 << 2)));
            boolean a2 = (b1 ^ b2);
            b1 = EvenParityCheck((byte) (tmp & FEC_Tbl[3]));
            b2 = EvenParityCheck((byte) (fec & (0x01 << 3)));
            boolean a3 = (b1 ^ b2);
            index = SetByte(index, 0, a0);
            index = SetByte(index, 1, a1);
            index = SetByte(index, 2, a2);
            index = SetByte(index, 3, a3);
            int searchKey = 0;
            if (index != 0) {
                //var item = dic.ToList().Find(p => p.Value == index);
                for (int key : dic.keySet()) {
                    if (dic.get(key).equals(index)) {
                        searchKey = key;
                        break;
                    }
                }
                InversionBit(tmp, searchKey);//取反
            }
            array.add(tmp);
        }
        data = toPrimitives(array.toArray(new Byte[array.size()]));
        iret = 0;
        return data;
    }
}
