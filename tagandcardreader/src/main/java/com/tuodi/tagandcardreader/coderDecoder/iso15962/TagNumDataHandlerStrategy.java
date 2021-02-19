package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import android.text.TextUtils;

import com.example.commonlib.utils.CommonUtil;


public class TagNumDataHandlerStrategy implements TagDataHandlerStrategy{
    @Override
    public String handleData(byte[] data,int type) {
        return  CommonUtil.ByteArrToHex(data);
    }

    @Override
    public  String processData(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        return data;
    }
}
