package com.stoprefactoring.once.module._OperFile;

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
    @RequestMapping(value="/sample/upload", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleUpload(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"upload",
                    "_targetKey":"file",
                    "_resultPathKey":"result",
                    "_isPutReturn":true,
                    "_resultOriginalNameKey":"name",
                    "_resultFileSizeKey":"size",
                }
            """);
            result = Module.Start("_OperFile", moduleParam, dataPool);
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
                    "_path":"get##file",
                    "_isNullError":false
                }
            """);
            result = Module.Start("_OperFile", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool,result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }

    @RequestMapping(value="/sample/move", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sampleMove(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"move",
                    "_sourcePath":"get##source",
                    "_targetPath":"get##target"
                }
            """);
            result = Module.Start("_OperFile", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool,result);
            JSONObject returnJson = (JSONObject) dataPool.get("returnParam");
            returnJson = ERRORCODE.GetErrorCode(result, returnJson);
            LOGGER.END("API end", returnJson);
            return returnJson;
        }
    }
}