package com.tuodi.tagandcardreader.coderDecoder.iso15962;

/**
 * Created by xiaosheng on 2018/12/15.
 */

public class TagTypeModel {
    public static final String BOOK_YTPE = "book_type";
    public static final String CARD_YTPE = "card_type";
    //标签类型
    public static final String TAG_TYPE_BOOK = "图书";
    public static final String TAG_TYPE_READER_CARD = "读者证";
    public static final String TAG_TYPE_SHELF_TAG = "层架标";
    public static final String TAG_TYPE_UNKNOWN = "未知类型";
    private String tagType;
    private String bookBarcode;
    private String libCode;

    public TagTypeModel(String tagType, String bookBarcode) {
        this.tagType = tagType;
        this.bookBarcode = bookBarcode;
    }

    public TagTypeModel() {
    }

    public String getLibCode() {
        return libCode;
    }

    public void setLibCode(String libCode) {
        this.libCode = libCode;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getBookBarcode() {
        return bookBarcode;
    }

    public void setBookBarcode(String bookBarcode) {
        String data = bookBarcode;
        if (bookBarcode.length() > 30) {
            data = bookBarcode.substring(0, 30);
        }
        this.bookBarcode = data;
    }

    @Override
    public String toString() {
        return "TagTypeModel{" +
                "tagType='" + tagType + '\'' +
                ", bookBarcode='" + bookBarcode + '\'' +
                ", libCode='" + libCode + '\'' +
                '}';
    }
}
