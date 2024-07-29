package com.stoprefactoring.once.module._DataCheck;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.common.TOOLS;
import com.stoprefactoring.once.module.Link;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.math.BigDecimal;
import java.util.HashMap;

public class _DataCheck extends Link {
    private Boolean TraverseJson_CheckType(Object require, Object passParam){
        //WHEN::Type JSONObject/JSONArray/Integer/Boolean/Double/Long need match
        if((require instanceof JSONObject && passParam instanceof JSONObject)||
           (require instanceof JSONArray && passParam instanceof JSONArray)||
           (require instanceof Integer && passParam instanceof Integer)||
           (require instanceof Boolean && passParam instanceof Boolean)||
           (require instanceof Long && passParam instanceof Long)||
           (require instanceof BigDecimal && passParam instanceof BigDecimal)){
                return true;
        }

        //WHEN::Type String may means 'Allow Anything'/'Specify Type'
        if(require instanceof String){
            String requireValue = (String)require;

            //WHEN-IN::Allow anything
            if(requireValue.equals("")){
                return true;
            }

            //WHEN-IN::Specify type
            if(requireValue.startsWith("str##") && passParam instanceof String){
                return true;
            }else if(requireValue.startsWith("int##") && passParam instanceof Integer){
                return true;
            }else if(requireValue.startsWith("int##") && passParam instanceof Boolean){
                return true;
            }else if(requireValue.startsWith("long##") && passParam instanceof Long){
                return true;
            }else if(requireValue.startsWith("double##") && passParam instanceof BigDecimal){
                return true;
            }else if(requireValue.contains("reg##") && 
                    (passParam instanceof String ||
                     passParam instanceof Integer ||
                     passParam instanceof BigDecimal ||
                     passParam instanceof Long ||
                     passParam instanceof Boolean
                    )){
                return true;
            }else if(passParam instanceof String){
                return true;
            }

        }
        return false;
    }

