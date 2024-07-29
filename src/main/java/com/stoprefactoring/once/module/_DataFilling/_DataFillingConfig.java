package com.stoprefactoring.once.module._DataFilling;

import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class _DataFillingConfig {
    public _DataFillingConfig(){
        LOGGER.StraightInfo("Module-_DataFilling: Load configuration");
    }

    @PostConstruct
    //This function will be called, after this module's config has been loaded
    public void Init() {
        //Init action
        LOGGER.StraightInfo("Init module: _DataFilling");

        //STEP::Register module
        LOGGER.StraightInfo("Module-_DataFilling: Register module");
        Module.moduleMaps.put("_DataFilling", new _DataFilling());
        Module.moduleNeedCallEnd.put("_DataFilling", false);

        //STEP::Init module
        LOGGER.StraightInfo("Module-_DataFilling: Init module");
    }

    //@Value("${module._DataCheck.value}")
    //public void _value(String value) {
    //    _DataFillingConfig.value = value;
    //}
    //public static String value;

}