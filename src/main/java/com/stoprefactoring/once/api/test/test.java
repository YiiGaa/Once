package com.stoprefactoring.once.api.test;

import com.stoprefactoring.once.common.APIINIT;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.common.TOOLS;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.module.Module;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import com.alibaba.fastjson2.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/test")
public class test {
    @Resource(name = "testService")
    private testService _apiService;
    
    @RequestMapping(value="/test", method=RequestMethod.POST)
    @ResponseBody
    public JSONObject testPOST(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        HashMap<String, Object> dataPool = new HashMap<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        JSONObject moduleParam;
    
        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }
    
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataCheck
                moduleParam = JSONObject.parseObject("""
                    {"_isClean": true, "_param": {"opt##test_value": "str##"}}
                """);
                result = Module.Start("_DataCheck", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Service
                result = _apiService.testPOST(dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
    
        } catch(Exception e){
            result = ERRORCODE.ERR_Api_Exception;
            LOGGER.ERROR("API exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }
    
    @RequestMapping(value="/testBatch", method=RequestMethod.POST)
    @ResponseBody
    public JSONObject testBatchPOST(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        HashMap<String, Object> dataPool = new HashMap<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        JSONObject moduleParam;
    
        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }
    
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataCheck
                moduleParam = JSONObject.parseObject("""
                    {"_isClean": true, "_param": {"10##insert": [{"test_value": "str##"}]}}
                """);
                result = Module.Start("_DataCheck", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Service
                result = _apiService.testBatchPOST(dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
    
        } catch(Exception e){
            result = ERRORCODE.ERR_Api_Exception;
            LOGGER.ERROR("API exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }
    
    @RequestMapping(value="/test", method=RequestMethod.DELETE)
    @ResponseBody
    public JSONObject testDELETE(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        HashMap<String, Object> dataPool = new HashMap<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        JSONObject moduleParam;
    
        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }
    
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
                    {"_action": "delete", "_table": "test", "_isCheckAffect": true}
                """);
                result = Module.Start("_ServeDao", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
    
        } catch(Exception e){
            result = ERRORCODE.ERR_Api_Exception;
            LOGGER.ERROR("API exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }
    
    @RequestMapping(value="/test", method=RequestMethod.PUT)
    @ResponseBody
    public JSONObject testPUT(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        HashMap<String, Object> dataPool = new HashMap<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        JSONObject moduleParam;
    
        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }
    
            //STEP::Run task
            do{
    
                //STEP-IN::Call Module-_DataCheck
                moduleParam = JSONObject.parseObject("""
                    {"_isClean": true, "_param": {"10##test_id": ["str##"]}}
                """);
                result = Module.Start("_DataCheck", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Module-_DataFilling
                moduleParam = JSONObject.parseObject("""
                    {"_setting": {"test_value": "time"}}
                """);
                result = Module.Start("_DataFilling", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
                //STEP-IN::Call Module-_ServeDao
                moduleParam = JSONObject.parseObject("""
                    {"_action": "update", "_table": "test", "_isCheckAffect": true, "_isPutReturn": true, "_resultKey": "result"}
                """);
                result = Module.Start("_ServeDao", moduleParam, dataPool);
                if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                    continue;
                }
                result = ERRORCODE.ERR_OK;
                
            }while (false);
    
        } catch(Exception e){
            result = ERRORCODE.ERR_Api_Exception;
            LOGGER.ERROR("API exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }
    
    @RequestMapping(value="/test", method=RequestMethod.GET)
    @ResponseBody
    public JSONObject testGET(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
        List<HashMap<String, Object>> selectorRetryData = new ArrayList<>();
        HashMap<String, Object> dataPool = new HashMap<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        JSONObject moduleParam;
    
        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }
    
            //STEP::Run task
            do{
    
                //STEP-IN::Switch
                {
                    switch (TOOLS.ReadPassParam("option", "", dataPool)){
                        case "object":
                            //STEP-IN::Call Service
                            result = _apiService.testGET_object(dataPool);
                            if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                                continue;
                            }
                            result = ERRORCODE.ERR_OK;
                            
                            break;
                        case "list":
                            //STEP-IN::Call Service
                            result = _apiService.testGET_list(dataPool);
                            if(ERRORCODE.IsError(result, new ERRORCODE[]{})){
                                continue;
                            }
                            result = ERRORCODE.ERR_OK;
                            
                            break;
                        default:
                            result = ERRORCODE.ERR_Api_Switch_Lack;
                    }
                }
                
            }while (false);
    
        } catch(Exception e){
            result = ERRORCODE.ERR_Api_Exception;
            LOGGER.ERROR("API exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

}