package main.module._DataCheck;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.jackson.databind.node.*;
import tools.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;
import main.module.Link;

public class _DataCheck extends Link {
    private Boolean TraverseJson_CheckType(JsonNode require, JsonNode passParam){
        //WHEN::Type need match
        if(!require.isString() && require.getNodeType() == passParam.getNodeType()){
            return true;
        }

        //WHEN::Type String may means 'Allow Anything'/'Specify Type'
        if(require.isString()){
            String requireValue = require.asString();

            //WHEN-IN::Allow anything
            if(requireValue.equals("")){
                return true;
            }

            //WHEN-IN::Specify type
            if(requireValue.startsWith("str##")){
                if(passParam.isString())
                    return true;
            }else if(requireValue.startsWith("int##")){
                if(passParam.isNumber() && !passParam.isDouble())
                    return true;
                if(passParam.isString() && (passParam.asString()).matches("^-?\\d+$"))
                    return true;
            }else if(requireValue.startsWith("bool##")){
                if(passParam.isBoolean())
                    return true;
                if(passParam.isString()){
                    String value = passParam.asString();
                    if(value.equals("true")||value.equals("false"))
                        return true;
                }
            }else if(requireValue.startsWith("long##")){
                if(passParam.isNumber() && !passParam.isDouble())
                    return true;
                if(passParam.isString() && (passParam.asString()).matches("^-?\\d+$"))
                    return true;
            }else if(requireValue.startsWith("double##")){
                if(passParam.isDouble())
                    return true;
                if(passParam.isString() && (passParam.asString()).matches("-?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"))
                    return true;
            }else if(requireValue.contains("reg##")){
                if(passParam.isString() || passParam.isNumber() || passParam.isBoolean())
                    return true;
            }else if(passParam.isString()){
                return true;
            }

        }
        return false;
    }

    private Boolean TraverseJson_CheckValue(JsonNode require, JsonNode passParam){
        String requireValue = require.asString();

        //STEP::Remove type setting
        if(requireValue.startsWith("str##")){
            requireValue = requireValue.substring("str##".length());
        } else if(requireValue.startsWith("int##")){
            requireValue = requireValue.substring("int##".length());
        } else if(requireValue.startsWith("double##")){
            requireValue = requireValue.substring("double##".length());
        } else if(requireValue.startsWith("long##")){
            requireValue = requireValue.substring("long##".length());
        } else if(requireValue.startsWith("bool##")){
            requireValue = requireValue.substring("bool##".length());
        }

        //WHEN::Allow anything
        if(requireValue.equals("")){
            return true;
        }

        //STEP::Get judge value
        String judgeValue = "";
        if(passParam.isString()){
            judgeValue = passParam.asString();
        }
        if(passParam.isNumber()){
            judgeValue = passParam.asString();
        }
        if(passParam.isLong()){
            judgeValue = passParam.asString();
        }
        if(passParam.isBoolean()){
            judgeValue = passParam.asBoolean()?"true":"false";
        }
        if(passParam.isBigDecimal()){
            judgeValue = passParam.asString();
        }
        if(passParam.isDouble()){
            judgeValue = passParam.asString();
        }

        //STEP::Get whether regex
        boolean isRegex = false;
        if(requireValue.startsWith("reg##")) {
            requireValue = requireValue.substring("reg##".length());
            isRegex = true;
        }

        //STEP::Get whether to reverse the result
        boolean isNot = false;
        if(requireValue.startsWith("not##")) {
            requireValue = requireValue.substring("not##".length());
            isNot = true;
        }

        //STEP::Check value
        boolean result = false;
        String[] rangeArr = requireValue.split("/");
        for(String range:rangeArr){
            //WHEN-IN::Regex mode
            if(isRegex){
                Pattern pattern = Pattern.compile(range);
                Matcher matcher = pattern.matcher(judgeValue);
                if(matcher.matches()){
                    result = true;
                    break;
                }
            }
            //WHEN-IN::String mode
            else if(judgeValue.equals(range)){
                result = true;
                break;
            }
        }

        return isNot?!result:result;
    }

    private void TraverseJson_CreateNew_JsonPath(JsonNode newParam, JsonNode target, String key){
        String[] pathArr = key.split(">>");
        ObjectNode newParamObject = (ObjectNode)newParam;
        Integer index = 0;
        for (String path:pathArr) {
            index++;
            if(index == pathArr.length){
                newParamObject.set(path, target);
            } else {
                if(!(newParamObject.has(path)) || 
                   !(newParamObject.get(path).isObject())){
                        newParamObject.set(path, TOOLS.JsonInitObject());
                }
                newParamObject = (ObjectNode)newParamObject.get(path);
            }
        }
    }

