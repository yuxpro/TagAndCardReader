package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import android.text.TextUtils;

import com.example.commonlib.utils.CommonUtil;

public class TagIntDataHandlerStrategy implements TagDataHandlerStrategy {
    @Override
    public String handleData(byte[] data, int type) {
        return String.valueOf(Long.parseLong(CommonUtil.encodeHexStr(data), 16));
    }

    @Override
    public String processData(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        byte[] datas = CommonUtil.IntToByteArray(Integer.parseInt(data));
        return CommonUtil.ByteArrToHexToNoNULL(datas);
    }
}
