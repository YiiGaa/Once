package main.module._OperFile;

import tools.jackson.databind.JsonNode;
import main.common.APIINIT;
import main.common.ERRORCODE;
import main.common.TOOLS;
import main.common.LOGGER;
import main.module.Module;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

@RequestMapping("/module")
@RestController
public class Sample {
    @RequestMapping(value="/sample/upload", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleUpload(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"upload",
                    "_targetKey":"file",
                    "_resultPathKey":"result",
                    "_resultOriginalNameKey":"name",
                    "_resultFileSizeKey":"size"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/delete", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleDelete(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/move", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleMove(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }
}