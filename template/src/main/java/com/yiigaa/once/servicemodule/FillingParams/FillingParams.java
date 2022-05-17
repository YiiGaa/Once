package com.yiigaa.once.servicemodule.FillingParams;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.servicecommon.COMMON_LOGGER;
import com.yiigaa.once.servicemodule.ErrorCodes;
import com.yiigaa.once.servicemodule.Link;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
**version: v1.0
**Please do not modify the following
**If you modify the following contents, please re LinkModule: ruby Christmas.rb ./Menu/MakeEngineeringNormal/AutoLinkModule

######LinkpackageImport start######
import com.yiigaa.once.servicemodule.FillingParams.FillingParams;
######LinkpackageImport end######

######LinkObjectImport start######
        put("FillingParams", new FillingParams());
######LinkObjectImport end######

######GradleImport start######
//FillingParams
######GradleImport end######

######ErrorCodes start######
        //FillingParams
        put("MODULE_FillingParams_sessionGet_block", new String[]{"E-SM01(FillingParams)", "session获取失败"});
        put("MODULE_FillingParams_exception", new String[]{"E-SM02(FillingParams)", "填充参数崩溃"});
######ErrorCodes end######
*/


public class FillingParams extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String backUpKey = (moduleParam.get("module_backUpPrefix") == null)?null:moduleParam.get("module_backUpPrefix").toString();
            for (Map.Entry<String, String> entry : moduleParam.entrySet()){
                if(entry.getKey().compareTo("module_backUpPrefix") == 0){
                    continue;
                }

                //judge is absolutly
                String[] head = entry.getValue().split("@@");
                String header = "";
                if(0 == head[0].compareTo("abs")){
                    head[0] = head[1];
                } else if(0 == head[0].compareTo("return")){
                    header = head[0];
                    head[0] = head[1];
                } else {
                    if(null == passParam.get(entry.getKey()) || 0 == passParam.get(entry.getKey()).toString().compareTo("") || 0 == passParam.get(entry.getKey()).toString().compareTo(" ")){
                        //do nothing
                    } else {
                        continue;
                    }
                }

                //judge link
                String[] part = head[0].split("\\+");
                String targetValue = "";
                for(int i=0; i<part.length; i++){
                    //judge is function
                    String[] function = part[i].split("##");
                    String tempValue = "";
                    switch(function[0]){
                        case "uuid":
                            String uuid = UUID.randomUUID().toString().replace("-", "");
                            tempValue = uuid;
                            break;
                        case "session":
                            if(request.getSession().getAttribute(function[1]) == null) {
                                returnParam.put("errorCode", "MODULE_FillingParams_sessionGet_block");
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
                            if(null == passParam.get(function[1])){
                                tempValue = "null";
                            } else {
                                tempValue = passParam.get(function[1]).toString();
                            }
                            break;
                        case "self":
                            if(null == passParam.get(entry.getKey()) || 0 == passParam.get(entry.getKey()).toString().compareTo("") || 0 == passParam.get(entry.getKey()).toString().compareTo(" ")){
                                //do nothing
                            } else {
                                tempValue = passParam.get(entry.getKey()).toString();
                            }
                            break;
                        default:
                            tempValue = function[0];
                    }

                    targetValue = targetValue + tempValue;

                }

                if(header.compareTo("return") == 0){
                    returnParam.put(entry.getKey(), targetValue);
                } else if(header.compareTo("session") == 0){
                    returnParam.put(entry.getKey(), targetValue);
                } else {
                    passParam.put(entry.getKey(), targetValue);
                }
            }

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

            returnParam.put("errorCode", "MODULE_FillingParams_exception");
            returnMap.put("returnParam", returnParam);
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ServiceModule-FillingParams start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ServiceModule-FillingParams end");
        return returnParam;
    }
}


