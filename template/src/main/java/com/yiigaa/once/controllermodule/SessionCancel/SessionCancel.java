package com.yiigaa.once.controllermodule.SessionCancel;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;
import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.controllermodule.SessionCancel.SessionCancel;
######LinkpackageImport end######

######LinkObjectImport start######
        put("SessionCancel", new SessionCancel());
######LinkObjectImport end######

######GradleImport start######
//SessionCancel
######GradleImport end######

######ErrorCodes start######
        //SessionCancel
        put("MODULE_SessionCancel_exception", new String[]{"E-CM01(SessionCancel)", "Session去掉崩溃"});
######ErrorCodes end######
*/

public class SessionCancel extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        String[] moduleParam = (String[]) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            if(moduleParam == null ||moduleParam[0].compareTo("all")==0){
                Enumeration e=request.getSession().getAttributeNames();
                while(e.hasMoreElements()){
                    request.getSession().removeAttribute(e.nextElement().toString());
                }
            }else {
                try {
                    for (String element : moduleParam) {
                        request.getSession().removeAttribute(element);
                    }
                } catch(Exception e){}
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_SessionCancel_exception");
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
        COMMON_LOGGER.DEBUG(param, "ControllerModule-SessionCancel start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-SessionCancel end");
        return returnParam;
    }
}


