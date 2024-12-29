package com.stoprefactoring.once.api.test;

import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.common.TOOLS;
import com.stoprefactoring.once.module.Module;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("testService")
public class testService {
    
    public ERRORCODE testPOST(HashMap<String, Object> dataPool) {
        LOGGER.DEBUG("SERVICE testPOST start", dataPool);
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        LOGGER.ChangePosition("SERVICE");
        JSONObject moduleParam;
    
        try {
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataFilling
                moduleParam = JSONObject.parseObject("""
                    {"_setting": {"test_id": "uuid", "opt##test_value": "time"}}
                """);
                result = Module.Start("_DataFilling", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Module-_ServeDao
                moduleParam = JSONObject.parseObject("""
                    {"_action": "insert", "_table": "test", "_isPutReturn": true, "_isCheckAffect": true}
                """);
                result = Module.Start("_ServeDao", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
        } catch(Exception e){
            result = ERRORCODE.ERR_Service_Exception;
            LOGGER.ERROR("SERVICE exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally{
            Module.End(dataPool, result);
        }
    
        LOGGER.DEBUG("SERVICE testPOST end", dataPool);
        return result;
    }
    
    public ERRORCODE testBatchPOST(HashMap<String, Object> dataPool) {
        LOGGER.DEBUG("SERVICE testBatchPOST start", dataPool);
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        LOGGER.ChangePosition("SERVICE");
        JSONObject moduleParam;
    
        try {
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataFilling
                moduleParam = JSONObject.parseObject("""
                    {"_setting": {"insert": [{"test_id": "uuid"}]}}
                """);
                result = Module.Start("_DataFilling", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Module-_ServeDao
                moduleParam = JSONObject.parseObject("""
                    {"_action": "insert batch", "_table": "test", "_target": "get##insert", "_resultKey": "result", "_isPutReturn": true, "_isCheckAffect": true}
                """);
                result = Module.Start("_ServeDao", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
        } catch(Exception e){
            result = ERRORCODE.ERR_Service_Exception;
            LOGGER.ERROR("SERVICE exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally{
            Module.End(dataPool, result);
        }
    
        LOGGER.DEBUG("SERVICE testBatchPOST end", dataPool);
        return result;
    }
    
    public ERRORCODE testGET_object(HashMap<String, Object> dataPool) {
        LOGGER.DEBUG("SERVICE testGET_object start", dataPool);
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        LOGGER.ChangePosition("SERVICE");
        JSONObject moduleParam;
    
        try {
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataCheck
                moduleParam = JSONObject.parseObject("""
                    {"_isClean": true, "_param": {"test_id": "str##"}}
                """);
                result = Module.Start("_DataCheck", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Module-_ServeDao
                moduleParam = JSONObject.parseObject("""
                    {"_action": "select object", "_table": "test", "_isCheckAffect": true, "_isGetNullError": true, "_isPutReturn": true, "_resultKey": ""}
                """);
                result = Module.Start("_ServeDao", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
        } catch(Exception e){
            result = ERRORCODE.ERR_Service_Exception;
            LOGGER.ERROR("SERVICE exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally{
            Module.End(dataPool, result);
        }
    
        LOGGER.DEBUG("SERVICE testGET_object end", dataPool);
        return result;
    }
    
    public ERRORCODE testGET_list(HashMap<String, Object> dataPool) {
        LOGGER.DEBUG("SERVICE testGET_list start", dataPool);
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        LOGGER.ChangePosition("SERVICE");
        JSONObject moduleParam;
    
        try {
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataCheck
                moduleParam = JSONObject.parseObject("""
                    {"_isClean": true, "_param": {"page": "int##", "pageSize": "int##"}}
                """);
                result = Module.Start("_DataCheck", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Module-_ServeDao
                moduleParam = JSONObject.parseObject("""
                    {"_action": "select list", "_table": "test", "_page": "get##page", "_pageSize": "get##pageSize", "_countResultKey": "count", "_resultKey": "result", "_isPutReturn": true}
                """);
                result = Module.Start("_ServeDao", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
        } catch(Exception e){
            result = ERRORCODE.ERR_Service_Exception;
            LOGGER.ERROR("SERVICE exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally{
            Module.End(dataPool, result);
        }
    
        LOGGER.DEBUG("SERVICE testGET_list end", dataPool);
        return result;
    }

}