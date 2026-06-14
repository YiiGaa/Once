package main.module._DataFilling;

import java.util.HashMap;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.common.APIINIT;
import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;
import main.module.Module;

@RequestMapping("/module")
@RestController
public class Sample {
    @RequestMapping(value="/sample", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sample(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
        HashMap<String, Object> dataPool = new HashMap<>();
        ERRORCODE result = ERRORCODE.ERR_OK;
        JsonNode moduleParam;

        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            Module.Init(dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = TOOLS.JsonParse("""
                {
                    "_isSessionNullError":true,
                    "_setting":{
                        "key-1":null,
                        "key-2":[],
                        "opt##key-3":3.5,
                        "key-4":{
                            "key-4-1":3.4,
                            "key-4-2":[
                                {"uuid":"uuid short"}
                            ]
                        },
                        "key-5":"get##key-5",
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
            System.err.println("API Exception: " );
        } catch(Exception e){
            e.printStackTrace();
            result = ERRORCODE.ERR_Api_Exception;
        } finally {
            Module.End(dataPool, result);
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }
}