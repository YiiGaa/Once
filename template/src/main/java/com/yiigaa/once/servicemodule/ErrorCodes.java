package com.yiigaa.once.servicemodule;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

/*
* Special Note:
* When using the command "Christmas.rb Menu/MakeEngineeringNormal/AutoLinkModule@Controller/",
* Everything in this file will be updated.
* So don't add any code here
* If you need to modify this file, you need to modify it in /Christmas/Template/AutoLinkModule , and then execute the command "Christmas.rb Menu/MakeEngineeringNormal/AutoLinkModule@Controller/".
*/
public class ErrorCodes {
    public static boolean isErrorHappens(HashMap<String, Object> param){
        JSONObject checkParam = (JSONObject) param.get("returnParam");
        if(null == checkParam.get("errorCode") || 0 == ((String)checkParam.get("errorCode")).compareTo("Success") || 0 == ((String)checkParam.get("errorCode")).compareTo("200") || 0 == ((String)checkParam.get("errorCode")).compareTo("0")){
            return false;
        }
        return true;
    }

    public static String getErrorInfoFromException(Exception e){
        try {
            StringWriter strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            e.printStackTrace(printWriter);
            return "\n\r" + strWriter.toString() + "\n\r";
        } catch (Exception ee) {
            return "\n\rbad getErrorInfoFromException!\n\r";
        }
    }

    public static HashMap<String, Object> getErrorMessage(HashMap<String, Object> moduleMap){
        JSONObject param = (JSONObject) (moduleMap.get("returnParam"));
        if(null != param.get("message")){
            return moduleMap;
        }

        if(null == param.get("errorCode") || 0 == (param.get("errorCode").toString()).compareTo("Success")){
            return moduleMap;
        }
        String[] tempStringArr = (String[]) errorCodeMaps.get(param.get("errorCode"));
        if(tempStringArr == null){
            param.put("errorCode", "unknown");
            param.put("message", "unknown");
        } else {
            param.put("errorCode", tempStringArr[0]);
            param.put("message", tempStringArr[1]);
        }

        moduleMap.put("returnParam", param);

        return moduleMap;
    }


    private static HashMap<String, String[]> errorCodeMaps = new HashMap<String, String[]>(){{
        put("Serivce_Inner_block", new String[]{"E-SS01(Service)", "Service 崩溃"});
        //DataBaseDao
        put("DAO_InsertInDb_exception", new String[]{"E-SD01(Dao)", "数据库insert崩溃"});
        put("DAO_BatchInsertInDb_exception", new String[]{"E-SD01(Dao)", "数据库batchInsert崩溃"});
        put("DAO_DeleteInDb_exception", new String[]{"E-SD02(Dao)", "数据库delete崩溃"});
        put("DAO_UpdateInDb_exception", new String[]{"E-SD03(Dao)", "数据库update崩溃"});
        put("DAO_Search_Block", new String[]{"E-SD04(Dao)", "数据search失败"});
        put("DAO_SearchInDb_exception", new String[]{"E-SD05(Dao)", "数据库search崩溃"});
        put("DAO_ExcuteUpdate_block", new String[]{"E-SD06(Dao)", "数据库执行更新失败"});
        put("DAO_ExcuteUpdate_exception", new String[]{"E-SD07(Dao)", "数据库执行更新崩溃"});
        put("DAO_ExcuteSearch_Exception", new String[]{"E-SD08(Dao)", "数据库执行更新崩溃"});
        put("DAO_Order_Block", new String[]{"E-SD09(Dao)", "非法Order"});
        //DeleteFile
        put("MODULE_DeleteFile_block", new String[]{"E-SM01(DeleteFile)", "非法文件目录"});
        put("MODULE_DeleteFile_exception", new String[]{"E-SM02(DeleteFile)", "删除文件崩溃"});

        //FillingParams
        put("MODULE_FillingParams_sessionGet_block", new String[]{"E-SM01(FillingParams)", "session获取失败"});
        put("MODULE_FillingParams_exception", new String[]{"E-SM02(FillingParams)", "填充参数崩溃"});
    }};
}
