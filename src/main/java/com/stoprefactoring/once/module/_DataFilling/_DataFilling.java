package com.stoprefactoring.once.module._DataFilling;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.common.TOOLS;
import com.stoprefactoring.once.module.Link;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;

public class _DataFilling extends Link {
    private Object Filling(String fillingSet, JSONObject passParam, HttpServletRequest request, Boolean isSessionNullError){
        String[] pieceArr = fillingSet.split("\\+");
        String result = "";
        int pieceCount = pieceArr.length;
        for(String piece:pieceArr){
            //WHEN::Empty("") means '+'
            if(piece == ""){
                result+="+";
            }

            //STEP::Split function & setting
            String[] tempArr = piece.split("##", 2);
            String function = tempArr[0];
            String setting = tempArr.length>1?tempArr[1]:"";

            //STEP::Excute function filling
            switch (function){
                case "uuid":
                    String uuid = UUID.randomUUID().toString().replace("-", "");
                    result += uuid;
                    break;
                case "uuid short":
                    String shortuuid = UUID.randomUUID().toString().replace("-", "");
                    long byteBuffer = ByteBuffer.wrap(shortuuid.toString().getBytes()).getLong();
                    result += Long.toString(byteBuffer, Character.MAX_RADIX);
                    break;
                case "random id":
                    String randomID = pieceArr.length>1? RandomStringUtils.randomAlphanumeric(Integer.parseInt(setting)): RandomStringUtils.randomAlphanumeric(8);
                    result += randomID;
                    break;
                case "session":
                    if(request.getSession().getAttribute(setting) == null) {
                        result += "null";
                        LOGGER.DEBUG("Module-_DataFilling Filling get session fail:"+setting);
                        if(isSessionNullError){
                            return ERRORCODE.ERR_Module__DataFilling_Session_Fail;
                        }
                    }else {
                        result = request.getSession().getAttribute(setting).toString();
                    }
                    break;
                case "header":
                    if(request.getHeader(setting) == null) {
                        result += "null";
                        LOGGER.DEBUG("Module-_DataFilling Filling get header fail:"+setting);
                    }else {
                        result = request.getHeader(setting).toString();
                    }
                    break;
                case "get":
                    Object value = passParam.get(setting);
                    if(null != value){
                        if(pieceCount == 1){
                            return value;
                        }
                        result += value.toString();
                    } else if(TOOLS.JsonPathCheck(setting)){
                        String jsonPath = TOOLS.JsonPathChangeReal(setting);
                        try {
                            value = passParam.getByPath(jsonPath);
                            if(null != value){
                                if(pieceCount == 1){
                                    return value;
                                }
                                result += value.toString();
                            }
                        } catch (Exception e){
                            LOGGER.DEBUG("Module-_DataFilling Filling json path illegal:"+setting+",jsonPath:"+jsonPath);
                            return ERRORCODE.ERR_Module__DataFilling_JsonPath_Illegal;
                        }
                    }
                    if(null == value){
                        result += "null";
                        LOGGER.DEBUG("Module-_DataFilling Filling get passParam fail:"+setting);
                    }
                    break;
                case "time":
                    long currentTime = System.currentTimeMillis();
                    SimpleDateFormat formatter = new SimpleDateFormat(setting==""?"yyyy-MM-dd HH:mm:ss":setting);
                    Date date = new Date(currentTime);
                    result += formatter.format(date);
                    break;
                default:
                    result += piece;
            }
        }
        return result;
    }

    private void TraverseJson_DeleteByJsonPath(Object target, List<String> keyList){
        Object dealTarget = target;
        int index = 0;
        for (String key:keyList) {
            index++;

            if(index == keyList.size()){
                if(dealTarget instanceof JSONObject){
                    ((JSONObject)dealTarget).remove(key);
                } else if(dealTarget instanceof JSONArray && key.matches("^(0|[1-9][0-9]*)$")){
                    int removeIndex = Integer.parseInt(key);
                    ((JSONArray)dealTarget).remove(removeIndex);
                }
                break;
            }

            if(dealTarget instanceof JSONObject){
                dealTarget = ((JSONObject)dealTarget).get(key);
            } else if(dealTarget instanceof JSONArray && key.matches("^(0|[1-9][0-9]*)$")){
                int removeIndex = Integer.parseInt(key);
                dealTarget = ((JSONArray)dealTarget).get(removeIndex);
            } else {
                break;
            }
        }
    }

