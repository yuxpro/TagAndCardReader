package com.tuodi.tagandcardreader.coderDecoder.iso15962;

import java.util.HashMap;
import java.util.Map;

public class TagStrategyFactory {
    //数据解析类型
    public static final String DARA_TYPE_CUSTOM = "0";
    public static final String DARA_TYPE_INT = "1";
    public static final String DARA_TYPE_NUM = "2";
    public static final String DARA_TYPE_5_BIT = "3";
    public static final String DARA_TYPE_6_BIT = "4";
    public static final String DARA_TYPE_7_BIT = "5";
    public static final String DARA_TYPE_ASCALL = "6";
    public static final String DARA_TYPE_GB_13000 = "7";
    private static TagStrategyFactory factory = new TagStrategyFactory();

    private TagStrategyFactory() {
    }

    private static Map strategyMap = new HashMap<>();

    static {
        strategyMap.put(DARA_TYPE_INT, new TagIntDataHandlerStrategy());
        strategyMap.put(DARA_TYPE_NUM, new TagNumDataHandlerStrategy());
        strategyMap.put(DARA_TYPE_ASCALL, new TagAscallDataHandlerStrategy());
        strategyMap.put(DARA_TYPE_5_BIT, new TagBit5DataHandlerStrategy());
        strategyMap.put(DARA_TYPE_6_BIT, new TagBit6DataHandlerStrategy());
        strategyMap.put(DARA_TYPE_7_BIT, new TagBit7DataHandlerStrategy());
        strategyMap.put(DARA_TYPE_CUSTOM, new TagCustomDataHandlerStrategy());
    }

    public TagDataHandlerStrategy creator(String type) {
        return (TagDataHandlerStrategy)strategyMap.get(type);
    }

    public static TagStrategyFactory getInstance() {
        return factory;
    }


}
