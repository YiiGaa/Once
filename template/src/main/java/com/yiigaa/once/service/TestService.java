package com.yiigaa.once.service;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.dao.DataBaseDao;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.servicemodule.ServiceModule;
import com.yiigaa.once.service.TestService;
import org.springframework.stereotype.Service;
import com.yiigaa.once.servicemodule.ErrorCodes;

import javax.annotation.Resource;
import java.util.HashMap;

@Service("TestService")
public class TestService {
    @Resource(name = "DataBase")
    private DataBaseDao _DataBaseDao;

    public HashMap<String, Object> createTest(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {

           
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));
            ((JSONObject)moduleMap.get("returnParam")).put("errorCode", "Serivce_Inner_block");

        }  finally {
            //STEP get errorMessage
            moduleMap = ErrorCodes.getErrorMessage(moduleMap);
        }

        COMMON_LOGGER.DEBUG(moduleMap, "Service end");
        return moduleMap;
    }

    public HashMap<String, Object> deleteTest(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {

           
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));
            ((JSONObject)moduleMap.get("returnParam")).put("errorCode", "Serivce_Inner_block");

        }  finally {
            //STEP get errorMessage
            moduleMap = ErrorCodes.getErrorMessage(moduleMap);
        }

        COMMON_LOGGER.DEBUG(moduleMap, "Service end");
        return moduleMap;
    }

    public HashMap<String, Object> updateTest(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {

           
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));
            ((JSONObject)moduleMap.get("returnParam")).put("errorCode", "Serivce_Inner_block");

        }  finally {
            //STEP get errorMessage
            moduleMap = ErrorCodes.getErrorMessage(moduleMap);
        }

        COMMON_LOGGER.DEBUG(moduleMap, "Service end");
        return moduleMap;
    }

    public HashMap<String, Object> getTest(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {

           
        } catch(Exception e){
            COMMON_LOGGER.ERROR(moduleMap, ErrorCodes.getErrorInfoFromException(e));
            ((JSONObject)moduleMap.get("returnParam")).put("errorCode", "Serivce_Inner_block");

        }  finally {
            //STEP get errorMessage
            moduleMap = ErrorCodes.getErrorMessage(moduleMap);
        }

        COMMON_LOGGER.DEBUG(moduleMap, "Service end");
        return moduleMap;
    }



}
