package com.tuodi.tagandcardreader.coderDecoder.iso15962;


import com.example.commonlib.utils.CommonUtil;

/**
 * Role:
 * Author: Pbin
 * Date: 2020/3/27 14:53
 */
public class TagCustomDataHandlerStrategy implements TagDataHandlerStrategy {
    @Override
    public String handleData(byte[] data, int type) {
        return  CommonUtil.ByteArrToHex(data);
    }

    @Override
    public String processData(String data) {
        return data;
    }
}