    private Boolean TraverseJson_CheckValue(Object require, Object passParam){
        String requireValue = (String)require;

        //WHEN::Allow anything
        if(requireValue.equals("")){
            return true;
        }

        //STEP::Get judge value
        String judgeValue = "";
        if(requireValue.startsWith("str##")){
            requireValue = requireValue.substring("str##".length());
        } else if(requireValue.startsWith("int##")){
            requireValue = requireValue.substring("int##".length());
        } else if(requireValue.startsWith("double##")){
            requireValue = requireValue.substring("double##".length());
        } else if(requireValue.startsWith("long##")){
            requireValue = requireValue.substring("long##".length());
        }
        if(passParam instanceof String){
            judgeValue = (String)passParam;
        }
        if(passParam instanceof Integer){
            judgeValue = ((Integer)passParam).toString();
        }
        if(passParam instanceof Long){
            judgeValue = ((Long)passParam).toString();
        }
        if(passParam instanceof Boolean){
            judgeValue = ((Integer)(((Boolean)passParam) ? 1 : 0)).toString();
        }
        if(passParam instanceof BigDecimal){
            judgeValue = ((BigDecimal)passParam).toString();
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

    private void TraverseJson_CreateNew_JsonPath(Object newParam, Object target, String key){
        String[] pathArr = key.split(">>");
        JSONObject newParamObject = (JSONObject)newParam;
        Integer index = 0;
        for (String path:pathArr) {
            index++;
            if(index == pathArr.length){
                newParamObject.put(path, target);
            } else {
                if(!(newParamObject.containsKey(path)) || 
                   !(newParamObject.get(path) instanceof JSONObject)){
                        newParamObject.put(path, new JSONObject());
                }
                newParamObject = (JSONObject)newParamObject.get(path);
            }
        }
    }

    private void TraverseJson_CreateNew(Object newParam, Object target, String key, boolean isGetFromJsonPath){
        if(newParam==null){
            return;
        }

        //WHEN::Target param is JSONObject
        if(target instanceof JSONObject){
            if(isGetFromJsonPath && newParam instanceof JSONObject){
                TraverseJson_CreateNew_JsonPath(newParam, new JSONObject(), key);
            } else if(!isGetFromJsonPath && newParam instanceof JSONObject){
                ((JSONObject)newParam).put(key, new JSONObject());
            } else if (newParam instanceof JSONArray){
                ((JSONArray)newParam).add(new JSONObject());
            }
            return;
        }

        //WHEN::Target param is JSONArray
        if(target instanceof JSONArray){
            if(isGetFromJsonPath && newParam instanceof JSONObject){
                TraverseJson_CreateNew_JsonPath(newParam, new JSONArray(), key);
            } else if(!isGetFromJsonPath && newParam instanceof JSONObject){
                ((JSONObject)newParam).put(key, new JSONArray());
            } else if (newParam instanceof JSONArray){
                ((JSONArray)newParam).add(new JSONArray());
            }
            return;
        }

        //WHEN::Set param when orther cases
        if(isGetFromJsonPath && newParam instanceof JSONObject){
            TraverseJson_CreateNew_JsonPath(newParam, target, key);
        } else if(!isGetFromJsonPath && newParam instanceof JSONObject){
            ((JSONObject)newParam).put(key, target);
        } else if (newParam instanceof JSONArray){
            ((JSONArray)newParam).add(target);
        }
    }

    private ERRORCODE TraverseJson(Object require, Object passParam, Object newParam){
        //STEP::Check type
        if(!TraverseJson_CheckType(require, passParam)){
            return ERRORCODE.ERR_Module__DataCheck_Check_Block;
        }

        //WHEN::Require is String type
        if (require instanceof String) {
            if(!TraverseJson_CheckValue(require, passParam)){
                return ERRORCODE.ERR_Module__DataCheck_Check_Block;
            }
            return ERRORCODE.ERR_OK;
        }

        //WHEN::Require is JSONObject type
        if (require instanceof JSONObject) {
            JSONObject requireParam = (JSONObject) require;

            //WHEN-IN::Empty{} means allow anything
            if(requireParam.isEmpty()){
                if(newParam!=null){
                    for (String key : ((JSONObject)passParam).keySet()) {
                        ((JSONObject)newParam).put(key, ((JSONObject)passParam).get(key));
                    }
                }
                return ERRORCODE.ERR_OK;
            }

            //WHEN-IN::Not empty{} will check each key
            for (String key : requireParam.keySet()) {
                Object requireValue = requireParam.get(key);

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
                    if(requireValue instanceof JSONArray){
                        listLengthLimit = Integer.parseInt(matchedString);
                        key = tempKey;
                    }
                }

                //STEP-IN-IN::Check key is in passParam
                Object targetPassParam = null;
                boolean isGetFromPath = false;
                if(((JSONObject)passParam).containsKey(key)){
                    targetPassParam = ((JSONObject)passParam).get(key);
                }
                if(targetPassParam==null && TOOLS.JsonPathCheck(key)){
                    String jsonPath = TOOLS.JsonPathChangeReal(key);
                    try {
                        targetPassParam = ((JSONObject) passParam).getByPath(jsonPath);
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

                //STEP-IN-IN::Init newParam
                TraverseJson_CreateNew(newParam, targetPassParam, key, isGetFromPath);

                //STEP-IN-IN::Traverse check
                ERRORCODE result = TraverseJson(requireValue, targetPassParam, (newParam!=null)?((JSONObject)newParam).get(key):null);
                if(ERRORCODE.ERR_OK!=result){
                    LOGGER.DEBUG("Module-_DataCheck block key:"+key);
                    return result;
                }

                //STEP-IN-IN::Check list length limit
                if(listLengthLimit>-1 && targetPassParam instanceof JSONArray){
                    if(((JSONArray)targetPassParam).size()>listLengthLimit){
                        return ERRORCODE.ERR_Module__DataCheck_LensLimit_Over;
                    }
                }
            }

            return ERRORCODE.ERR_OK;
        }

        //WHEN::Require is JSONArray type
        if(require instanceof JSONArray) {
            JSONArray requireParam = (JSONArray) require;
            //WHEN-IN::Empty[] means allow anything
            if(requireParam.isEmpty()){
                if(newParam!=null){
                    for (Object item : (JSONArray)passParam) {
                        ((JSONArray)newParam).add(item);
                    }
                }
                return ERRORCODE.ERR_OK;
            }

            //WHEN-IN::Not empty[] will check every item
            for(Object item:(JSONArray)passParam){
                ERRORCODE result = ERRORCODE.ERR_OK;

                //STEP-IN-IN::Init newParam
                TraverseJson_CreateNew(newParam, item, null, false);
                
                //STEP-IN-IN::Traverse check
                for(Object item_2:requireParam){
                    result = TraverseJson(item_2, item, (newParam!=null)?((JSONArray)newParam).getLast():null);
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

    private ERRORCODE DoStart(JSONObject moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        JSONObject passParam = (JSONObject) dataPool.get("passParam");
        JSONObject returnParam = (JSONObject) dataPool.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            //STEP::Get setting
            Boolean _isClean = TOOLS.ReadParam("_isClean", false, moduleParam, passParam);
            JSONObject _param = TOOLS.ReadParam("_param", new JSONObject(), moduleParam, passParam);
            LOGGER.DEBUG("Module-_DataCheck DoStart param, _isClean:" + _isClean);
            LOGGER.DEBUG("Module-_DataCheck DoStart param, _param:" + _param.toString());
            
            //STEP::Init newParam for clean passParam
            JSONObject newParam = null;
            if(_isClean){
                newParam = new JSONObject();
            }
            
            //STEP::Check passParam
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
    public ERRORCODE Start(JSONObject moduleParam, HashMap<String, Object> dataPool){
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