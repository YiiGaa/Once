package com.stoprefactoring.once.module._OperSession;

import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.APIINIT;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.module.Module;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
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

        dataPool.put("response", response);

        try {
            //STEP::Init data pool
            result = APIINIT.InitDataPool(dataPool, request, response, requestParam);
            LOGGER.INIT("API", "API start", dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            System.out.println("> Before save session");
            Enumeration<String> attributeNames = request.getSession().getAttributeNames();
            while (attributeNames!=null && attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = request.getSession().getAttribute(attributeName);
                System.out.println("- key:" + attributeName + ", value:" + attributeValue.toString());
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"save",
                    "key 1":"value",
                    "key 2":"get##put",
                    "key 3":{"aa":"bb"}
                }
            """);
            result = Module.Start("_OperSession", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            System.out.println("> Save session");
            attributeNames = request.getSession().getAttributeNames();
            while (attributeNames!=null && attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = request.getSession().getAttribute(attributeName);
                System.out.println("- key:" + attributeName + ", value:" + attributeValue.toString());
            }

            //STEP::Call Module
            moduleParam = JSONObject.parseObject("""
                {
                    "_action":"delete",
                    "_key":[
                        "key 1"
                    ]
                }
            """);
            result = Module.Start("_OperSession", moduleParam, dataPool);
            if(ERRORCODE.IsError(result)){
                return null;
            }

            System.out.println("> After delete session");
            attributeNames = request.getSession().getAttributeNames();
            while (attributeNames!=null && attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = request.getSession().getAttribute(attributeName);
                System.out.println("- key:" + attributeName + ", value:" + attributeValue.toString());
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