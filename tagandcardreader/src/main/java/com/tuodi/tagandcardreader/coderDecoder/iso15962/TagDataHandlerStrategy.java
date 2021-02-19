package com.tuodi.tagandcardreader.coderDecoder.iso15962;

public interface TagDataHandlerStrategy {
    String handleData(byte[] data, int type);

    String processData(String data);
}
