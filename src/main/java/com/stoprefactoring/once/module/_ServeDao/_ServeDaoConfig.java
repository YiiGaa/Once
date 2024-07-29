package com.stoprefactoring.once.module._ServeDao;

import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.module.Module;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import java.sql.SQLException;

@Component
public class _ServeDaoConfig {
    public _ServeDaoConfig(){
        LOGGER.StraightInfo("Module-_ServeDao: Load configuration");
    }

    @PostConstruct
    //This function will be called, after this module's config has been loaded
    public void Init() throws SQLException {
        //STEP::Register module
        LOGGER.StraightInfo("Module-_ServeDao: Register module");
        Module.moduleMaps.put("_ServeDao", new _ServeDao());
        Module.moduleNeedCallEnd.put("_ServeDao", true);

        //STEP::Init module
        LOGGER.StraightInfo("Module-_ServeDao: Init module");
        if(isAutoGenerateMap){
            _ServeDaoGenerateMap.Start();
        }
        LOGGER.StraightInfo("Module-_ServeDao: Init 3 module");
    }

    @Value("${module._ServeDao.isAutoGenerateMap:true}")
    public void _isAutoGenerateMap(boolean value) {
        _ServeDaoConfig.isAutoGenerateMap = value;
    }
    public static boolean isAutoGenerateMap;

    @Resource(name = "Module_ServeDao.ConnectHandler")
    public void _daoHandler(JdbcTemplate value) {
        _ServeDaoConfig.daoHandler = value;
    }
    public static JdbcTemplate daoHandler;

    @Resource(name = "Module_ServeDao.Transaction")
    public void _transactionManager(PlatformTransactionManager value) {
        _ServeDaoConfig.transactionManager = value;
    }
    public static PlatformTransactionManager transactionManager;

    //Set by _ServeDaoGenerateMap(need isAutoGenerateMap is true)
    //_ServeDaoConfig.tableViewMap data like:
    //{
    //    "table/view 1 name":{
    //          "type":"table/view"
    //          "keyList":["column name(key column)"]
    //          "column":{
    //              "column 1 name":{
    //                  "type":"",
    //                  "textMaxLength":255(Long type),
    //                  "isNullAble":true/false,
    //                  "isKey":true/false
    //              },
    //              "column 2 name":{...}
    //          }
    //    },
    //    "table/view 2 name":{...}
    //}
    public static boolean isGenerateSuccess = false;
    public static String dataBaseName = "";
    public static JSONObject tableViewMap = new JSONObject();
}