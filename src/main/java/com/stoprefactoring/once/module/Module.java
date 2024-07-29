package com.stoprefactoring.once.module;

import java.util.HashMap;
import java.util.Set;
import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import org.springframework.stereotype.Component;

@Component
public class Module {
    public static ERRORCODE Start(String module, JSONObject moduleParam, HashMap<String, Object> param){
        String position = LOGGER.GetPosition();
        ERRORCODE result = ERRORCODE.ERR_OK;

        //STEP::Check whether module is illegal
        if(moduleMaps.get(module)==null) {
            LOGGER.DEBUG("Module Lack:"+module);
            return result;
        }

        //STEP::Mark whether module need call end
        if(moduleNeedCallEnd.get(module)!=null && moduleNeedCallEnd.get(module)){
            if(position.equals("API")){
                ((Set<String>)(param.get("needEndModuleCall_api"))).add(module);
            }else{
                ((Set<String>)(param.get("needEndModuleCall_service"))).add(module);
            }
        }

        //STEP::Call Module
        LOGGER.ChangePosition("Module-"+module);
        result = moduleMaps.get(module).Start(moduleParam, param);
        LOGGER.ChangePosition(null);

        return result;
    }

    public static void End(HashMap<String, Object> param, ERRORCODE result){
        String position = LOGGER.GetPosition();
        boolean isAPI = !position.equals("SERVICE");
        param.put("moduleParam", new JSONObject());

        //STEP::Get module list for call module.End()
        Set<String> needCallEndList = isAPI?(Set<String>)(param.get("needEndModuleCall_api")): (Set<String>)(param.get("needEndModuleCall_service"));

        //STEP::Call each module.End()
        for (Object object : needCallEndList) {
            String module = (String) object;
            LOGGER.ChangePosition("Module-" + module);
            moduleMaps.get(module).End(param, result);
            LOGGER.ChangePosition(null);
        }
        needCallEndList.clear();
        if(!isAPI){
            LOGGER.ChangePosition(null);
        }
    }

    public static HashMap<String, Link> moduleMaps = new HashMap<>();
    public static HashMap<String, Boolean> moduleNeedCallEnd = new HashMap<>();
}