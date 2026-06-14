package main.module;

import java.util.HashMap;

import tools.jackson.databind.node.ObjectNode;

import main.common.ERRORCODE;

public class Link {
    public ERRORCODE Start(ObjectNode moduleParam, HashMap<String, Object> param){
        return ERRORCODE.ERR_OK;
    }

    public ERRORCODE End(HashMap<String, Object> param, ERRORCODE result){
        return ERRORCODE.ERR_OK;
    }
}
