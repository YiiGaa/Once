package main.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;

public class LOGGER {
    public static ThreadLocal<HashMap<String, String>> loggerHeaders = new ThreadLocal<>();
    public static ThreadLocal<ArrayList<Object[]>> loggerPosition = new ThreadLocal<>();
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
        headers.put("remoteIp", (ipFromNginx==null||"".equals(ipFromNginx)||" ".equals(ipFromNginx))?request.getRemoteAddr():ipFromNginx);
        headers.put("callPath", request.getRequestURI()+"."+request.getMethod());
        headers.put("position", position);
        loggerHeaders.set(headers);

        //STEP::Mark position info
        ArrayList<Object[]> positionsArrays = new ArrayList<>();
        Object[] positionMark = new Object[]{position, 0};
        positionsArrays.add(positionMark);
        loggerPosition.set(positionsArrays);

        //STEP::Print init log
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        msg += ",callPath:"+headers.get("callPath")+
                ",userId:"+headers.get("userId")+
                ",remoteIp:"+headers.get("remoteIp");
        LOGGER.info(InnerOutputFormat(msg, codePath, "{-}", ((JsonNode)dataPool.get("passParam")).toString()));
    }

    public static void END(String msg, JsonNode passParam) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.info(InnerOutputFormat(msg, codePath, "{-}", passParam.toString()));
        loggerHeaders.remove();
        loggerPosition.remove();
    }

    public static void ChangePosition(String position) {
        HashMap<String, String> headers = loggerHeaders.get();
        ArrayList<Object[]> positionsArrays = loggerPosition.get();
        if(position == null && !positionsArrays.isEmpty())
            positionsArrays.removeLast();
        else if(position != null){
            Integer count=0;
            for(Object[] item:positionsArrays){
                if(((String)item[0]).equals(position)){
                    count = (Integer)item[1]+1;
                    break;
                }
            }
            Object[] positionMark = new Object[]{position, count};
            positionsArrays.add(positionMark);
        }
        position = "";
        if(!positionsArrays.isEmpty()){
            position = (String)(positionsArrays.getLast()[0]);
            Integer count = (Integer)(positionsArrays.getLast()[1]);
            if(count!=0)
                position = position + "("+count.toString()+")";
        }
        headers.put("position", position);
        loggerHeaders.set(headers);
    }

    public static String GetPosition() {
        ArrayList<Object[]> positionsArrays = loggerPosition.get();
        String position = "";
        if(!positionsArrays.isEmpty()){
            position = (String)(positionsArrays.getLast()[0]);
            Integer count = (Integer)(positionsArrays.getLast()[1]);
            if(count!=0)
                position = position + "("+count.toString()+")";
        }
        return position;
    }

    public static String GetCallPosition() {
        ArrayList<Object[]> positionsArrays = loggerPosition.get();
        int index = Math.max(positionsArrays.size() - 2, 0);
        String position = "";
        if(!positionsArrays.isEmpty()){
            position = (String)(positionsArrays.get(index)[0]);
            Integer count = (Integer)(positionsArrays.get(index)[1]);
            if(count!=0)
                position = position + "("+count.toString()+")";
        }
        return position;
    }

    private static String InnerOutputFormat(String msg, String codePath, String moduleParam, String passParam) {
        HashMap<String, String> headers = loggerHeaders.get();

        return  "["+headers.get("requestId")+ "]"+
                "["+headers.get("position")+"]"+
                msg+
                ","+codePath+
                ",moduleParam:"+moduleParam+
                ",passParam:"+passParam;
    }

    public static void DEBUG(String msg) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, null, null);
    }
    public static void DEBUG(String msg, JsonNode moduleParam, JsonNode passParam) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, moduleParam, passParam);
    }
    public static void DEBUG(String msg, JsonNode moduleParam, HashMap<String, Object> dataPool) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, moduleParam, (JsonNode)dataPool.get("passParam"));
    }
    public static void DEBUG(String msg, HashMap<String, Object> dataPool) {
        if(LOGGER.isDebugEnabled())
            InnerDEBUG(msg, null, (JsonNode)dataPool.get("passParam"));
    }
    private static void InnerDEBUG(String msg, JsonNode moduleParam, JsonNode passParam) {
        String passParamStr = passParam!=null?passParam.toString():"{-}";
        String moduleParamStr = moduleParam!=null?moduleParam.toString():"{-}";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.debug(InnerOutputFormat(msg, codePath, moduleParamStr, passParamStr));
    }

    public static void ERROR(String msg) {
        InnerERROR(msg, null, null);
    }
    public static void ERROR(String msg, JsonNode moduleParam, JsonNode passParam) {
        InnerERROR(msg, moduleParam, passParam);
    }
    public static void ERROR(String msg, JsonNode moduleParam, HashMap<String, Object> dataPool) {
        InnerERROR(msg, moduleParam, (JsonNode)dataPool.get("passParam"));
    }
    public static void ERROR(String msg, HashMap<String, Object> dataPool) {
        InnerERROR(msg, null, (JsonNode)dataPool.get("passParam"));
    }
    private static void InnerERROR(String msg, JsonNode moduleParam, JsonNode passParam) {
        String passParamStr = passParam!=null?passParam.toString():"{-}";
        String moduleParamStr = moduleParam!=null?moduleParam.toString():"{-}";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.info(InnerOutputFormat(msg, codePath, moduleParamStr, passParamStr));
    }

    public static void INFO(String msg) {
        InnerINFO(msg, null, null);
    }
    public static void INFO(String msg, JsonNode moduleParam, JsonNode passParam) {
        InnerINFO(msg, moduleParam, passParam);
    }
    public static void INFO(String msg, JsonNode moduleParam, HashMap<String, Object> dataPool) {
        InnerINFO(msg, moduleParam, (JsonNode)dataPool.get("passParam"));
    }
    public static void INFO(String msg, HashMap<String, Object> dataPool) {
        InnerINFO(msg, null, (JsonNode)dataPool.get("passParam"));
    }
    private static void InnerINFO(String msg, JsonNode moduleParam, JsonNode passParam) {
        String passParamStr = passParam!=null?passParam.toString():"{-}";
        String moduleParamStr = moduleParam!=null?moduleParam.toString():"{-}";

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[3];
        String codePath = stackTrace.getClassName()+".java,"+stackTrace.getMethodName()+"(),"+stackTrace.getLineNumber();
        LOGGER.info(InnerOutputFormat(msg, codePath, moduleParamStr, passParamStr));
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