package com.yiigaa.once.servicecommon;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class COMMON_LOGGER {
    private static final Logger LOGGER = LogManager.getLogger();

    private static String InnerOutputFormat(String codePath, String msg, String passParam, String returnParam, String moduleParam) {
        HashMap<String, String> headers = com.yiigaa.once.controllercommon.COMMON_LOGGER.loggerHeaders.get();
        return  "["+headers.get("requestId")+ "]"+
                "[message:"+ msg+ "]"+
                "[codePath:"+ codePath+ "]"+
                "[passParam:"+ passParam+ "]"+
                "[moduleParam:"+ moduleParam+ "]"+
                "[returnParam:"+ returnParam+ "]";
    }

    public static void INFO(HashMap<String, Object> moduleMap) {
        InnerINFO(moduleMap, "", false, false, false);
    }
    public static void INFO(HashMap<String, Object> moduleMap, String msg) {
        InnerINFO(moduleMap, msg, false, false, false);
    }
    public static void INFO(HashMap<String, Object> moduleMap, String msg, boolean isShowpassParam) {
        InnerINFO(moduleMap, msg, isShowpassParam, false, false);
    }
    public static void INFO(HashMap<String, Object> moduleMap, String msg, boolean isShowpassParam , boolean isShowreturnParam) {
        InnerINFO(moduleMap, msg, isShowpassParam, isShowreturnParam, false);
    }
    public static void INFO(HashMap<String, Object> moduleMap, String msg, boolean isShowpassParam , boolean isShowreturnParam, boolean isShowmoduleParam) {
        InnerINFO(moduleMap, msg, isShowpassParam, isShowreturnParam, isShowmoduleParam);
    }
    private static void InnerINFO(HashMap<String, Object> moduleMap, String msg, boolean isShowpassParam , boolean isShowreturnParam, boolean isShowmoduleParam) {
        String passParam = (isShowpassParam||LOGGER.isDebugEnabled())&&(moduleMap!=null)?((JSONObject) moduleMap.get("passParam")).toString():"hide";
        String moduleParam = (isShowmoduleParam||LOGGER.isDebugEnabled())&&(moduleMap!=null)?moduleMap.get("moduleParam").toString():"hide";
        String returnParam = (isShowreturnParam||LOGGER.isDebugEnabled())&&(moduleMap!=null)?((JSONObject) moduleMap.get("returnParam")).toString():"hide";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+","+stackTrace.getLineNumber();

        LOGGER.info(InnerOutputFormat(codePath, msg, passParam, returnParam, moduleParam));
    }

    public static void DEBUG(HashMap<String, Object> moduleMap, String msg) {
        if(LOGGER.isDebugEnabled()){
            String passParam = (moduleMap == null)?"null":((JSONObject) moduleMap.get("passParam")).toString();
            String moduleParam = (moduleMap == null)?"null":moduleMap.get("moduleParam").toString();
            String returnParam = (moduleMap == null)?"null":((JSONObject) moduleMap.get("returnParam")).toString();

            StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
            String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+","+stackTrace.getLineNumber();

            LOGGER.debug(InnerOutputFormat(codePath, msg, passParam, returnParam, moduleParam));
        }
    }

    public static void ERROR(HashMap<String, Object> moduleMap, String msg) {
        String passParam = (moduleMap == null)?"null":((JSONObject) moduleMap.get("passParam")).toString();
        String moduleParam = (moduleMap == null)?"null":moduleMap.get("moduleParam").toString();
        String returnParam = (moduleMap == null)?"null":((JSONObject) moduleMap.get("returnParam")).toString();

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+","+stackTrace.getLineNumber();

        LOGGER.error(InnerOutputFormat(codePath, msg, passParam, returnParam, moduleParam));
    }
}
