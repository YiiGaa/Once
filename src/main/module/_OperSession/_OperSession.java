package main.module._OperSession;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.*;

import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;
import main.module.Link;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.Map.Entry;

public class _OperSession extends Link {

    private ERRORCODE Save(ObjectNode moduleParam, ObjectNode passParam, HttpServletRequest request, ERRORCODE result){
        //STEP::Get Setting
        JsonNode _defaultValue = TOOLS.ReadParam("_defaultValue", (JsonNode)null, moduleParam, passParam);
        Boolean _isNullError = TOOLS.ReadParam("_isNullError", true, moduleParam, passParam);
        ObjectNode _setting = TOOLS.ReadParam("_setting", TOOLS.JsonInitObject(), moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperSession Save param, _defaultValue:" + _defaultValue);
        LOGGER.DEBUG("Module-_OperSession Save param, _isNullError:" + _isNullError);
        LOGGER.DEBUG("Module-_OperSession Save param, _setting:" + _setting);

        //STEP::Save session
        Set<Entry<String,JsonNode>> fields = _setting.properties();
        for (Entry<String,JsonNode> entry : fields) {
            //STEP-IN::Get value
            String key = entry.getKey();
            JsonNode value = TOOLS.ReadParam(key, (JsonNode) null, _setting, passParam);
            if(value == null){
                LOGGER.DEBUG("Module-_OperSession Save value is empty, key:"+key);
                if(_isNullError)
                    return ERRORCODE.ERR_Module__OperSession_Save_Empty;
                if(_defaultValue == null)
                    continue;
                value = _defaultValue;
            }

            //STEP-IN::Save session
            String targetValue = value.toString();
            LOGGER.DEBUG("Module-_OperSession Save session, key:" + key+", value:" + targetValue);
            request.getSession().setAttribute(key, targetValue);
        }

        return result;
    }

    private ERRORCODE Delete(ObjectNode moduleParam, ObjectNode passParam, HttpServletRequest request, ERRORCODE result){
        //STEP::Get Setting
        ArrayNode _key = TOOLS.ReadParam("_key", TOOLS.JsonInitArray(), moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperSession Delete param, _key:" + _key);

        //STEP::Delete session
        for(JsonNode item : _key) {
            if(!(item.isString())){
                continue;
            }

            //STEP-IN::Try get from passParam
            String key = item.asString();
            if(key.startsWith("get##")){
                String keyPass = TOOLS.JsonPathChangeReal(key.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPass);
                if(returnValue.isString()) {
                    key = returnValue.asString();
                }else{
                    continue;
                }
            }

            //STEP-IN::Remove session
            LOGGER.DEBUG("Module-_OperSession Delete session, key:" + key);
            request.getSession().removeAttribute(key);
        }

        return result;
    }

    private ERRORCODE DoStart(ObjectNode moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        ObjectNode passParam = (ObjectNode) dataPool.get("passParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            String _action = TOOLS.ReadParam("_action", "", moduleParam, passParam);

            switch (_action) {
                case "save":
                    result = Save(moduleParam, passParam, request, result);
                    break;
                case "delete":
                    result = Delete(moduleParam, passParam, request, result);
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
    public ERRORCODE Start(ObjectNode moduleParam, HashMap<String, Object> dataPool){
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