    private Object TraverseJson_CreateNewByJsonPath(Object target, Object value, List<String> keyList){
        if(keyList.size() ==0){
            return value;
        }
        
        //STEP::Get key
        String key = keyList.getFirst();
        keyList.removeFirst();

        //WHEN::Number index illegal
        if(key.matches("^(0|[1-9][0-9]*)$")){
            int index = Integer.parseInt(key);
            if(target instanceof JSONArray && index<((JSONArray) target).size()){
                Object result = TraverseJson_CreateNewByJsonPath(((JSONArray) target).get(index), value, keyList);
                ((JSONArray) target).remove(index);
                ((JSONArray) target).add(index, result);
                return target;
            }
        }

        //WHEN::Other cases
        if(!(target instanceof JSONObject))
            target = new JSONObject();
        if(!((JSONObject)target).containsKey(key))
            ((JSONObject)target).put(key, "");
        Object result = TraverseJson_CreateNewByJsonPath(((JSONObject) target).get(key), value, keyList);
        ((JSONObject)target).put(key, result);
        return target;
    }

    private Object TraverseJson(Object fillingSet, Object targetParam, boolean isPush, JSONObject passParam, HttpServletRequest request, Boolean isSessionNullError){
        //WHEN::JSONObject
        if(fillingSet instanceof JSONObject && (!((JSONObject) fillingSet).isEmpty())){
            //STEP-IN::Make the type consistent
            if(!(targetParam instanceof JSONObject)){
                targetParam = new JSONObject();
            }

            //STEP-IN::Traverse settings
            for (String key : ((JSONObject)fillingSet).keySet()) {
                Object fillingValue = ((JSONObject)fillingSet).get(key);

                //STEP-IN-IN::Get is necessary
                boolean isNecessary = true;
                if(key.startsWith("opt##")){
                    isNecessary = false;
                    key = key.substring("opt##".length());
                }else if(key.startsWith("nec##")){
                    isNecessary = true;
                    key = key.substring("nec##".length());
                }

                //STEP-IN-IN::Get is push
                boolean push = false;
                if(key.startsWith("push##")){
                    if(fillingValue instanceof JSONArray) {
                        push = true;
                        key = key.substring("push##".length());
                    }
                }

                //STEP-IN-IN::Check key is in passParam
                Object targetValue = null;
                boolean isGetFromJsonPath = false;
                if(((JSONObject)targetParam).containsKey(key)){
                    targetValue = ((JSONObject)targetParam).get(key);
                }
                if(targetValue==null){
                    Pattern pattern = Pattern.compile("@(.+?)@");
                    Matcher matcher = pattern.matcher(key);
                    List<String> matchedStrings = new ArrayList<>();
                    while (matcher.find()) {
                        matchedStrings.add(matcher.group(1));
                    }
                    if(matchedStrings.size()>0){
                        for (String matchPiece : matchedStrings) {
                            Object tempValue = Filling(matchPiece, passParam, request, isSessionNullError);
                            if(tempValue instanceof ERRORCODE){
                                return tempValue;
                            }
                            key = key.replaceFirst(Pattern.quote("@"+matchPiece+"@"), quoteReplacement(tempValue.toString()));
                        }
                        if(((JSONObject)targetParam).containsKey(key)){
                            targetValue = ((JSONObject)targetParam).get(key);
                        }
                    }
                }
                if(targetValue==null && TOOLS.JsonPathCheck(key)){
                    String jsonPath = TOOLS.JsonPathChangeReal(key);
                    try {
                        isGetFromJsonPath = true;
                        targetValue = ((JSONObject) targetParam).getByPath(jsonPath);
                    } catch (Exception e){
                        LOGGER.DEBUG("Module-_DataFilling json path illegal:"+key+",jsonPath:"+jsonPath);
                        targetValue = null;
                    }
                }
                if(targetValue!=null && !isNecessary) {
                    continue;
                }

                //STEP-IN-IN::Init targetValue
                if(targetValue==null)
                    targetValue = "";

                //WHEN-IN-IN::null means delete
                if(fillingValue==null){
                    if(!isGetFromJsonPath){
                        ((JSONObject) targetParam).remove(key);
                    }else{
                        List<String> keyList =  new ArrayList<>(Arrays.asList(key.split(">>")));
                        TraverseJson_DeleteByJsonPath(targetParam, keyList);
                    }
                    continue;
                }

                //STEP-IN-IN::Traverse call get real targetValue
                targetValue = TraverseJson(fillingValue, targetValue, push, passParam, request, isSessionNullError);
                if(targetValue instanceof ERRORCODE){
                    return targetValue;
                }

                //STEP-IN-IN::Traverse call
                if(!isGetFromJsonPath){
                    ((JSONObject) targetParam).put(key, targetValue);
                } else {
                    List<String> keyList =  new ArrayList<>(Arrays.asList(key.split(">>")));
                    targetParam = TraverseJson_CreateNewByJsonPath(targetParam, targetValue, keyList);
                }
            }
            return targetParam;
        }

        if(fillingSet instanceof JSONArray && (!((JSONArray) fillingSet).isEmpty())){
            //STEP-IN::Make the type consistent
            if(!(targetParam instanceof JSONArray)){
                targetParam = new JSONArray();
            }

            //STEP-IN::Empty means push list
            if(((JSONArray) targetParam).isEmpty()){
                isPush = true;
            }

            //WHEN-IN::Push list
            if(isPush){
                for(Object item:(JSONArray)fillingSet){
                    Object targetValue = "";
                    targetValue = TraverseJson(item, targetValue,false, passParam, request, isSessionNullError);
                    if(targetValue instanceof ERRORCODE){
                        return targetValue;
                    }
                    ((JSONArray) targetParam).add(targetValue);
                }
                return targetParam;
            }

            //WHEN-IN::Set list
            for(int index =0;index<((JSONArray) targetParam).size();index++){
                Object item = ((JSONArray)targetParam).get(index);
                Class<?> itemClass = item.getClass();
                for(Object item_2:(JSONArray)fillingSet){
                    if(itemClass==item_2.getClass()){
                        Object targetValue = item;
                        targetValue = TraverseJson(item_2, targetValue,false, passParam, request, isSessionNullError);
                        if(targetValue instanceof ERRORCODE){
                            return targetValue;
                        }
                        ((JSONArray) targetParam).remove(index);
                        ((JSONArray) targetParam).add(index, targetValue);
                        break;
                    }
                }
            }

            return targetParam;
        }

        //WHEN::String means filling value by expressions
        if(fillingSet instanceof String){
            Object tempValue = Filling((String)fillingSet, passParam, request, isSessionNullError);
            if(tempValue instanceof ERRORCODE){
                return tempValue;
            }
            return tempValue;
        }

        //WHEN::Other cases(eg boolean, int) will fill directly
        return fillingSet;
    }

