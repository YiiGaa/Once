package main.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.JsonNodeType;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.databind.node.JsonNodeFactory;

import jakarta.annotation.PostConstruct;

@Component
public class TOOLS {
    //TIPS::Init jackson handler
    @Autowired
    private ObjectMapper mapper;
    @PostConstruct
    public void init() {
        JSON = mapper;
    }
    public static ObjectMapper JSON;

    //TIPS::Init json node
    public static ObjectNode JsonInitObject(){
        return JSON.createObjectNode();
    }
    public static ArrayNode JsonInitArray(){
        return JSON.createArrayNode();
    }
    public static JsonNode JsonInit(String param){
        return new StringNode(param);
    }
    public static JsonNode JsonInit(Integer param){
        return new IntNode(param);
    }
    public static JsonNode JsonInit(Long param){
        return new LongNode(param);
    }
    public static JsonNode JsonInit(Boolean param){
        return JsonNodeFactory.instance.booleanNode(param);
    }

    //TIPS::Convert json string
    public static JsonNode JsonParse(String text) throws JacksonException{
        return JSON.readTree(text);
    }

    //TIPS::Get json string json string
    public static String JsonToString(JsonNode param){
        if(param == null)
            return "null";
        else if(param.isMissingNode())
            return "null";
        else if(param.isString())
            return param.asString();
        return param.toString();
    }

