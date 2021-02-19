package com.tuodi.tagandcardreader.andiReader;


import com.tuodi.tagandcardreader.coderDecoder.iso15962.TagStrategyController;
import com.tuodi.tagandcardreader.coderDecoder.iso15962.TagStrategyFactory;
import com.tuodi.tagandcardreader.coderDecoder.iso15962.TagTypeModel;

import static com.tuodi.tagandcardreader.coderDecoder.iso15962.TagTypeModel.TAG_TYPE_BOOK;
import static com.tuodi.tagandcardreader.coderDecoder.iso15962.TagTypeModel.TAG_TYPE_READER_CARD;
import static com.tuodi.tagandcardreader.coderDecoder.iso15962.TagTypeModel.TAG_TYPE_SHELF_TAG;
import static com.tuodi.tagandcardreader.coderDecoder.iso15962.TagTypeModel.TAG_TYPE_UNKNOWN;

/**
 * Created by xiaosheng on 2018/8/20.
 */

public class BookTagHandlerUtil {

    public static final String ASCALL_YTPE = "6";
    public static final String ASCALL_SKEW_YTPE = "E";
    public static final String DITIG_YTPE = "2";
    public static final String BOOK_YTPE = "1";
    public static final String CARD_YTPE = "5";

    //标签类型
    public static final String TYPE_BOOK_VALUE = "12";
    public static final String TYPE_READER_VALUE = "80";
    public static final String TYPE_CJB_VALUE = "4F";

    //oid为14以上标志
    public static final String OID_OVER_14 = "f";

    //oid类型
    public static final String OID_TYPE_DARA = "1";
    public static final String OID_TYPE_PARAM = "2";
    public static final String OID_TYPE_LIB_CODE = "3";
    public static final String OID_TYPE_USE = "5";

    /**
     * /**
     * uuid处理
     * （1）、16进制
     * （2）、整体转10进制
     * （3）、韦根26协议
     * （4）、逆反规则 高低位
     *
     * @param dataType 逆反规则 0为正，1为反
     */
    public static String handlerUUid(byte[] datas, int type, int dataType) {
        if (datas.length < 4) {
            return CommonUtil.encodeHexStr(datas);
        }
        byte[] mDatas = datas;
        if (dataType == 1) {
            mDatas = CommonUtil.byteReverse(datas);
        }
        String uuid = "";
        switch (type) {
            case 1:
                uuid = CommonUtil.encodeHexStr(mDatas);
                break;
            case 2:
                //                uuid = String.valueOf(StringUtil.bytesToInt(mDatas, 0));
                uuid = String.valueOf(Long.parseLong(CommonUtil.encodeHexStr(mDatas), 16));
                break;
            case 3:
                int num1 = (int) Long.parseLong(CommonUtil.Byte2Hex(mDatas[1]), 16);
                byte[] datas2 = new byte[4];
                System.arraycopy(mDatas, 2, datas2, 2, 2);
                //                int num2 = StringUtil.bytesToInt(datas2, 0);
                int num2 = (int) Long.parseLong(CommonUtil.encodeHexStr(datas2), 16);
                datas2 = null;
                uuid = String.format("%03d", num1) + String.format("%05d", num2);
                break;
            default:
                break;
        }
        return uuid;
    }

