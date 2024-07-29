package com.stoprefactoring.once.common;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class LOGGER {
    public static ThreadLocal<HashMap<String, String>> loggerHeaders = new ThreadLocal<>();
    public static ThreadLocal<ArrayList<String>> loggerPosition = new ThreadLocal<>();
    private static final Logger LOGGER = LogManager.getLogger();

    public static void INIT(String position, String msg, HashMap<String, Object> dataPool) {
        //STEP::Mark logger header info
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("requestId", UUID.randomUUID().toString());
        if(request.getSession().getAttribute("userId") == null) {
            headers.put("userId", "null");
        } else {
            headers.put("userId", request.getSession().getAttribute("userId").toString());
        }
        String ipFromNginx = request.getHeader("X-Real-IP");
        headers.put("remoteIp", (ipFromNginx==null||ipFromNginx==""||ipFromNginx==" ")?request.getRemoteAddr():ipFromNginx);
        headers.put("callPath", request.getRequestURI()+"."+request.getMethod());
        headers.put("position", position);
        loggerHeaders.set(headers);

        //STEP::Mark position info
        ArrayList<String> positionsArrays = new ArrayList<>();
        positionsArrays.add(position);
        loggerPosition.set(positionsArrays);

        //STEP::Print init log
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        msg += ",callPath:"+headers.get("callPath")+
                ",userId:"+headers.get("userId")+
                ",remoteIp:"+headers.get("remoteIp");
        LOGGER.info(InnerOutputFormat(msg, codePath, "{-}", ((JSONObject)dataPool.get("passParam")).toString(), "{-}"));
    }

    public static void END(String msg, JSONObject returnParam) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.info(InnerOutputFormat(msg, codePath, "{-}", "{-}", returnParam.toString()));
        loggerHeaders.remove();
        loggerPosition.remove();
    }

    public static void ChangePosition(String position) {
        HashMap<String, String> headers = loggerHeaders.get();
        ArrayList<String> positionsArrays = loggerPosition.get();
        if(position == null && !positionsArrays.isEmpty())
            positionsArrays.removeLast();
        else if(position != null)
            positionsArrays.add(position);
        position = !positionsArrays.isEmpty()?positionsArrays.getLast():"";
        headers.put("position", position);
        loggerHeaders.set(headers);
    }

    public static String GetPosition() {
        ArrayList<String> positionsArrays = loggerPosition.get();
        return !positionsArrays.isEmpty()?positionsArrays.getLast():"";
    }

    public static String GetCallPosition() {
        ArrayList<String> positionsArrays = loggerPosition.get();
        int index = Math.max(positionsArrays.size() - 2, 0);
        return !positionsArrays.isEmpty()?positionsArrays.get(index):"";
    }

    private static String InnerOutputFormat(String msg, String codePath, String moduleParam, String passParam, String returnParam) {
        HashMap<String, String> headers = loggerHeaders.get();

        return  "["+headers.get("requestId")+ "]"+
                "["+headers.get("position")+"]"+
                msg+
                ","+codePath+
                ",moduleParam:"+moduleParam+
                ",passParam:"+passParam+
                ",returnParam:"+returnParam;
    }

    public static void DEBUG(String msg) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, null, null,  null);
    }
    public static void DEBUG(String msg, JSONObject moduleParam, JSONObject passParam, JSONObject returnParam) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, moduleParam, passParam, returnParam);
    }
    public static void DEBUG(String msg, JSONObject moduleParam, HashMap<String, Object> dataPool) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, moduleParam, (JSONObject)dataPool.get("passParam"), (JSONObject)dataPool.get("returnParam"));
    }
    public static void DEBUG(String msg, HashMap<String, Object> dataPool) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, null, (JSONObject)dataPool.get("passParam"), (JSONObject)dataPool.get("returnParam"));
    }
    private static void InnerDEBUG(String msg, JSONObject moduleParam, JSONObject passParam, JSONObject returnParam) {
        String passParamStr = passParam!=null?passParam.toString():"{-}";
        String moduleParamStr = moduleParam!=null?moduleParam.toString():"{-}";
        String returnParamStr = returnParam!=null?returnParam.toString():"{-}";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.debug(InnerOutputFormat(msg, codePath, moduleParamStr, passParamStr, returnParamStr));
    }

    public static void ERROR(String msg) {
        InnerERROR(msg, null, null,  null);
    }
    public static void ERROR(String msg, JSONObject moduleParam, JSONObject passParam, JSONObject returnParam) {
        InnerERROR(msg, moduleParam, passParam, returnParam);
    }
    public static void ERROR(String msg, JSONObject moduleParam, HashMap<String, Object> dataPool) {
        InnerERROR(msg, moduleParam, (JSONObject)dataPool.get("passParam"), (JSONObject)dataPool.get("returnParam"));
    }
    public static void ERROR(String msg, HashMap<String, Object> dataPool) {
        InnerERROR(msg, null, (JSONObject)dataPool.get("passParam"), (JSONObject)dataPool.get("returnParam"));
    }
    private static void InnerERROR(String msg, JSONObject moduleParam, JSONObject passParam, JSONObject returnParam) {
        String passParamStr = passParam!=null?passParam.toString():"{-}";
        String moduleParamStr = moduleParam!=null?moduleParam.toString():"{-}";
        String returnParamStr = returnParam!=null?returnParam.toString():"{-}";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.info(InnerOutputFormat(msg, codePath, moduleParamStr, passParamStr, returnParamStr));
    }

    public static void INFO(String msg) {
        InnerINFO(msg, null, null,  null);
    }
    public static void INFO(String msg, JSONObject moduleParam, JSONObject passParam, JSONObject returnParam) {
        InnerINFO(msg, moduleParam, passParam, returnParam);
    }
    public static void INFO(String msg, JSONObject moduleParam, HashMap<String, Object> dataPool) {
        InnerINFO(msg, moduleParam, (JSONObject)dataPool.get("passParam"), (JSONObject)dataPool.get("returnParam"));
    }
    public static void INFO(String msg, HashMap<String, Object> dataPool) {
        InnerINFO(msg, null, (JSONObject)dataPool.get("passParam"), (JSONObject)dataPool.get("returnParam"));
    }
    private static void InnerINFO(String msg, JSONObject moduleParam, JSONObject passParam, JSONObject returnParam) {
        String passParamStr = passParam!=null?passParam.toString():"{-}";
        String moduleParamStr = moduleParam!=null?moduleParam.toString():"{-}";
        String returnParamStr = returnParam!=null?returnParam.toString():"{-}";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.info(InnerOutputFormat(msg, codePath, moduleParamStr, passParamStr, returnParamStr));
    }

    public static void StraightDebug(String msg) {
        LOGGER.debug(msg);
    }
    public static void StraightError(String msg) {
        LOGGER.error(msg);
    }
    public static void StraightInfo(String msg) {
        LOGGER.info(msg);
    }
}