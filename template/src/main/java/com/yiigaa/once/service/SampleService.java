package com.yiigaa.once.service;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.dao.DataBaseDao;
import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.yiigaa.once.servicemodule.ServiceModule;
import com.yiigaa.once.service.SampleService;
import org.springframework.stereotype.Service;
import com.yiigaa.once.servicemodule.ErrorCodes;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.HashMap;

@Service("SampleService")
public class SampleService {
    @Resource(name = "DataBase")
    private DataBaseDao _DataBaseDao;

    public HashMap<String, Object> addData(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {
            //STEP: call DataBase
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("control","insert");
					put("form","t_test");

            }});
            moduleMap = _DataBaseDao.start(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                return moduleMap;
            }


           
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

    public HashMap<String, Object> getData(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {
            //STEP: call DataBase
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("control","select");
					put("isAsResult","true");
					put("query","select * from t_test where test_id='@test_id@'");

            }});
            moduleMap = _DataBaseDao.start(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                return moduleMap;
            }


           
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

    public HashMap<String, Object> getDataAll(HashMap<String, Object> moduleMap) {
        COMMON_LOGGER.DEBUG(moduleMap, "Service start");

        try {
            //STEP: call DataBase
            moduleMap.put("moduleParam", new HashMap<String, String>(){{
					put("control","select");
					put("form","t_test");
					put("isAsResult","true");
					put("isAllGet","true");

            }});
            moduleMap = _DataBaseDao.start(moduleMap);
            if(ErrorCodes.isErrorHappens(moduleMap)){
                return moduleMap;
            }


           
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
