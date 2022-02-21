package com.yiigaa.once.controllermodule.FillingHashParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.controllermodule.FillingHashParam.FillingHashParam;
######LinkpackageImport end######

######LinkObjectImport start######
        put("FillingHashParam", new FillingHashParam());
######LinkObjectImport end######

######GradleImport start######
//FillingHashParam
######GradleImport end######

######ErrorCodes start######
        //FillingHashParam
        put("MODULE_FillingHashParam_SessionGet_block", new String[]{"E-CM01(FillingHashParam)", "session获取失败"});
        put("MODULE_FillingHashParam_exception", new String[]{"E-CM02(FillingHashParam)", "填充参数崩溃"});
######ErrorCodes end######
*/

public class FillingHashParam extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String backUpKey = (moduleParam.get("module_backUpPrefix") == null)?null:moduleParam.get("module_backUpPrefix").toString();
            String module_targetKey = (moduleParam.get("module_targetKey") == null)?null:moduleParam.get("module_targetKey").toString();
            JSONArray jsonArray=null;
            if (module_targetKey==null){
                returnParam.put("errorCode", "MODULE_FillingHashParam_exception");
                return returnMap;
            }else{
                jsonArray = passParam.getJSONArray(module_targetKey);
            }
            for (Map.Entry<String, String> entry : moduleParam.entrySet()){
                if(entry.getKey().compareTo("module_backUpPrefix") == 0){
                    continue;
                }
                if(entry.getKey().compareTo("module_targetKey") == 0){
                    continue;
                }
                int index=0;
                for (Object object : jsonArray) {
                    ++index;
                    JSONObject valueObject = (JSONObject) object;

                    //judge is absolutly
                    String[] head = entry.getValue().split("@@");
                    String header = "";
                    if (0 == head[0].compareTo("abs")) {
                        head[0] = head[1];
                    } else {
                        if (null == valueObject.get(entry.getKey()) || 0 == valueObject.get(entry.getKey()).toString().compareTo("") || 0 == valueObject.get(entry.getKey()).toString().compareTo(" ")) {
                            //do nothing
                        } else {
                            continue;
                        }
                    }

                    //judge link
                    String[] part = head[0].split("\\+");
                    String targetValue = "";
                    for (int i = 0; i < part.length; i++) {
                        //judge is function
                        String[] function = part[i].split("##");
                        String tempValue = "";
                        switch (function[0]) {
                            case "index":
                                tempValue=tempValue+String.valueOf(index);
                                break;
                            case "uuid":
                                String uuid = UUID.randomUUID().toString().replace("-", "");
                                tempValue = uuid;
                                break;
                            case "session":
                                if (request.getSession().getAttribute(function[1]) == null) {
                                    returnParam.put("errorCode", "MODULE_FillingHashParam_SessionGet_block");
                                    return returnMap;
                                }
                                tempValue = request.getSession().getAttribute(function[1]).toString();
                                break;
                            case "time":
                                long currentTime = System.currentTimeMillis();
                                SimpleDateFormat formatter = new SimpleDateFormat(function[1]);
                                Date date = new Date(currentTime);
                                tempValue = formatter.format(date);
                                break;
                            case "get":
                                if (null == passParam.get(function[1])) {
                                    tempValue = "null";
                                } else {
                                    tempValue = passParam.get(function[1]).toString();
                                }
                                break;
                            case "self":
                                if (null == valueObject.get(entry.getKey()) || 0 == valueObject.get(entry.getKey()).toString().compareTo("") || 0 == valueObject.get(entry.getKey()).toString().compareTo(" ")) {
                                    //do nothing
                                } else {
                                    tempValue = valueObject.get(entry.getKey()).toString();
                                }
                                break;
                            default:
                                tempValue = function[0];
                        }

                        targetValue = targetValue + tempValue;

                    }

                    valueObject.put(entry.getKey(), targetValue);
                }
            }
            passParam.put(module_targetKey,jsonArray);

            if(backUpKey != null){
                JSONObject backupJson = new JSONObject();
                for(Map.Entry<String, Object> entry : passParam.entrySet()) {
                    backupJson.put(backUpKey+entry.getKey(), entry.getValue());
                }
                passParam.putAll(backupJson);
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_FillingHashParam_exception");
            returnMap.put("returnParam", returnParam);
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ControllerModule-FillHahParam start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-FillHahParam end");
        return returnParam;
    }
}


