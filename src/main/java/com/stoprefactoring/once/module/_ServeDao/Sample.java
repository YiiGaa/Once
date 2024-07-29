package com.stoprefactoring.once.module._ServeDao;

import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.APIINIT;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.module.Module;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

@RequestMapping("/module")
@RestController
public class Sample {
    @RequestMapping(value="/sample/insert", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleInsert(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"insert",
                    "_table":"test",
                    "_resultKey":"result",
                    "_isPutReturn":true,
                    "_isReplace":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/insertBatch", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleInsertBatch(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"insert batch",
                    "_target":"get##insertList",
                    "_table":"test",
                    "_isCheckAffect":true,
                    "_resultKey":"result",
                    "_isPutReturn":true,
                    "_isReplace":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/delete", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleDelete(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"delete",
                    "_table":"test",
                    "_isCheckAffect":true,
                    "_resultKey":"result",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/update", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleUpdate(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"update",
                    "_table":"test",
                    "_isCheckAffect":true,
                    "_resultKey":"result",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/select", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleSelect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"select object",
                    "_table":"test",
                    "_isGetNullError":true,
                    "_isGetNotNullError":false,
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/selectList", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleSelectList(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"select list",
                    "_table":"test",
                    "_countResultKey":"count",
                    "_resultKey":"result",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/sqlUpdate", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleSQLUpdate(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"sql update",
                    "_sql":"UPDATE test SET test_value='@value@' WHERE test_id='@id@'",
                    "_isCheckAffect":true,
                    "_resultKey":"result",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/sqlSelect", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleSQLSelect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"sql select",
                    "_sql":"SELECT COUNT(*) FROM test WHERE test_value like '@value@'",
                    "_selectType":"select int",
                    "_isGetNullError":false,
                    "_isGetNotNullError":false,
                    "_resultKey":"count",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"sql select",
                    "_sql":"SELECT * FROM test WHERE test_value like '@value@'",
                    "_selectType":"select list",
                    "_isGetNullError":false,
                    "_isGetNotNullError":false,
                    "_resultKey":"result",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/transactional", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleTransactional(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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

            //STEP::Call Module
            moduleParam =JSONObject.parseObject("""
                {
                    "_action":"txn start"
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"insert",
                    "_table":"test",
                    "_resultKey":"result_insert",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"txn end"
                }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"txn start",
                    "_isAutoErrorRollBack":false,
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"delete",
                    "_table":"test",
                    "_resultKey":"result_delete",
                    "_isPutReturn":true
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }
}