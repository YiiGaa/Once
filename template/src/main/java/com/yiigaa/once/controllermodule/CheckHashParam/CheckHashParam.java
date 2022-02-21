package com.yiigaa.once.controllermodule.CheckHashParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.controllermodule.CheckHashParam.CheckHashParam;
######LinkpackageImport end######

######LinkObjectImport start######
        put("CheckHashParam", new CheckHashParam());
######LinkObjectImport end######

######GradleImport start######
//CheckHashParam
######GradleImport end######

######ErrorCodes start######
        //CheckHashParam
        put("MODULE_CheckHashParam_block", new String[]{"E-CM01(CheckHashParam)", "缺少必要参数"});
        put("MODULE_CheckHashParam_exception", new String[]{"E-CM02(CheckHashParam)", "检查必要参数崩溃"});
######ErrorCodes end######
*/

public class CheckHashParam extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        try {
            JSONObject passParam = (JSONObject) param.get("passParam");
            HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
            JSONObject sessionSave = (JSONObject) param.get("sessionSave");
            JSONObject returnParam = (JSONObject) param.get("returnParam");
            HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");
            boolean module_isClean = (moduleParam.get("module_isClean") == null)?true:Boolean.parseBoolean(moduleParam.get("module_isClean").toString());
            String module_targetKey = (moduleParam.get("module_targetKey") == null)?null:moduleParam.get("module_targetKey").toString();
            JSONArray jsonArray=null;
            JSONArray newJsonArray=new JSONArray();

            if (module_targetKey==null){
                returnParam.put("errorCode", "MODULE_CheckHashParam_block");
                return returnMap;
            }else{
                jsonArray = passParam.getJSONArray(module_targetKey);
            }
            if (jsonArray.isEmpty()){
                returnParam.put("errorCode", "MODULE_CheckHashParam_block");
                return returnMap;
            }

            for (Object jsonObject : jsonArray) {
                JSONObject value = (JSONObject) jsonObject;
                JSONObject tempJsonObject = new JSONObject();
                Set<String> keys = value.keySet();
                for (Map.Entry<String, String> entry : moduleParam.entrySet()) {
                    if (entry.getKey().compareTo("module_isClean") == 0) {
                        continue;
                    }
                    if (entry.getKey().compareTo("module_targetKey") == 0) {
                        continue;
                    }
                    Object valueChild = value.get(entry.getKey());

                    switch (entry.getValue()) {
                        case "nec":
                            if (!keys.contains(entry.getKey()) || value.getString(entry.getKey()) == null) {
                                returnParam.put("errorCode", "MODULE_CheckHashParam_block");
                                return returnMap;
                            }
                            break;
                        case "opt":
                    }

                    if (module_isClean) {
                        if (valueChild != null) {
                            tempJsonObject.put(entry.getKey(), valueChild);
                        }
                    }

                }
                newJsonArray.add(tempJsonObject);
            }

            if(module_isClean){
                passParam.put(module_targetKey,newJsonArray);
            }



            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            JSONObject returnParam = new JSONObject();
            returnParam.put("errorCode", "MODULE_CheckHashParam_exception");
            returnMap.put("returnParam", returnParam);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ControllerModule-CheckHashParam start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-CheckHashParam end");
        return returnParam;
    }
}

