package main.common;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class APIINIT {
    private static final StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();

    private static ERRORCODE GetParam(HashMap<String, Object> dataPool, String requestBody, ERRORCODE result){
        ObjectNode passParam = (ObjectNode)dataPool.get("passParam");
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
                                ArrayNode tempArray = TOOLS.JsonInitArray();
                                for (String item : paramValues) {
                                    tempArray.add(item);
                                }
                                passParam.set(paramKey, tempArray);
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
                            JsonNode templ = TOOLS.JsonParse(requestBody);
                            if(templ.isObject())
                                passParam = (ObjectNode) templ;
                            else{
                                return ERRORCODE.ERR_ApiInit_JsonType_Illegal;
                            }
                    }
                    //WHEN-IN::When html form type
                    else if(contentType.startsWith("application/x-www-form-urlencoded")){
                        String[] pairs = requestBody.split("&");
                        for (String pair : pairs) {
                            String[] keyValue = pair.split("=");
                            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
                            if(!passParam.has(key)) {
                                passParam.put(key, value);
                                continue;
                            }
                            JsonNode lastValue = passParam.get(key);
                            if(lastValue.isArray()){
                                ((ArrayNode)lastValue).add(value);
                            } else{
                                ArrayNode tempArray = TOOLS.JsonInitArray();
                                tempArray.add(lastValue);
                                tempArray.add(value);
                                passParam.set(key, tempArray);
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
                                ArrayNode arrayNode = TOOLS.JsonInitArray();
                                for(String value : paramValues) {
                                    arrayNode.add(value);
                                }
                                passParam.set(paramKey, arrayNode);
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
                                    ArrayNode fileList = TOOLS.JsonInitArray();
                                    for (int i = 0; i < paramValues.size(); i++) {
                                        fileList.add(fileMarkValue);
                                    }
                                    passParam.set(paramKey, fileList);
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
            return ERRORCODE.ERR_ApiInit_Param_Exception;
        }
        return result;
    }

    public static StandardServletMultipartResolver GetMultipartResolver(){
        return multipartResolver;
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
        dataPool.put("passParam", TOOLS.JsonInitObject());
        dataPool.put("httpRequest", request);
        dataPool.put("httpResponse", response);
        dataPool.put("needEndModuleCall", new ArrayList<ArrayList<String>>());

        //STEP::Init data pool
        result = GetParam(dataPool, requestBody, result);

        return result;
    }
}
