package main.module;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;

@Component
public class Module {
    //TIPS::Logical fragment begins
    public static void Init(HashMap<String, Object> dataPool){
        ((ArrayList<ArrayList<String>>)dataPool.get("needEndModuleCall")).add(new ArrayList<>());
    }

    //TIPS::Call module
    public static ERRORCODE Start(String module, JsonNode moduleParam, HashMap<String, Object> dataPool){
        ERRORCODE result = ERRORCODE.ERR_OK;

        //STEP::Check whether moduleParam is object{}
        if(!moduleParam.isObject()) {
            LOGGER.ERROR("Module param(moduleParam) must be an object", dataPool);
            return ERRORCODE.ERR_Api_moduleParamType_Illegal;
        }

        //STEP::Check whether module is illegal
        if(moduleMaps.get(module)==null) {
            LOGGER.DEBUG("Module Lack:"+module);
            return result;
        }

        //STEP::Mark whether module need call end
        if(moduleNeedCallEnd.get(module)!=null && moduleNeedCallEnd.get(module)==true){
            ArrayList<ArrayList<String>> needCallEndList = (ArrayList<ArrayList<String>>) dataPool.get("needEndModuleCall");
            if(needCallEndList!=null && !needCallEndList.isEmpty()){
                ArrayList<String> lastArray = needCallEndList.get(needCallEndList.size() - 1);
                lastArray.add(module);
            }
        }

        //STEP::Call Module
        LOGGER.ChangePosition("Module-"+module);
        result = moduleMaps.get(module).Start((ObjectNode)moduleParam, dataPool);
        LOGGER.ChangePosition(null);

        return result;
    }

    //TIPS::The logic fragment ends, and the call module ends the logic
    public static void End(HashMap<String, Object> dataPool, ERRORCODE result){
        //STEP::Get module list for call module.End()
        ArrayList<ArrayList<String>> needCallEndList = (ArrayList<ArrayList<String>>) dataPool.get("needEndModuleCall");

        //STEP::Call each module.End()
        if(needCallEndList!=null && !needCallEndList.isEmpty()){
            dataPool.put("moduleParam", TOOLS.JsonInitObject());
            ArrayList<String> lastArray = needCallEndList.get(needCallEndList.size() - 1);
            if(!lastArray.isEmpty()){
                for (String module : lastArray) {
                    LOGGER.ChangePosition("Module-" + module);
                    moduleMaps.get(module).End(dataPool, result);
                    LOGGER.ChangePosition(null);
                }
            }
            needCallEndList.removeLast();
        }
        if(needCallEndList!=null && !needCallEndList.isEmpty()){
            LOGGER.ChangePosition(null);
        }
    }

    public static HashMap<String, Link> moduleMaps = new HashMap<>();
    public static HashMap<String, Boolean> moduleNeedCallEnd = new HashMap<>();
}