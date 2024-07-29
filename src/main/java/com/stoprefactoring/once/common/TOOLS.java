package com.stoprefactoring.once.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TOOLS {
    public static String ReadParam(String key, String defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof String){
                    return returnValue.toString();
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof String){
            return moduleParam.getString(key);
        }

        return defaultValue;
    }
    public static Double ReadParam(String key, Double defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof Double){
                    return (Double) returnValue;
                }
                if(returnValue instanceof BigDecimal){
                    return ((BigDecimal) returnValue).doubleValue();
                }
                if(returnValue instanceof String && ((String)returnValue).matches("-?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")){
                    try {
                        return Double.parseDouble((String) returnValue);
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof Double){
            return moduleParam.getDouble(key);
        }
        if(moduleParam.get(key) instanceof BigDecimal){
            return moduleParam.getBigDecimal(key).doubleValue();
        }

        return defaultValue;
    }
    public static Integer ReadParam(String key, Integer defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof Boolean){
                    return (Boolean) returnValue ? 1 : 0;
                }
                if(returnValue instanceof Integer){
                    return (Integer) returnValue;
                }
                if(returnValue instanceof String && ((String)returnValue).matches("^-?\\d+$")){
                    try {
                        return Integer.parseInt((String) returnValue);
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof Boolean){
            return (Boolean) moduleParam.get(key) ? 1 : 0;
        }
        if(moduleParam.get(key) instanceof Integer){
            return (Integer) moduleParam.get(key);
        }

        return defaultValue;
    }
    public static Long ReadParam(String key, Long defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof Integer){
                    return ((Integer) returnValue).longValue();
                }
                if(returnValue instanceof Long){
                    return (Long) returnValue;
                }
                if(returnValue instanceof Boolean){
                    return (Boolean) returnValue ? 1L : 0L;
                }
                if(returnValue instanceof String && ((String)returnValue).matches("^-?\\d+$")){
                    try {
                        return Long.parseLong((String) returnValue);
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof Long){
            return (Long) moduleParam.get(key);
        }
        if(moduleParam.get(key) instanceof Integer){
            return moduleParam.getInteger(key).longValue();
        }
        if(moduleParam.get(key) instanceof Boolean){
            return (Boolean) moduleParam.get(key) ? 1L : 0L;
        }

        return defaultValue;
    }
    public static Boolean ReadParam(String key, Boolean defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof Boolean){
                    return (Boolean) returnValue;
                }
                if(returnValue instanceof Integer){
                    return ((Integer) returnValue) != 0;
                }
                if(returnValue instanceof String && (((String)returnValue).equals("true")||((String)returnValue).equals("false"))){
                    try {
                        return Boolean.parseBoolean((String) returnValue);
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof Boolean){
            return ((Boolean) moduleParam.get(key));
        }
        if(moduleParam.get(key) instanceof Integer){
            return ((Integer) moduleParam.get(key)) != 0;
        }

        return defaultValue;
    }
    public static JSONObject ReadParam(String key, JSONObject defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof JSONObject){
                    return (JSONObject)returnValue;
                }
                if(returnValue instanceof String){
                    try {
                        return JSONObject.parseObject((String) returnValue);
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof JSONObject){
            return moduleParam.getJSONObject(key);
        }

        return defaultValue;
    }
    public static JSONArray ReadParam(String key, JSONArray defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue instanceof JSONArray){
                    return (JSONArray)returnValue;
                }
                if(returnValue instanceof String){
                    try {
                        return JSONArray.parse((String) returnValue);
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) instanceof JSONArray){
            return moduleParam.getJSONArray(key);
        }

        return defaultValue;
    }

    public static Object ReadParam(String key, Object defaultValue, JSONObject moduleParam, JSONObject passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        if(moduleParam.get(key) instanceof String){
            String value = moduleParam.getString(key);
            if(value.startsWith("get##")){
                String keyPass = JsonPathChangeReal(value.substring("get##".length()));
                Object returnValue = passParam.getByPath(keyPass);
                if(returnValue!=null) {
                    return returnValue;
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(moduleParam.get(key) !=null){
            return moduleParam.get(key);
        }

        return defaultValue;
    }

    public static boolean JsonPathCheck(String key){
        if(key.contains(">>")){
            return true;
        }
        return false;
    }
    public static String JsonPathChangeReal(String key){
        String jsonPath = "";
        String[] pieceArr = key.split(">>");
        for(String piece : pieceArr){
            if(piece.matches("^(0|[1-9][0-9]*)$")){
                jsonPath += "["+piece+"]";
            }else{
                jsonPath += "['"+piece+"']";
            }
        }
        return jsonPath;
    }

    public static String GetExceptionInfo(Exception e){
        try {
            StringWriter strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            e.printStackTrace(printWriter);
            return "\n\r" + strWriter.toString() + "\n\r";
        } catch (Exception ee) {
            return "\n\rbad getErrorInfoFromException!\n\r";
        }
    }
}
