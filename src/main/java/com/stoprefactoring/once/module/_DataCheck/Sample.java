package com.stoprefactoring.once.module._DataCheck;

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
                    "_isClean":true,
                    "_param":{
                        "key-1":12345666L,
                        "key-2":"reg##123",
                        "key-3":"int##1",
                        "key-4":"str##test/best",
                        "key-5":"double##3.5/5.4",
                        "opt##key-6":"uuid",
                        "key-7":"not##abc/bcd",
                        "key-8":{
                            "key-8-1":true,
                            "key-8-2":[],
                            "key-8-3":[
                                {"key-8-3-1":"value","key-8-3-2":"value","key-8-3-3":123},
                                true,
                                123
                            ],
                            "key-8-4>>abc>>x":"123",
                            "key-8-4>>aaa>>xx>>1>>xx":"yy"
                        },
                        "key-9":[
                            {"key-9-1":"value","key-9-2":"value","key-9-3":123},
                            true,
                            123
                        ]
                    }
                }
            """);
            result = Module.Start("_DataCheck", moduleParam, dataPool);
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