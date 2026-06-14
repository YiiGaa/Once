package main.module._DataFilling;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import static java.util.regex.Matcher.quoteReplacement;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.StringNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;
import main.module.Link;

public class _DataFilling extends Link {
    private Object Filling(String fillingSet, JsonNode passParam, HttpServletRequest request, Boolean isSessionNullError){
        String[] pieceArr = fillingSet.split("\\+");
        String result = "";
        int pieceCount = pieceArr.length;
        for(String piece:pieceArr){
            //WHEN::Empty("") means '+'
            if("".equals(piece)){
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
                    long byteBuffer = ByteBuffer.wrap(shortuuid.getBytes()).getLong();
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
                        String sessionStr = request.getSession().getAttribute(setting).toString();
                        try{
                            JsonNode resultJson = TOOLS.JsonParse(sessionStr);
                            if(pieceCount == 1){
                                return resultJson;
                            }
                            if(resultJson.isString()){
                                result += resultJson.asString();
                            } else {
                                result += sessionStr;
                            }
                        } catch(Exception e){
                            result += sessionStr;
                        }
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
                    JsonNode value = passParam.get(setting);
                    if(null != value){
                        if(pieceCount == 1){
                            return value;
                        }
                        if(value.isString()){
                            result += value.asString();
                        } else {
                            result += value.toString();
                        }
                    } else if(TOOLS.JsonPathCheck(setting)){
                        String jsonPath = TOOLS.JsonPathChangeReal(setting);
                        try {
                            value = passParam.at(jsonPath);
                            if(((JsonNode)value).isMissingNode())
                                value = null;
                            if(null != value){
                                if(pieceCount == 1){
                                    return value;
                                }
                                if(value.isString()){
                                    result += value.asString();
                                } else {
                                    result += value.toString();
                                }
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
                    SimpleDateFormat formatter = new SimpleDateFormat("".equals(setting)?"yyyy-MM-dd HH:mm:ss":setting);
                    Date date = new Date(currentTime);
                    result += formatter.format(date);
                    break;
                default:
                    result += piece;
            }
        }
        return result;
    }

    private void TraverseJson_DeleteByJsonPath(JsonNode dealTarget, List<String> keyList){
        int index = 0;
        for (String key:keyList) {
            index++;

            if(index == keyList.size()){
                if(dealTarget.isObject()){
                    ((ObjectNode)dealTarget).remove(key);
                } else if(dealTarget.isArray() && key.matches("^(0|[1-9][0-9]*)$")){
                    int removeIndex = Integer.parseInt(key);
                    ((ArrayNode)dealTarget).remove(removeIndex);
                }
                break;
            }

            if(dealTarget.isObject()){
                dealTarget = ((ObjectNode)dealTarget).get(key);
            } else if(dealTarget.isArray() && key.matches("^(0|[1-9][0-9]*)$")){
                int removeIndex = Integer.parseInt(key);
                dealTarget = ((ArrayNode)dealTarget).get(removeIndex);
            } else {
                break;
            }
        }
    }

    private Object TraverseJson_CreateNewByJsonPath(JsonNode target, JsonNode value, List<String> keyList){
        if(keyList.size() ==0){
            return value;
        }
        
        //STEP::Get key
        String key = keyList.getFirst();
        keyList.removeFirst();

        //WHEN::Number index illegal
        if(key.matches("^(0|[1-9][0-9]*)$")){
            int index = Integer.parseInt(key);
            if(target.isArray() && index<((ArrayNode) target).size()){
                Object result = TraverseJson_CreateNewByJsonPath(((ArrayNode) target).get(index), value, keyList);
                ((ArrayNode) target).set(index, (JsonNode)result);
                return target;
            }
        }

        //WHEN::Other cases
        if(!(target.isObject()))
            target = TOOLS.JsonInitObject();
        if(!((ObjectNode)target).has(key))
            ((ObjectNode)target).put(key, "");
        Object result = TraverseJson_CreateNewByJsonPath(((ObjectNode) target).get(key), value, keyList);
        ((ObjectNode)target).set(key, (JsonNode)result);
        return target;
    }

    private Object TraverseJson(JsonNode fillingSet, JsonNode targetParam, boolean isPush, JsonNode passParam, HttpServletRequest request, Boolean isSessionNullError){
        //WHEN::Object
        if(fillingSet.isObject() && (!((ObjectNode) fillingSet).isEmpty())){
            //STEP-IN::Make the type consistent
            if(!(targetParam.isObject())){
                targetParam = TOOLS.JsonInitObject();
            }

            //STEP-IN::Traverse settings
            Set<Entry<String,JsonNode>> fields = ((ObjectNode)fillingSet).properties();
            for (Entry<String,JsonNode> entry : fields) {
                String key = entry.getKey();
                JsonNode fillingValue = entry.getValue();

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
                    if(fillingValue.isArray()) {
                        push = true;
                        key = key.substring("push##".length());
                    }
                }

                //STEP-IN-IN::Check key is in passParam
                Object targetValue = null;
                boolean isGetFromJsonPath = false;
                if(((ObjectNode)targetParam).has(key)){
                    targetValue = ((ObjectNode)targetParam).get(key);
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
                            if(tempValue instanceof JsonNode && ((JsonNode)tempValue).isString()){
                                tempValue = ((JsonNode)tempValue).asString();
                            } else if(!(tempValue instanceof String)){
                                tempValue = tempValue.toString();
                            }
                            key = key.replaceFirst(Pattern.quote("@"+matchPiece+"@"), quoteReplacement(tempValue.toString()));
                        }
                        if(((ObjectNode)targetParam).has(key)){
                            targetValue = ((ObjectNode)targetParam).get(key);
                        }
                    }
                }
                if(targetValue==null && TOOLS.JsonPathCheck(key)){
                    String jsonPath = TOOLS.JsonPathChangeReal(key);
                    try {
                        isGetFromJsonPath = true;
                        targetValue = ((ObjectNode) targetParam).at(jsonPath);
                        if(((JsonNode)targetValue).isMissingNode())
                            targetValue = null;
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
                    targetValue = TOOLS.JsonInit("");

                //WHEN-IN-IN::null means delete
                if(fillingValue.isNull()){
                    if(!isGetFromJsonPath){
                        ((ObjectNode) targetParam).remove(key);
                    }else{
                        List<String> keyList =  new ArrayList<>(Arrays.asList(key.split(">>")));
                        TraverseJson_DeleteByJsonPath(targetParam, keyList);
                    }
                    continue;
                }

                //STEP-IN-IN::Traverse call get real targetValue
                targetValue = TraverseJson(fillingValue, (JsonNode)targetValue, push, passParam, request, isSessionNullError);
                if(targetValue instanceof ERRORCODE){
                    return targetValue;
                }

                //STEP-IN-IN::Traverse call
                if(!isGetFromJsonPath){
                    ((ObjectNode) targetParam).set(key, (JsonNode)targetValue);
                } else {
                    List<String> keyList =  new ArrayList<>(Arrays.asList(key.split(">>")));
                    targetParam = (JsonNode)TraverseJson_CreateNewByJsonPath(targetParam, (JsonNode)targetValue, keyList);
                }
            }
            return targetParam;
        }

        if(fillingSet.isArray() && (!((ArrayNode) fillingSet).isEmpty())){
            //STEP-IN::Make the type consistent
            if(!(targetParam.isArray())){
                targetParam = TOOLS.JsonInitArray();
            }

            //STEP-IN::Empty means push list
            if(((ArrayNode) targetParam).isEmpty()){
                isPush = true;
            }

            //WHEN-IN::Push list
            if(isPush){
                for(JsonNode item:(ArrayNode)fillingSet){
                    Object targetValue;
                    targetValue = TraverseJson(item, TOOLS.JsonInit(""),false, passParam, request, isSessionNullError);
                    if(targetValue instanceof ERRORCODE){
                        return targetValue;
                    }
                    ((ArrayNode) targetParam).add((JsonNode) targetValue);
                }
                return targetParam;
            }

            //WHEN-IN::Set list
            for(int index =0;index<((ArrayNode) targetParam).size();index++){
                JsonNode item = ((ArrayNode)targetParam).get(index);
                Class<?> itemClass = item.getClass();
                for(JsonNode item_2:(ArrayNode)fillingSet){
                    if(itemClass==item_2.getClass()){
                        Object targetValue;
                        targetValue = TraverseJson(item_2, item,false, passParam, request, isSessionNullError);
                        if(targetValue instanceof ERRORCODE){
                            return targetValue;
                        }
                        ((ArrayNode) targetParam).set(index, (JsonNode)targetValue);
                        break;
                    }
                }
            }

            return targetParam;
        }

        //WHEN::String means filling value by expressions
        if(fillingSet.isString()){
            Object tempValue = Filling(fillingSet.asString(), passParam, request, isSessionNullError);
            if(tempValue instanceof ERRORCODE){
                return tempValue;
            }
            if(tempValue instanceof String){
                return TOOLS.JsonInit((String)tempValue);
            }
            return tempValue;
        }

        //WHEN::Other cases(eg boolean, int) will fill directly
        return fillingSet;
    }

    private ERRORCODE DoStart(ObjectNode moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        ObjectNode passParam = (ObjectNode) dataPool.get("passParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            //STEP::Get setting
            Boolean _isSessionNullError = TOOLS.ReadParam("_isSessionNullError", true, moduleParam, passParam);
            ObjectNode _setting = TOOLS.ReadParam("_setting", TOOLS.JsonInitObject(), moduleParam, passParam);
            LOGGER.DEBUG("Module-_DataFilling DoStart param, _isSessionNullError:" + _isSessionNullError);
            LOGGER.DEBUG("Module-_DataFilling DoStart param, _setting:" + _setting);

            //STEP::Traverse for filling data
            Object resultObject = TraverseJson(_setting, passParam, false, passParam, request, _isSessionNullError);
            if(resultObject instanceof ERRORCODE){
                return (ERRORCODE)resultObject;
            }

            //STEP::Deal result
            passParam = (ObjectNode) resultObject;
            dataPool.put("passParam", passParam);
        } catch (Exception e) {
            LOGGER.ERROR("Module-_DataFilling exception:"+TOOLS.GetExceptionInfo(e), moduleParam, dataPool);
            result = ERRORCODE.ERR_Module__DataFilling_Exception;
        }

        return result;
    }

    @Override
    public ERRORCODE Start(ObjectNode moduleParam, HashMap<String, Object> dataPool){
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