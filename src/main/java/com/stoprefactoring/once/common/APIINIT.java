package com.stoprefactoring.once.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class APIINIT {
    private static final MultipartResolver multipartResolver = new StandardServletMultipartResolver();

    private static ERRORCODE GetParam(HashMap<String, Object> dataPool, String requestBody, ERRORCODE result){
        JSONObject passParam = (JSONObject)dataPool.get("passParam");
        HttpServletRequest request = (HttpServletRequest) dataPool.get("httpRequest");

        try {
            switch (request.getMethod()){
                //WHEN::Do nothing when request method is MOCK
                case "MOCK":
                    break;

                //WHEN::Get param from url when request method is GET
                case "GET": {
                        Map<String, String[]> parameterMap = request.getParameterMap();
                        if (parameterMap.isEmpty())
                            break;
                        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                            String paramKey = entry.getKey();
                            String[] paramValues = entry.getValue();
                            if (paramValues.length == 1) {
                                passParam.put(paramKey, paramValues[0]);
                            } else {
                                JSONArray tempArray = new JSONArray();
                                for (String item : paramValues) {
                                    tempArray.add(item);
                                }
                                passParam.put(paramKey, tempArray);
                            }
                        }
                    }
                    break;

                //WHEN::Get param from request body when request method is others
                default:
                    String contentType = request.getContentType().toLowerCase();
                    //WHEN-IN::When json type
                    if(contentType.startsWith("application/json") ||
                       contentType.startsWith("text/json")){
                            passParam = JSONObject.parseObject(requestBody);
                    }
                    //WHEN-IN::When html form type
                    else if(contentType.startsWith("application/x-www-form-urlencoded")){
                        String[] pairs = requestBody.split("&");
                        for (String pair : pairs) {
                            String[] keyValue = pair.split("=");
                            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
                            if(!passParam.containsKey(key)) {
                                passParam.put(key, value);
                                continue;
                            }
                            Object lastValue = passParam.get(key);
                            if(lastValue instanceof JSONArray){
                                ((JSONArray)lastValue).add(value);
                            } else{
                                JSONArray tempArray = new JSONArray();
                                tempArray.add(lastValue);
                                tempArray.add(value);
                                passParam.put(key, tempArray);
                            }
                        }
                    }
                    //WHEN-IN::When form type
                    else if(contentType.startsWith("multipart/form-data")){
                        //STEP-IN-IN::Get form data
                        if(!multipartResolver.isMultipart(request)){
                            break;
                        }
                        //STEP-IN-IN::Get from form
                        Map<String, String[]> parameterMap = ((MultipartHttpServletRequest) request).getParameterMap();
                        if (parameterMap.isEmpty())
                            break;
                        //STEP-IN-IN::Organizing form data
                        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                            String paramKey = entry.getKey();
                            String[] paramValues = entry.getValue();
                            if (paramValues.length == 1) {
                                passParam.put(paramKey, paramValues[0]);
                            } else if(paramValues.length > 1) {
                                passParam.put(paramKey, new JSONArray((Object) paramValues));
                            }
                        }
                        //STEP-IN-IN::Organizing file data
                        Map<String, List<MultipartFile>> fileMap = ((MultipartHttpServletRequest) request).getMultiFileMap();
                        if (!fileMap.isEmpty()) {
                            String fileMarkValue = "##file##";
                            for (Map.Entry<String, List<MultipartFile>> entry : fileMap.entrySet()) {
                                String paramKey = entry.getKey();
                                List<MultipartFile> paramValues = entry.getValue();
                                if (paramValues.size() == 1) {
                                    passParam.put(paramKey, fileMarkValue);
                                } else {
                                    JSONArray fileList = new JSONArray();
                                    for (int i = 0; i < paramValues.size(); i++) {
                                        fileList.add(fileMarkValue);
                                    }
                                    passParam.put(paramKey, fileList);
                                }
                            }
                        }
                    }
                    //WHEN-IN::When other type
                    else{
                        return ERRORCODE.ERR_ApiInit_ContentType_Illegal;
                    }
            }
            dataPool.put("passParam", passParam);
        } catch (Exception e) {
            System.out.println(TOOLS.GetExceptionInfo(e));
            return ERRORCODE.ERR_ApiInit_Param_Exception;
        }
        return result;
    }

    public static ERRORCODE InitDataPool(HashMap<String, Object> dataPool, HttpServletRequest request, HttpServletResponse response, String requestBody){
        ERRORCODE result = ERRORCODE.ERR_OK;

        //STEP::Try mock request
        if(request == null) {
            request = Mockito.mock(HttpServletRequest.class);
            HttpSession sessionMock = Mockito.mock(HttpSession.class);
            Mockito.when(request.getSession()).thenReturn(sessionMock);
            Mockito.when(request.getHeader("X-Real-IP")).thenReturn("0.0.0.0");
            StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
            String codePath = stackTrace.getClassName()+"."+stackTrace.getMethodName();
            Mockito.when(request.getRequestURI()).thenReturn(codePath);
            Mockito.when(request.getMethod()).thenReturn("MOCK");
        }

        //STEP::Try mock response
        if(response == null) {
            response = Mockito.mock(HttpServletResponse.class);
        }

        //STEP::Init data pool
        dataPool.put("passParam", new JSONObject());
        dataPool.put("returnParam", new JSONObject());
        dataPool.put("httpRequest", request);
        dataPool.put("httpResponse", response);
        dataPool.put("needEndModuleCall_api", new HashSet<String>());
        dataPool.put("needEndModuleCall_service", new HashSet<String>());

        //STEP::Init data pool
        result = GetParam(dataPool, requestBody, result);

        return result;
    }
}