    private ERRORCODE DoStart(JSONObject moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        JSONObject passParam = (JSONObject) dataPool.get("passParam");
        JSONObject returnParam = (JSONObject) dataPool.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            //STEP::Get setting
            Boolean _isSetReturnParam = TOOLS.ReadParam("_isSetReturnParam", false, moduleParam, passParam);
            Boolean _isSessionNullError = TOOLS.ReadParam("_isSessionNullError", true, moduleParam, passParam);
            JSONObject _setting = TOOLS.ReadParam("_setting", new JSONObject(), moduleParam, passParam);
            LOGGER.DEBUG("Module-_DataFilling DoStart param, _isSetReturnParam:" + _isSetReturnParam);
            LOGGER.DEBUG("Module-_DataFilling DoStart param, _isSessionNullError:" + _isSessionNullError);
            LOGGER.DEBUG("Module-_DataFilling DoStart param, _setting:" + _setting.toString());

            //STEP::Traverse for filling data
            JSONObject targetJson = _isSetReturnParam? returnParam.clone(): passParam.clone();
            Object resultObject = TraverseJson(_setting, targetJson, false, passParam, request, _isSessionNullError);
            if(resultObject instanceof ERRORCODE){
                return (ERRORCODE)resultObject;
            }

            //STEP::Deal result
            if(_isSetReturnParam){
                returnParam = (JSONObject) resultObject;
                dataPool.put("returnParam", returnParam);
            } else {
                passParam = (JSONObject) resultObject;
                dataPool.put("passParam", passParam);
            }

        } catch (Exception e) {
            LOGGER.ERROR("Module-_DataFilling exception:"+TOOLS.GetExceptionInfo(e), moduleParam, dataPool);
            result = ERRORCODE.ERR_Module__DataFilling_Exception;
        }

        return result;
    }

    @Override
    public ERRORCODE Start(JSONObject moduleParam, HashMap<String, Object> dataPool){
        LOGGER.DEBUG("Module-_DataFilling start", moduleParam, dataPool);
        ERRORCODE result = ERRORCODE.ERR_OK;
        result = DoStart(moduleParam, dataPool, result);
        LOGGER.DEBUG("Module-_DataFilling end", moduleParam, dataPool);
        return result;
    }

    @Override
    public ERRORCODE End(HashMap<String, Object> dataPool, ERRORCODE result){
        LOGGER.DEBUG("Module-_DataFilling End start", dataPool);
        try{
            //Some work of cleaning up the module（When Controller end or Service end）
            //You need to set "Module.moduleNeedCallEnd.put("_DataFilling",true)" in _DataFillingConfig.Init() to take effect
        } catch (Exception e){LOGGER.ERROR("Module-_DataFilling End for clean exception:"+ TOOLS.GetExceptionInfo(e), dataPool);}
        LOGGER.DEBUG("Module-_DataFilling End end", dataPool);
        return result;
    }
}