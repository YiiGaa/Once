package com.stoprefactoring.once.module._DataCheck;

import com.stoprefactoring.once.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import com.stoprefactoring.once.common.LOGGER;

@Component
public class _DataCheckConfig {
    public _DataCheckConfig(){
        LOGGER.StraightInfo("Module-_DataCheck: Load configuration");
    }

    @PostConstruct
    //This function will be called, after this module's config has been loaded
    public void Init() {
        //STEP::Register module
        LOGGER.StraightInfo("Module-_DataCheck: Register module");
        Module.moduleMaps.put("_DataCheck", new _DataCheck());
        Module.moduleNeedCallEnd.put("_DataCheck", false);

        //STEP::Init module
        LOGGER.StraightInfo("Module-_DataCheck: Init module");
    }

    //@Value("${module._DataCheck.value}")
    //public void _value(String value) {
    //    Config.value = value;
    //}
    //public static String value;

}