package com.stoprefactoring.once.module;

import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.ERRORCODE;

import java.util.HashMap;

public class Link {
    public ERRORCODE Start(JSONObject moduleParam, HashMap<String, Object> param){
        return ERRORCODE.ERR_OK;
    }

    public ERRORCODE End(HashMap<String, Object> param, ERRORCODE result){
        return ERRORCODE.ERR_OK;
    }
}