    private void TraverseJson_CreateNew(JsonNode newParam, JsonNode target, String key, boolean isGetFromJsonPath){
        //WHEN::Target param is Object
        if(target.isObject()){
            if(isGetFromJsonPath && newParam.isObject()){
                TraverseJson_CreateNew_JsonPath(newParam, TOOLS.JsonInitObject(), key);
            } else if(!isGetFromJsonPath && newParam.isObject()){
                ((ObjectNode)newParam).set(key, TOOLS.JsonInitObject());
            } else if (newParam.isArray()){
                ((ArrayNode)newParam).add(TOOLS.JsonInitObject());
            }
            return;
        }

        //WHEN::Target param is Array
        if(target.isArray()){
            if(isGetFromJsonPath && newParam.isObject()){
                TraverseJson_CreateNew_JsonPath(newParam, TOOLS.JsonInitArray(), key);
            } else if(!isGetFromJsonPath && newParam.isObject()){
                ((ObjectNode)newParam).set(key, TOOLS.JsonInitArray());
            } else if (newParam.isArray()){
                ((ArrayNode)newParam).add(TOOLS.JsonInitArray());
            }
            return;
        }

        //WHEN::Set param when orther cases
        if(isGetFromJsonPath && newParam.isObject()){
            TraverseJson_CreateNew_JsonPath(newParam, target, key);
        } else if(!isGetFromJsonPath && newParam.isObject()){
            ((ObjectNode)newParam).set(key, target);
        } else if (newParam.isArray()){
            ((ArrayNode)newParam).add(target);
        }
    }

    private ERRORCODE TraverseJson(JsonNode require, JsonNode passParam, JsonNode newParam){
        //STEP::Check type
        if(!TraverseJson_CheckType(require, passParam)){
            LOGGER.DEBUG("Module-_DataCheck value type illegal");
            return ERRORCODE.ERR_Module__DataCheck_Check_Block;
        }

        //WHEN::Require is String type
        if (require.isString()) {
            if(!TraverseJson_CheckValue(require, passParam)){
                LOGGER.DEBUG("Module-_DataCheck value illegal");
                return ERRORCODE.ERR_Module__DataCheck_Check_Block;
            }
            return ERRORCODE.ERR_OK;
        }

        //WHEN::Require is object type
        if (require.isObject()) {
            ObjectNode requireParam = (ObjectNode) require;

            //WHEN-IN::Empty{} means allow anything
            if(requireParam.isEmpty()){
                if(newParam!=null){
                    ((ObjectNode)newParam).setAll((ObjectNode)passParam);
                }
                return ERRORCODE.ERR_OK;
            }

            //WHEN-IN::Not empty{} will check each key
            Set<Entry<String,JsonNode>> fields = requireParam.properties();
            for (Entry<String,JsonNode> entry : fields) {
                String key = entry.getKey();
                JsonNode requireValue = entry.getValue();

                //STEP-IN-IN::Get is necessary
                boolean isNecessary = true;
                if(key.startsWith("opt##")){
                    isNecessary = false;
                    key = key.substring("opt##".length());
                }else if(key.startsWith("nec##")){
                    isNecessary = true;
                    key = key.substring("nec##".length());
                }

                //STEP-IN-IN::Get list length limit
                Integer listLengthLimit = -1;
                Pattern pattern = Pattern.compile("^(\\d+)##");
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    String matchedString = matcher.group(1);
                    String tempKey = matcher.replaceFirst("");
                    if(requireValue.isArray()){
                        listLengthLimit = Integer.valueOf(matchedString);
                        key = tempKey;
                    }
                }

                //STEP-IN-IN::Check key is in passParam
                Object targetPassParam = null;
                boolean isGetFromPath = false;
                if(passParam.has(key)){
                    targetPassParam = passParam.get(key);
                }
                if(targetPassParam==null && TOOLS.JsonPathCheck(key)){
                    String jsonPath = TOOLS.JsonPathChangeReal(key);
                    try {
                        targetPassParam = passParam.at(jsonPath);
                        if(((JsonNode)targetPassParam).isMissingNode())
                            targetPassParam = null;
                        isGetFromPath = true;
                    } catch (Exception e){
                        LOGGER.DEBUG("Module-_DataCheck json path illegal:"+key+",jsonPath:"+jsonPath);
                        targetPassParam = null;
                    }
                }
                if(targetPassParam==null) {
                    if (isNecessary) {
                        LOGGER.DEBUG("Module-_DataCheck lack key:" + key);
                        return ERRORCODE.ERR_Module__DataCheck_Lack_Block;
                    }
                    if (!isNecessary) {
                        continue;
                    }
                }

                //WHEN-IN::requireValue=="", means allow anything
                if(newParam!=null && requireValue.isString() && requireValue.asString().equals("")){
                    if(isGetFromPath){
                        TraverseJson_CreateNew_JsonPath(newParam, (JsonNode)targetPassParam, key);
                    } else {
                        ((ObjectNode)newParam).set(key, (JsonNode)targetPassParam);
                    }
                    continue;
                }

                //STEP-IN-IN::Init newParam
                if(newParam!=null)
                    TraverseJson_CreateNew(newParam, (JsonNode)targetPassParam, key, isGetFromPath);

                //STEP-IN-IN::Traverse check
                ERRORCODE result = TraverseJson(requireValue, (JsonNode)targetPassParam, (newParam!=null)?(newParam).get(key):null);
                if(ERRORCODE.ERR_OK!=result){
                    LOGGER.DEBUG("Module-_DataCheck block key:"+key);
                    return result;
                }

                //STEP-IN-IN::Check list length limit
                if(listLengthLimit>-1 && targetPassParam!=null &&((JsonNode)targetPassParam).isArray()){
                    if(((ArrayNode)targetPassParam).size()>listLengthLimit){
                        return ERRORCODE.ERR_Module__DataCheck_LensLimit_Over;
                    }
                }
            }

            return ERRORCODE.ERR_OK;
        }

