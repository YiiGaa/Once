package com.stoprefactoring.once.module._OperSession;

import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.module.Module;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class _OperSessionConfig {
    public _OperSessionConfig(){
        LOGGER.StraightInfo("Module-_OperSession: Load configuration");
    }

    @PostConstruct
    //This function will be called, after this module's config has been loaded
    public void Init() {
        //STEP::Register module
        LOGGER.StraightInfo("Module-_OperSession: Register module");
        Module.moduleMaps.put("_OperSession", new _OperSession());
        Module.moduleNeedCallEnd.put("_OperSession", true);

        //STEP::Init module
        LOGGER.StraightInfo("Module-_OperSession: Init module");
    }

    //@Value("${module._OperSession.value}")
    //public void _value(String value) {
    //    _OperSessionConfig.value = value;
    //}
    //public static String value;

}