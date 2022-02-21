package com.yiigaa.once.controllermodule.SessionSave;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.controllermodule.SessionSave.SessionSave;
######LinkpackageImport end######

######LinkObjectImport start######
        put("SessionSave", new SessionSave());
######LinkObjectImport end######

######GradleImport start######
//SessionSave
######GradleImport end######

######ErrorCodes start######
        //SessionSave
        put("MODULE_SessionSave_exception", new String[]{"E-CM01(SessionSave)", "session 存储失败"});
######ErrorCodes end######
*/

public class SessionSave extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {

            for (Map.Entry<String, Object> entry : sessionSave.entrySet()) {
                    request.getSession().setAttribute(entry.getKey(), entry.getValue());
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_SessionSave_exception");
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
        COMMON_LOGGER.DEBUG(param, "ControllerModule-SessionSave start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-SessionSave end");
        return returnParam;
    }
}


