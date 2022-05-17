package com.yiigaa.once.controller;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controller.Sample;
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
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/sample")
public class Sample {

    @Resource(name = "SampleService")
    private SampleService _SampleService;

	@RequestMapping(value="/simple",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject simpleSample(@RequestBody String requestParam, HttpServletRequest request){
        JSONObject requestJson = JSONObject.parseObject(requestParam);
        JSONObject returnJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", requestJson);
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);
        moduleMap.put("moduleParam", new JSONObject());

        try {

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP: check Neccessary parameters
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("module_isClean","true");
					put("test_name","opt");

            }});
            moduleMap = ControllerModule.invokeMethod("CheckNecessaryParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

			//STEP: filling param
            moduleMap.put("moduleParam", new LinkedHashMap<String, String>(){{
					put("test_name","return@@get##test_name");

            }});
            moduleMap = ControllerModule.invokeMethod("FillingParam", moduleMap);
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

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true);
        }

        
        return returnJson;
    }

	@RequestMapping(value="/database",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addData(@RequestBody String requestParam, HttpServletRequest request){
        JSONObject requestJson = JSONObject.parseObject(requestParam);
        JSONObject returnJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", requestJson);
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);
        moduleMap.put("moduleParam", new JSONObject());

        try {

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP: check Neccessary parameters
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("module_isClean","true");
					put("test_name","opt");

            }});
            moduleMap = ControllerModule.invokeMethod("CheckNecessaryParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

			//STEP: filling param
            moduleMap.put("moduleParam", new LinkedHashMap<String, String>(){{
					put("test_id","abs@@uuid");

            }});
            moduleMap = ControllerModule.invokeMethod("FillingParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

            //STEP call Service
            moduleMap = _SampleService.addData(moduleMap);
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

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true);
        }

        
        return returnJson;
    }

	@RequestMapping(value="/database/batch",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addDataBatch(@RequestBody String requestParam, HttpServletRequest request){
        JSONObject requestJson = JSONObject.parseObject(requestParam);
        JSONObject returnJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", requestJson);
        moduleMap.put("returnParam", new JSONObject());
        moduleMap.put("httpRequest", request);
        moduleMap.put("moduleParam", new JSONObject());

        try {

            COMMON_LOGGER.INIT(moduleMap, "Rest-api start");

            //STEP: check hash parameters
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("module_isClean","true");
					put("module_targetKey","temp_insert");
					put("test_name","opt");

            }});
            moduleMap = ControllerModule.invokeMethod("CheckHashParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

            //STEP: check Neccessary parameters
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("module_isClean","true");
					put("temp_insert","nec");

            }});
            moduleMap = ControllerModule.invokeMethod("CheckNecessaryParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

			//STEP: filling hash param
            moduleMap.put("moduleParam", new LinkedHashMap<String, String>(){{
					put("module_targetKey","temp_insert");
					put("test_id","abs@@uuid");

            }});
            moduleMap = ControllerModule.invokeMethod("FillingHashParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

            //STEP call Service
            moduleMap = _SampleService.addDataBatch(moduleMap);
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

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true);
        }

        
        return returnJson;
    }

	@RequestMapping(value="/database",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getData(HttpServletRequest request){
        JSONObject returnJson = new JSONObject();
        JSONObject requestJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", new JSONObject());
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

            //STEP: check Neccessary parameters
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("module_isClean","true");
					put("test_id","nec");

            }});
            moduleMap = ControllerModule.invokeMethod("CheckNecessaryParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

            //STEP call Service
            moduleMap = _SampleService.getData(moduleMap);
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

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true);
        }
        
        return returnJson;
    }

	@RequestMapping(value="/database/all",method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getDataAll(HttpServletRequest request){
        JSONObject returnJson = new JSONObject();
        JSONObject requestJson = new JSONObject();
        HashMap<String, Object> moduleMap = new HashMap<String, Object>();
        moduleMap.put("passParam", new JSONObject());
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

            //STEP: check Neccessary parameters
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("module_isClean","true");

            }});
            moduleMap = ControllerModule.invokeMethod("CheckNecessaryParam", moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                returnJson = (JSONObject) moduleMap.get("returnParam");
                return returnJson;
            }

            //STEP call Service
            moduleMap = _SampleService.getDataAll(moduleMap);
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

            COMMON_LOGGER.INFO(moduleMap, "Rest-api end", false, true);
        }
        
        return returnJson;
    }



}