        //WHEN::Require is JSONArray type
        if(require.isArray()) {
            ArrayNode requireParam = (ArrayNode) require;
            //WHEN-IN::Empty[] means allow anything
            if(requireParam.isEmpty()){
                if(newParam!=null){
                    ((ArrayNode)newParam).addAll(requireParam);
                }
                return ERRORCODE.ERR_OK;
            }

            if(((ArrayNode)passParam).isEmpty()){
                LOGGER.DEBUG("Module-_DataCheck list is empty");
                return ERRORCODE.ERR_Module__DataCheck_List_Empty;
            }

            //WHEN-IN::Not empty[] will check every item
            for (JsonNode item : (ArrayNode)passParam) {
                ERRORCODE result = ERRORCODE.ERR_OK;

                //STEP-IN-IN::Init newParam
                if(newParam!=null)
                    TraverseJson_CreateNew(newParam, item, null, false);
                
                //STEP-IN-IN::Traverse check
                for(JsonNode item_2:requireParam){
                    result = TraverseJson(item_2, item, (newParam!=null)?((ArrayNode)newParam).get(newParam.size()-1):null);
                    if(ERRORCODE.ERR_OK==result){
                        break;
                    }
                }
                if(ERRORCODE.ERR_OK!=result) {
                    LOGGER.DEBUG("Module-_DataCheck list has value beyond the range");
                    return result;
                }
            }
            return ERRORCODE.ERR_OK;
        }

        //WHEN::It's not the above type, and the required values are exactly the same.
        if(!require.equals(passParam)){
            return ERRORCODE.ERR_Module__DataCheck_Check_Block;
        }

        return ERRORCODE.ERR_OK;
    }

    private ERRORCODE DoStart(ObjectNode moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        ObjectNode passParam = (ObjectNode) dataPool.get("passParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            //STEP::Get setting
            Boolean _isClean = TOOLS.ReadParam("_isClean", false, moduleParam, passParam);
            ObjectNode _param = TOOLS.ReadParam("_param", TOOLS.JsonInitObject(), moduleParam, passParam);
            LOGGER.DEBUG("Module-_DataCheck DoStart param, _isClean:" + _isClean);
            LOGGER.DEBUG("Module-_DataCheck DoStart param, _param:" + _param);
            
            //STEP::Init newParam for clean passParam
            ObjectNode newParam = null;
            if(_isClean){
                newParam = TOOLS.JsonInitObject();
            }
            
            //STEP::Check passParam
            if(!_param.isEmpty())
                result = TraverseJson(_param, passParam, newParam);

            //STEP::Replace passParam for clean
            if(result == ERRORCODE.ERR_OK && _isClean){
                dataPool.put("passParam", newParam);
            }

        } catch (Exception e) {
            LOGGER.ERROR("Module-_DataCheck exception:"+TOOLS.GetExceptionInfo(e), moduleParam, dataPool);
            result = ERRORCODE.ERR_Module__DataCheck_Exception;
        }

        return result;
    }

    @Override
    public ERRORCODE Start(ObjectNode moduleParam, HashMap<String, Object> dataPool){
        LOGGER.DEBUG("Module-_DataCheck start", moduleParam, dataPool);
        ERRORCODE result = ERRORCODE.ERR_OK;
        result = DoStart(moduleParam, dataPool, result);
        LOGGER.DEBUG("Module-_DataCheck end", moduleParam, dataPool);
        return result;
    }

    @Override
    public ERRORCODE End(HashMap<String, Object> dataPool, ERRORCODE result){
        LOGGER.DEBUG("Module-_DataCheck End start", dataPool);
        try{
            //Some work of cleaning up the module（When Controller end or Service end）
            //You need to set "Module.moduleNeedCallEnd.put("_DataCheck",true)" in _DataCheckConfig.Init() to take effect
        } catch (Exception e){LOGGER.ERROR("Module-_DataCheck End for clean exception:"+ TOOLS.GetExceptionInfo(e), dataPool);}
        LOGGER.DEBUG("Module-_DataCheck End end", dataPool);
        return result;
    }
}