    /**
     *
     */
    public static TagTypeModel byteArray2TagTypeModel(byte[] datas) {
        if (null == datas || datas.length == 0) {
            return new TagTypeModel();
        }
        TagTypeModel tagTypeModel = new TagTypeModel();
        int oldIndex;
        int index = 0;
        int checkIndex = 0;
        while (datas[index] != 0) {
            oldIndex = index;
            byte actiontype1 = datas[index];
            String[] actionString = CommonUtil.byteToStrings(actiontype1);
            int offset = CommonUtil.hexStr2int(actionString[0]);
            if (offset > 8) {//有偏移数据
                if (actionString[1].equals("1")) {//标签barcode
                    int offsetLenght = CommonUtil.byte2int(datas[index + 1]);
                    int dataLenght = CommonUtil.byte2int(datas[index + 2]);
                    byte[] mData = new byte[dataLenght];
                    checkIndex = index + 3 + offsetLenght + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 3, mData, 0, dataLenght);
                    }
                    index = checkIndex;
                    String barcode = getBarcodeByTagBytes(mData, offset);
                    tagTypeModel.setBookBarcode(barcode);
                } else if (actionString[1].equals("3")) {//馆代码
                    int offsetLenght = CommonUtil.byte2int(datas[index + 1]);
                    int dataLenght = CommonUtil.byte2int(datas[index + 2]);
                    byte[] mData = new byte[dataLenght];
                    checkIndex = index + 3 + offsetLenght + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 3, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                    if (actionString[0].equals("9")) {//十进制编码
                        tagTypeModel.setLibCode(CommonUtil.ByteToInt32(mData) + "%");
                    } else if (actionString[0].equals("A")) {//数字编码
                        tagTypeModel.setLibCode(CommonUtil.ByteToInt32(mData) + "$");
                    } else if (actionString[0].equals("F")) {//无编码
                        tagTypeModel.setLibCode(new String(mData));
                    } else {

                    }
                    //tagTypeModel.setLibCode(new String(mData));
                } else if (actionString[1].equals("5")) {//标签类别
                    int offsetLenght = CommonUtil.byte2int(datas[index + 1]);
                    int dataLenght = CommonUtil.byte2int(datas[index + 2]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 3 + offsetLenght + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 3, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                    String type = CommonUtil.ByteArrToHex(mData).replace(" ", "");
                    String tagType = getTagTypeByTagBytes(type);
                    tagTypeModel.setTagType(tagType);
                } else if (actionString[1].equalsIgnoreCase("7")) {//标签类型
                    int dataLenght = CommonUtil.byte2int(datas[index + 1]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 2 + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 2, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                    String type = CommonUtil.ByteArrToHex(mData).replace(" ", "");
                    String tagType = getTagTypeByTagBytes(type);
                    tagTypeModel.setTagType(tagType);
                } else if (actionString[1].equalsIgnoreCase("f")) {//oid15及以上，不作处理
                    int oidOverflow = CommonUtil.byte2int(datas[index + 1]);
                    int offsetLenght = CommonUtil.byte2int(datas[index + 2]);
                    int dataLenght = CommonUtil.byte2int(datas[index + 3]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 4 + offsetLenght + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, 4, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                    //判断oid,然后保存值
                } else {//其他oid，不作处理
                    int offsetLenght = CommonUtil.byte2int(datas[index + 1]);
                    int dataLenght = CommonUtil.byte2int(datas[index + 2]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 3 + offsetLenght + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 3, mData, 0, dataLenght);
                    }
                    index = checkIndex;


                }
            } else {//没有偏移数据
                if (actionString[1].equalsIgnoreCase("f")) {//oid15及以上，不作处理
                    int oidOverflow = CommonUtil.byte2int(datas[index + 1]);
                    int dataLenght = CommonUtil.byte2int(datas[index + 2]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 3 + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 3, mData, 0, dataLenght);
                    }
                    index = checkIndex;
                    //判断oid,然后保存值
                } else if (actionString[1].equals("1")) {//标签barcode
                    int dataLenght = CommonUtil.byte2int(datas[index + 1]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 2 + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 2, mData, 0, dataLenght);
                    }
                    index = checkIndex;
                    String barcode = getBarcodeByTagBytes(mData, offset);
                    tagTypeModel.setBookBarcode(barcode);
                } else if (actionString[1].equals("3")) {//馆代码
                    int dataLenght = CommonUtil.byte2int(datas[index + 1]);
                    byte[] mData = new byte[dataLenght];
                    byte[] b = new byte[]{(byte) '1', (byte) '1', (byte) 'A', (byte) '0', (byte) '1', (byte) '0', (byte) '1'};
                    checkIndex = index + 2 + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 2, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                    if (actionString[0].equals("1")) {//整形编码
                        tagTypeModel.setLibCode(CommonUtil.ByteToInt32(mData) + "%");
                    } else if (actionString[0].equals("2")) {//数字编码
                        tagTypeModel.setLibCode(CommonUtil.ByteToInt32(mData) + "$");
                    } else if (actionString[0].equals("6")) {//无编码
                        tagTypeModel.setLibCode(new String(mData));
                    } else {

                    }
                } else if (actionString[1].equals("5")) {//标签类型
                    int dataLenght = CommonUtil.byte2int(datas[index + 1]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 2 + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 2, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                    String type = CommonUtil.ByteArrToHex(mData).replace(" ", "");
                    String tagType = getTagTypeByTagBytes(type);
                    tagTypeModel.setTagType(tagType);
                } else {//其他oid，不作处理
                    int dataLenght = CommonUtil.byte2int(datas[index + 1]);
                    byte[] mData = new byte[dataLenght];

                    checkIndex = index + 2 + dataLenght;
                    if (datas.length >= dataLenght && checkIndex <= datas.length - 1) {
                        System.arraycopy(datas, index + 2, mData, 0, dataLenght);
                    }
                    index = checkIndex;

                }
            }
            if (oldIndex == index || index >= datas.length - 1) {
                break;
            }
        }
        return tagTypeModel;
    }

    /**
     * @param type byte，标签类型
     * @return string，标签类型
     */
    private static String getTagTypeByTagBytes(String type) {
        String tagType;
        switch (type) {
            case TYPE_BOOK_VALUE:
                tagType = TAG_TYPE_BOOK;
                break;
            case TYPE_READER_VALUE:
                tagType = TAG_TYPE_READER_CARD;
                break;
            case TYPE_CJB_VALUE:
                tagType = TAG_TYPE_SHELF_TAG;
                break;
            default:
                tagType = TAG_TYPE_UNKNOWN;
                break;
        }
        return tagType;
    }

    /**
     * @param mData  byte[]，标签数据
     * @param offset int，数据偏移量
     * @return string，标签barcode
     */
    private static String getBarcodeByTagBytes(byte[] mData, int offset) {
        String barcode = "";
        if (offset < 8) {
            offset += 8;
        }
        if (Math.abs(offset - 8) == 1) {//int编码
            barcode = barcodeOid(mData, Integer.parseInt(TagStrategyFactory.DARA_TYPE_INT));
        }
        if (Math.abs(offset - 8) == 2) {//数字编码
            barcode = barcodeOid(mData, Integer.parseInt(TagStrategyFactory.DARA_TYPE_NUM));
            if (barcode.endsWith("F")) {//yuan xin：需要把编码时最后补齐的F去掉
                barcode = barcode.substring(0, barcode.length() - 1);
            }
        }
        if (Math.abs(offset - 8) == 3) {//5bit编码
            barcode = barcodeOid(mData, Integer.parseInt(TagStrategyFactory.DARA_TYPE_5_BIT));
        }
        if (Math.abs(offset - 8) == 4) {//6bit编码
            barcode = barcodeOid(mData, Integer.parseInt(TagStrategyFactory.DARA_TYPE_6_BIT));
        }
        if (Math.abs(offset - 8) == 5) {//7bit编码
            barcode = barcodeOid(mData, Integer.parseInt(TagStrategyFactory.DARA_TYPE_7_BIT));
        }
        if (Math.abs(offset - 8) == 6) {//ASCII编码，即8bit编码，实际没有编码
            barcode = barcodeOid(mData, Integer.parseInt(TagStrategyFactory.DARA_TYPE_ASCALL));
        }
        return barcode;
    }

    /**
     * barcodeOid数据处理，运用策略和工厂模式
     *
     * @return barcode字符串
     */
    public static String barcodeOid(byte[] data, int type) {
        TagStrategyController tagStrategyController = new TagStrategyController();

        return tagStrategyController.handleMsg(data, type);
    }

    /**
     * 馆代码处理
     */
    public static String lidCodeOid(byte[] data, int type) {
        String libCode = "";
        switch (type) {
            case 1:
                libCode = CommonUtil.ByteToInt32(data) + "%";
                break;
            case 2:
                libCode = CommonUtil.ByteToInt32(data) + "$";
                break;
            case 6:
                libCode = new String(data);
                break;
            default:
                break;
        }
        return libCode;
    }

    /**
     * 标签用途类型处理
     */
    public static String useOid(byte[] data, int type) {
        String types = CommonUtil.ByteArrToHex(data).replace(" ", "");
        String typeShow;
        switch (types) {
            case TYPE_BOOK_VALUE:
                typeShow = "图书";
                break;
            case TYPE_READER_VALUE:
                typeShow = "读者证";
                break;
            case TYPE_CJB_VALUE:
                typeShow = "层架标";
                break;
            default:
                typeShow = "未知类型";
                break;
        }
        return typeShow;
    }
}
