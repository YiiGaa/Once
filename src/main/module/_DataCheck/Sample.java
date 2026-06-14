package main.module._DataCheck;

import tools.jackson.databind.JsonNode;
import main.common.APIINIT;
import main.common.ERRORCODE;
import main.common.LOGGER;
import main.module.Module;
import main.common.TOOLS;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

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
                    "_isClean":true,
                    "_param":{
                        "key-1":12345666234234,
                        "key-2":"reg##123",
                        "key-3":"bool##true",
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
                        "2##key-9":[
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }
}