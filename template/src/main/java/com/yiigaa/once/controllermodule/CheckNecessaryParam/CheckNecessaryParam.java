package com.yiigaa.once.controllermodule.CheckNecessaryParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.controllermodule.CheckNecessaryParam.CheckNecessaryParam;
######LinkpackageImport end######

######LinkObjectImport start######
        put("CheckNecessaryParam", new CheckNecessaryParam());
######LinkObjectImport end######

######GradleImport start######
//CheckNecessaryParam
######GradleImport end######

######ErrorCodes start######
        //CheckNecessaryParam
        put("MODULE_CheckNecessaryParam_block", new String[]{"E-CM01(CheckNecessaryParam)", "缺少必要参数"});
        put("MODULE_CheckNecessaryParam_exception", new String[]{"E-CM02(CheckNecessaryParam)", "检查必要参数崩溃"});
######ErrorCodes end######
*/

public class CheckNecessaryParam extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        try {
            JSONObject passParam = (JSONObject) param.get("passParam");
            HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
            JSONObject sessionSave = (JSONObject) param.get("sessionSave");
            JSONObject returnParam = (JSONObject) param.get("returnParam");
            HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

            boolean module_isClean = (moduleParam.get("module_isClean") == null)?true:Boolean.parseBoolean(moduleParam.get("module_isClean").toString());
            String backUpKey = (moduleParam.get("module_backUpPrefix") == null)?null:moduleParam.get("module_backUpPrefix").toString();
            JSONObject newParam = new JSONObject();

            for(Map.Entry<String, String> entry : moduleParam.entrySet()){
                Object entryValue = passParam.get(entry.getKey());
                if(entry.getKey().compareTo("module_isClean") == 0 || entry.getKey().compareTo("module_backUpPrefix") == 0){
                    continue;
                }
                switch(entry.getValue()){
                    case "nec":
                        if(entryValue == null){
                            returnParam.put("errorCode", "MODULE_CheckNecessaryParam_block");
                        }
                        break;
                    case "nec@@list":
                        if (entryValue == null){
                            returnParam.put("errorCode", "MODULE_CheckNecessaryParam_block");
                        }
                        if (!(entryValue instanceof JSONArray)){
                            returnParam.put("errorCode", "MODULE_CheckNecessaryParam_block");
                        }
                        if(((JSONArray) JSONArray.parse(entryValue.toString())).size()==0){
                            returnParam.put("errorCode", "MODULE_CheckNecessaryParam_block");
                        }
                        break;
                    case "opt":
                    case "opt@@list":
                }
                if(module_isClean){
                    if(entryValue != null) {
                        newParam.put(entry.getKey(), entryValue);
                    }
                }
            }

            if(module_isClean){
                passParam = newParam;
            }
            if(backUpKey != null){
                for(Map.Entry<String, Object> entry : passParam.entrySet()) {
                    passParam.put(backUpKey+entry.getKey(), entry.getValue());
                }
            }

            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            JSONObject returnParam = new JSONObject();
            returnParam.put("errorCode", "MODULE_CheckNecessaryParam_exception");
            returnMap.put("returnParam", returnParam);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ControllerModule-CheckNecessaryParam start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-CheckNecessaryParam end");
        return returnParam;
    }
}

