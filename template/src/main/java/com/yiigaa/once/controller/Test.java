package com.yiigaa.once.controller;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controller.Test;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.controllermodule.ControllerModule;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.service.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Controller
@RequestMapping("/test")
public class Test {

    @Resource(name = "TestService")
    private TestService _TestService;

	@RequestMapping(value="/test",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject createTest(@RequestBody String requestParam, HttpServletRequest request){
        JSONObject requestJson = JSONObject.parseObject(requestParam);
        JSONObject returnJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", requestJson);
        moduleMap.put("sessionSave", new JSONObject());
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);
        moduleMap.put("moduleParam", new JSONObject());

        try {

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP call Service
            moduleMap = _TestService.createTest(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }


            //STEP set retrunParam
            returnJson = (JSONObject) moduleMap.get("returnParam");
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));

            returnJson.put("errorCode", "CONTROLLER_API_block");
        }  finally {
            //STEP get errorMessage
            returnJson = ErrorCodes.getErrorMessage(returnJson);

            //STEP return tempStorage Dump
            if(requestJson.get("tempStorage")!=null){
                returnJson.put("tempStorage", requestJson.get("tempStorage"));
            }

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true, true);
        }

        
        return returnJson;
    }

	@RequestMapping(value="/test",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject deleteTest(@RequestBody String requestParam, HttpServletRequest request){
        JSONObject requestJson = JSONObject.parseObject(requestParam);
        JSONObject returnJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", requestJson);
        moduleMap.put("sessionSave", new JSONObject());
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);
        moduleMap.put("moduleParam", new JSONObject());

        try {

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP call Service
            moduleMap = _TestService.deleteTest(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }


            //STEP set retrunParam
            returnJson = (JSONObject) moduleMap.get("returnParam");
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));

            returnJson.put("errorCode", "CONTROLLER_API_block");
        }  finally {
            //STEP get errorMessage
            returnJson = ErrorCodes.getErrorMessage(returnJson);

            //STEP return tempStorage Dump
            if(requestJson.get("tempStorage")!=null){
                returnJson.put("tempStorage", requestJson.get("tempStorage"));
            }

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true, true);
        }

        
        return returnJson;
    }

	@RequestMapping(value="/test",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject updateTest(@RequestBody String requestParam, HttpServletRequest request){
        JSONObject requestJson = JSONObject.parseObject(requestParam);
        JSONObject returnJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", requestJson);
        moduleMap.put("sessionSave", new JSONObject());
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);
        moduleMap.put("moduleParam", new JSONObject());

        try {

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP call Service
            moduleMap = _TestService.updateTest(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }


            //STEP set retrunParam
            returnJson = (JSONObject) moduleMap.get("returnParam");
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));

            returnJson.put("errorCode", "CONTROLLER_API_block");
        }  finally {
            //STEP get errorMessage
            returnJson = ErrorCodes.getErrorMessage(returnJson);

            //STEP return tempStorage Dump
            if(requestJson.get("tempStorage")!=null){
                returnJson.put("tempStorage", requestJson.get("tempStorage"));
            }

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true, true);
        }

        
        return returnJson;
    }

	@RequestMapping(value="/test",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getTest(HttpServletRequest request){
        JSONObject returnJson = new JSONObject();
        JSONObject requestJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", new JSONObject());
        moduleMap.put("sessionSave", new JSONObject());
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);

        try {
			//STEP : ErgodicGetParam
            moduleMap.put("moduleParam", new String[] {
            });
            moduleMap = ControllerModule.invokeMethod("ErgodicGetParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson.put("errorCode", ((JSONObject)moduleMap.get("returnParam")).get("errorCode"));
                return returnJson;
            }
            requestJson = (JSONObject) moduleMap.get("passParam");

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP call Service
            moduleMap = _TestService.getTest(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }


            //STEP set retrunParam
            returnJson = (JSONObject) moduleMap.get("returnParam");
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));

            returnJson.put("errorCode", "CONTROLLER_API_block");
        }  finally {
            //STEP get errorMessage
            returnJson = ErrorCodes.getErrorMessage(returnJson);

            //STEP return tempStorage Dump
            if(requestJson.get("tempStorage")!=null){
                returnJson.put("tempStorage", requestJson.get("tempStorage"));
            }

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true, true);
        }
        
        return returnJson;
    }



}
