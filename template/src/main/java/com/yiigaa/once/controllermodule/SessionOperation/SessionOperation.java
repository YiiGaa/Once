package com.yiigaa.once.controllermodule.SessionOperation;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.SessionOperation.SessionOperationConfig;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;
import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/*
**version: v1.0
**Please do not modify the following
**If you modify the following contents, please re LinkModule: ruby Christmas.rb ./Menu/MakeEngineeringNormal/AutoLinkModule

######LinkpackageImport start######
import com.yiigaa.once.controllermodule.SessionOperation.SessionOperation;
######LinkpackageImport end######

######LinkObjectImport start######
        put("SessionOperation", new SessionOperation());
######LinkObjectImport end######

######GradleImport start######
//SessionOperation
        //implementation 'org.springframework.session:spring-session-data-redis'
        //implementation 'org.springframework.boot:spring-boot-starter-data-redis'
        //implementation 'org.apache.commons:commons-pool2'
######GradleImport end######

######ErrorCodes start######
        //SessionOperation
        put("MODULE_SessionOperation_exception", new String[]{"E-CM01(SessionOperation)", " SessionOperation 崩溃"});
######ErrorCodes end######
*/


public class SessionOperation extends Link {
    private HashMap<String, Object> save(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            for (Map.Entry<String, String> entry : moduleParam.entrySet()){
                if(entry.getKey().compareTo("control") == 0){
                    continue;
                }
                String tempValue = entry.getValue();
                if(passParam.get(tempValue) != null){
                    tempValue = passParam.get(tempValue).toString();
                }
                request.getSession().setAttribute(entry.getKey(), tempValue);
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_SessionOperation_exception");
            returnMap.put("returnParam", returnParam);
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    private HashMap<String, Object> delete(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String deleteKey = (moduleParam.get("module_deleteKey") == null)?"all":moduleParam.get("module_deleteKey").toString();
            String[] deleteKeyArr = deleteKey.split(",");
            for(String tempKye : deleteKeyArr){
                switch(tempKye){
                    case "all":
                        // Enumeration e=request.getSession().getAttributeNames();
                        // while(e.hasMoreElements()){
                        //     request.getSession().removeAttribute(e.nextElement().toString());
                        // }
                        request.getSession().invalidate();
                        break;
                    default:
                        request.getSession().removeAttribute(tempKye);
                        break;
                }
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_SessionOperation_exception");
            returnMap.put("returnParam", returnParam);
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ControllerModule-SessionOperation start");
        HashMap<String, Object> returnParam = param;
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        switch(moduleParam.get("control")){
            case "save":
                returnParam = save(param);
                break;
            case "delete":
                returnParam = delete(param);
                break;
            default:
                break;
        }
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-SessionOperation end");
        return returnParam;
    }
}


