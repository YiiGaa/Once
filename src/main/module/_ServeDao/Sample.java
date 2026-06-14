package main.module._ServeDao;

import tools.jackson.databind.JsonNode;
import main.common.APIINIT;
import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;
import main.module.Module;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

@RequestMapping("/module")
@RestController
public class Sample {
    @RequestMapping(value="/sample/insert", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleInsert(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"insert",
                    "_table":"test",
                    "_resultKey":"result",
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/insertBatch", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleInsertBatch(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"insert batch",
                    "_target":"get##insertList",
                    "_table":"test",
                    "_isCheckAffect":true,
                    "_resultKey":"result",
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
                    "_table":"test",
                    "_isCheckAffect":true,
                    "_resultKey":"result"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/update", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleUpdate(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"update",
                    "_table":"test",
                    "_isCheckAffect":true,
                    "_resultKey":"result"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/select", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleSelect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"select object",
                    "_table":"test",
                    "_isGetNullError":true,
                    "_isGetNotNullError":false
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/selectList", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleSelectList(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"select list",
                    "_table":"test",
                    "_countResultKey":"count",
                    "_resultKey":"result"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/sqlUpdate", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleSQLUpdate(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"sql update",
                    "_sql":"UPDATE test SET test_value='@value@' WHERE test_id='@id@'",
                    "_isCheckAffect":true,
                    "_resultKey":"result"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/sqlSelect", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleSQLSelect(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
                    "_action":"sql select",
                    "_sql":"SELECT COUNT(*) FROM test WHERE test_value like '@value@'",
                    "_selectType":"select int",
                    "_isGetNullError":false,
                    "_isGetNotNullError":false,
                    "_resultKey":"count"
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = TOOLS.JsonParse("""
                {
                    "_action":"sql select",
                    "_sql":"SELECT * FROM test WHERE test_value like '@value@'",
                    "_selectType":"select list",
                    "_isGetNullError":false,
                    "_isGetNotNullError":false,
                    "_resultKey":"result"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }

    @RequestMapping(value="/sample/transactional", method= RequestMethod.POST)
    @ResponseBody
    public JsonNode sampleTransactional(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String requestParam){
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
            moduleParam =TOOLS.JsonParse("""
                {
                    "_action":"txn start"
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = TOOLS.JsonParse("""
                {
                    "_action":"insert",
                    "_table":"test",
                    "_resultKey":"result_insert"
               }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = TOOLS.JsonParse("""
                {
                    "_action":"txn end"
                }
            """);
            result = Module.Start("_ServeDao", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            //STEP::Call Module
            moduleParam = TOOLS.JsonParse("""
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
            moduleParam = TOOLS.JsonParse("""
                {
                    "_action":"delete",
                    "_table":"test",
                    "_resultKey":"result_delete"
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
            JsonNode passParam = (JsonNode) dataPool.get("passParam");
            passParam = ERRORCODE.GetErrorCode(result, passParam);
            LOGGER.END("API end", passParam);
            return passParam;
        }
    }
}