    //TIPS::Cache json for moudleParam
    private static final ConcurrentHashMap<String, JsonNode> CACHE = new ConcurrentHashMap<>();
    public static JsonNode JsonCache(String jsonStr) {
        JsonNode original = CACHE.computeIfAbsent(jsonStr, k -> {
            try {
                return JSON.readTree(k);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return original.deepCopy();
    }

    //TIPS::Get param according the key
    //>>key::The key of the param
    //>>defaultValue::The default value, the type of defaultValue also limit the type of target value
    //              - It can be null, means not limiting the type
    //              - It can be ObjectNode(createObjectNode()), means limiting '{}' type
    //              - It can be ArrayNode(createArrayNode()), means limiting '[]' type
    //              - It can be string type, means limiting string type
    //              - It can be int type, means limiting int type
    //              - It can be long type, like '1L', means limiting long type
    //              - It can be bool type, means limiting bool type
    //              - It can be double type, means limiting double type
    //>>moduleParam::Get the value corresponding to the 'key' in this object
    //              - If the acquisition fails, the 'defaultValue' is returned;
    //              - If the string starting with 'get##' is obtained, it will continue to search for value in 'passParam' as a 'new key';
    //              - If the same value as the 'defaultValue' type is obtained, it will be used as the return value;
    //>>passParam::Get the value corresponding to the 'new key'(get from moduleParam) in this object
    //              - The 'new key' can be set as json path(use '>>' to locate), such as 'get##key_1>>2>>key_3';
    //              - If the acquisition fails, the 'defaultValue' is returned;
    //              - If the same value as the 'defaultValue' type is obtained, it will be used as the return value;
    //>>return::Value
    public static String ReadParam(String key, String defaultValue, JsonNode moduleParam, JsonNode passParam){
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.getNodeType() == JsonNodeType.STRING){
            String value = setting.asString();
            //WHEN::Get passParam by jsonPath (String start with get##)
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(returnValue.getNodeType() == JsonNodeType.STRING){
                    return returnValue.asString();
                }
                return defaultValue;
            } 
            //WHEN::Get from moduleParam
            else {
                return setting.asString();
            }
        }
        return defaultValue;
    }
    public static Double ReadParam(String key, Double defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.getNodeType() == JsonNodeType.STRING){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(returnValue.isNumber()){
                    if(returnValue.isBigDecimal()){
                        return returnValue.decimalValue().doubleValue();
                    } else {
                        return returnValue.asDouble();
                    }
                }
                if(returnValue.isString() && (returnValue.asString()).matches("-?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")){
                    try {
                        return Double.valueOf(returnValue.asString());
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(setting.isNumber()){
            if(setting.isBigDecimal()){
                return setting.decimalValue().doubleValue();  // BigDecimal → Double
            } else {
                return setting.asDouble();
            }
        }

        return defaultValue;
    }
    public static Integer ReadParam(String key, Integer defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.getNodeType() == JsonNodeType.STRING){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(returnValue.isInt()){
                    return returnValue.asInt();
                }
                if(returnValue.isBoolean()){
                    return returnValue.asBoolean() ? 1 : 0;
                }
                if(returnValue.isString() && (returnValue.asString()).matches("^-?\\d+$")){
                    try {
                        return Integer.valueOf(returnValue.asString());
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(setting.isBoolean()){
            return setting.asBoolean() ? 1 : 0;
        }
        if(setting.isInt()){
            return setting.asInt();
        }

        return defaultValue;
    }
    public static Long ReadParam(String key, Long defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.getNodeType() == JsonNodeType.STRING){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(returnValue.isInt()){
                    return returnValue.asLong();
                }
                if(returnValue.isLong()){
                    return returnValue.asLong();
                }
                if(returnValue.isBoolean()){
                    return returnValue.asBoolean() ? 1L : 0L;
                }
                if(returnValue.isString() && (returnValue.asString()).matches("^-?\\d+$")){
                    try {
                        return Long.valueOf(returnValue.asString());
                    } catch (Exception e){}
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(setting.isBoolean()){
            return setting.asBoolean() ? 1L : 0L;
        }
        if(setting.isInt()){
            return setting.asLong();
        }        
        if(setting.isLong()){
            return setting.asLong();
        }

        return defaultValue;
    }
    public static Boolean ReadParam(String key, Boolean defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.isString()){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(returnValue.isBoolean()){
                    return returnValue.asBoolean();
                }
                if(returnValue.isInt()){
                    return returnValue.asInt() != 0;
                }
                if(returnValue.isString() && (returnValue.asString()).equals("true")){
                    return true;
                }
                if(returnValue.isString() && (returnValue.asString()).equals("false")){
                    return false;
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(setting.isBoolean()){
            return setting.asBoolean();
        }
        if(setting.isInt()){
            return setting.asInt() != 0;
        }

        return defaultValue;
    }
    public static ObjectNode ReadParam(String key, ObjectNode defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.isString()){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(!returnValue.isMissingNode() && (defaultValue==null || defaultValue.getNodeType() == returnValue.getNodeType())){
                    return (ObjectNode)returnValue;
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(defaultValue==null || defaultValue.getNodeType() == setting.getNodeType()){
            return (ObjectNode)setting;
        }

        return defaultValue;
    }
    public static ArrayNode ReadParam(String key, ArrayNode defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.isString()){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(!returnValue.isMissingNode() && (defaultValue==null || defaultValue.getNodeType() == returnValue.getNodeType())){
                    return (ArrayNode)returnValue;
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(defaultValue==null || defaultValue.getNodeType() == setting.getNodeType()){
            return (ArrayNode)setting;
        }

        return defaultValue;
    }
    public static JsonNode ReadParam(String key, JsonNode defaultValue, JsonNode moduleParam, JsonNode passParam){
        //WHEN::Get passParam by jsonPath (String start with get##)
        JsonNode setting = moduleParam.get(key);
        if(setting == null)
            return defaultValue;
        if(setting.isString()){
            String value = setting.asString();
            if(value.startsWith("get##")){
                String keyPath = JsonPathChangeReal(value.substring("get##".length()));
                JsonNode returnValue = passParam.at(keyPath);
                if(!returnValue.isMissingNode() && (defaultValue==null || defaultValue.getNodeType() == returnValue.getNodeType())){
                    return returnValue;
                }
                return defaultValue;
            }
        }

        //WHEN::Get from moduleParam
        if(defaultValue==null || defaultValue.getNodeType() == setting.getNodeType()){
            return setting;
        }

        return defaultValue;
    }

    //TIPS::Check whether the key is json path (contains '>>')
    public static boolean JsonPathCheck(String key){
        return key.contains(">>");
    }
    //TIPS::Convert string to json path
    public static String JsonPathChangeReal(String key) {
        StringBuilder jsonPtr = new StringBuilder();
        String[] pieceArr = key.split(">>");
        for(String piece : pieceArr) {
            if(piece.matches("^(0|[1-9][0-9]*)$")) {
                jsonPtr.append("/").append(piece);
            } else {
                String encoded = piece.replace("~", "~0").replace("/", "~1");
                jsonPtr.append("/").append(encoded);
            }
        }
        return jsonPtr.toString();
    }

    //TIPS::Get exception info from exception object
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

    //TIPS::Get param according the key in passParam
    public static String ReadPassParam(String key, HashMap<String, Object> dataPool){
        String keyPath = JsonPathChangeReal(key);
        JsonNode passParam = (JsonNode) dataPool.get("passParam");
        JsonNode returnValue = passParam.at(keyPath);
        return TOOLS.JsonToString(returnValue);
    }
}
