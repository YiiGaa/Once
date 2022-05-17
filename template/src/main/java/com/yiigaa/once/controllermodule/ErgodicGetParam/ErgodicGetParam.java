package com.yiigaa.once.controllermodule.ErgodicGetParam;

import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/*
**version: v1.0
**Please do not modify the following
**If you modify the following contents, please re LinkModule: ruby Christmas.rb ./Menu/MakeEngineeringNormal/AutoLinkModule

######LinkpackageImport start######
import com.yiigaa.once.controllermodule.ErgodicGetParam.ErgodicGetParam;
######LinkpackageImport end######

######LinkObjectImport start######
        put("ErgodicGetParam", new ErgodicGetParam());
######LinkObjectImport end######

######GradleImport start######
//ErgodicGetParam
######GradleImport end######

######ErrorCodes start######
        //ErgodicGetParam
        put("MODULE_ErgodicGetParam_exception", new String[]{"E-CM01(ErgodicGetParam)", "获取Url 参数崩溃"});
######ErrorCodes end######
*/

public class ErgodicGetParam extends Link {
    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        String[] moduleParam = (String[]) param.get("moduleParam");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {

            Enumeration em = request.getParameterNames();
            while(em.hasMoreElements()) {
                String tempKey = (String) em.nextElement();
                Object parameter = request.getParameter(tempKey);
                String parameterStr= (String) parameter;
                if(parameterStr.startsWith("@")&&parameterStr.endsWith("@")){
                   String subParameter = parameterStr.substring(1, parameterStr.length() - 1);
                   if (!subParameter.equals("")){
                       String[] split = subParameter.split(",");
                       parameter= JSONObject.toJSON(split);
                   }
               }
                passParam.put(tempKey, parameter);
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_ErgodicGetParam_exception");
            returnMap.put("returnParam", returnParam);
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        HashMap<String, Object> returnParam = doStart(param);
        return returnParam;
    }
}


