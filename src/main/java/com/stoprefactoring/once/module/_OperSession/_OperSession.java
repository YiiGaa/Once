package com.stoprefactoring.once.module._OperSession;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.common.TOOLS;
import com.stoprefactoring.once.module.Link;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

public class _OperSession extends Link {

    private ERRORCODE Save(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, HttpServletRequest request, ERRORCODE result){
        //STEP::Get Setting
        Object _defaultValue = TOOLS.ReadParam("_defaultValue", (Object)null, moduleParam, passParam);
        Boolean _isNullError = TOOLS.ReadParam("_isNullError", true, moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperSession Save param, _defaultValue:" + _defaultValue);
        LOGGER.DEBUG("Module-_OperSession Save param, _isNullError:" + _isNullError);

        //STEP::Save session
        for(String key : moduleParam.keySet()){
            if(key.equals("_action")||
               key.equals("_defaultValue")||
               key.equals("_isNullError")
            ) continue;

            //STEP-IN::Get value
            Object value = TOOLS.ReadParam(key, (Object) null, moduleParam, passParam);
            if(value == null){
                LOGGER.DEBUG("Module-_OperSession Save value is empty, key:"+key);
                if(_isNullError)
                    return ERRORCODE.ERR_Module__OperSession_Save_Empty;
                if(_defaultValue == null)
                    continue;
            }

            //STEP-IN::Save session
            LOGGER.DEBUG("Module-_OperSession Save session, key:" + key+", value:" + (value==null?_defaultValue:value));
            request.getSession().setAttribute(key, value==null?_defaultValue:value);
        }

        return result;
    }

    private ERRORCODE Delete(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, HttpServletRequest request, ERRORCODE result){
        //STEP::Get Setting
        JSONArray _key = TOOLS.ReadParam("_key", new JSONArray(), moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperSession Delete param, _key:" + _key.toString());

        //STEP::Delete session
        for(Object item : _key) {
            if(!(item instanceof String key)){
                continue;
            }

            //STEP-IN::Try get from passParam
            if(key.startsWith("get##")){
                String keyPass = TOOLS.JsonPathChangeReal(key.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof String) {
                    key = (String)returnValue;
                }else{
                    continue;
                }
            }

            //STEP-IN::Remove session
            request.getSession().removeAttribute(key);
        }

        return result;
    }

    private ERRORCODE DoStart(JSONObject moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        JSONObject passParam = (JSONObject) dataPool.get("passParam");
        JSONObject returnParam = (JSONObject) dataPool.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            String _action = TOOLS.ReadParam("_action", "", moduleParam, passParam);

            switch (_action) {
                case "save":
                    result = Save(moduleParam, passParam, returnParam, request, result);
                    break;
                case "delete":
                    result = Delete(moduleParam, passParam, returnParam, request, result);
                    break;
                default:
                    result = ERRORCODE.ERR_Module__OperSession_Action_Illegal;
            }

        } catch (Exception e) {
            LOGGER.ERROR("Module-_OperSession exception:"+TOOLS.GetExceptionInfo(e), moduleParam, dataPool);
            result = ERRORCODE.ERR_Module__OperSession_Exception;
        }

        return result;
    }

    @Override
    public ERRORCODE Start(JSONObject moduleParam, HashMap<String, Object> dataPool){
        LOGGER.DEBUG("Module-_OperSession start", moduleParam, dataPool);
        ERRORCODE result = ERRORCODE.ERR_OK;
        result = DoStart(moduleParam, dataPool, result);
        LOGGER.DEBUG("Module-_OperSession end", moduleParam, dataPool);
        return result;
    }

    @Override
    public ERRORCODE End(HashMap<String, Object> dataPool, ERRORCODE result){
        LOGGER.DEBUG("Module-_OperSession End start", dataPool);
        try{
            //Some work of cleaning up the module（When Controller end or Service end）
            //You need to set "Module.moduleNeedCallEnd.put("_OperSession",true)" in _OperSessionConfig.Init() to take effect
        }catch (Exception e){LOGGER.ERROR("Module-_OperSession End for clean exception:"+ TOOLS.GetExceptionInfo(e), dataPool);}
        LOGGER.DEBUG("Module-_OperSession End end", dataPool);
        return result;
    }
}