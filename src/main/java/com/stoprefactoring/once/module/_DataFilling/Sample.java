package com.stoprefactoring.once.module._DataFilling;

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
    @RequestMapping(value="/sample", method= RequestMethod.POST)
    @ResponseBody
    public JSONObject sample(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_isSetReturnParam":true,
                    "_isSessionNullError":true,
                    "_setting":{
                        "key-1":null,
                        "key-2":[],
                        "opt##key-3":3.5,
                        "key-4":{
                            "key-4-1":3.4,
                            "key-4-2":[
                                {"key-4-2-1":"value"},
                                false
                            ]
                        },
                        "push##key-5":[
                            "123",
                            true
                        ],
                        "key-6>>key-6-1>>1>>key-6-1-1":false,
                        "key-7":"uuid",
                        "key-8":"uuid short",
                        "key-9":"random id",
                        "key-10":"get##key-3",
                        "key-1@get##key-3@":"time"
                   }
               }
            """);
            result = Module.Start("_DataFilling", moduleParam, dataPool);
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