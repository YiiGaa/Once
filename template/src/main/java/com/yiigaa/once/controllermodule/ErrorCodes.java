package com.yiigaa.once.controllermodule;

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
            return "\r\n" + strWriter.toString() + "\r\n";
        } catch (Exception ee) {
            return "\r\nbad getErrorInfoFromException!\r\n";
        }
    }

    public static JSONObject getErrorMessage(JSONObject param){
        if(null != param.get("message")){
            return param;
        }

        if(null == param.get("errorCode")){
            param.put("errorCode", "Success");
        }
        String[] tempStringArr = (String[]) errorCodeMaps.get(param.get("errorCode"));
        if(tempStringArr == null){
            param.put("errorCode", "unknown");
            param.put("message", "unknown");
        } else {
            param.put("errorCode", tempStringArr[0]);
            param.put("message", tempStringArr[1]);
        }

        return param;
    }


    private static HashMap<String, String[]> errorCodeMaps = new HashMap<String, String[]>(){{
        put("Success", new String[]{"200", "成功"});
        put("CONTROLLER_API_block", new String[]{"E-CC01(Controller)", "controller 崩溃"});
        //CheckHashParam
        put("MODULE_CheckHashParam_block", new String[]{"E-CM01(CheckHashParam)", "缺少必要参数"});
        put("MODULE_CheckHashParam_exception", new String[]{"E-CM02(CheckHashParam)", "检查必要参数崩溃"});
        //CheckNecessaryParam
        put("MODULE_CheckNecessaryParam_block", new String[]{"E-CM01(CheckNecessaryParam)", "缺少必要参数"});
        put("MODULE_CheckNecessaryParam_exception", new String[]{"E-CM02(CheckNecessaryParam)", "检查必要参数崩溃"});
        //ErgodicGetParam
        put("MODULE_ErgodicGetParam_exception", new String[]{"E-CM01(ErgodicGetParam)", "获取Url 参数崩溃"});
        //FileUpload
        put("MODULE_FileUpload_FileListEmpty_block", new String[]{"E-CM01(FileUpload)", "上传文件列表为空"});
        put("MODULE_FileUpload_FileEmpty_block", new String[]{"E-CM02(FileUpload)", "上传文件为空"});
        put("MODULE_FileUpload_WriteFile_block", new String[]{"E-CM03(FileUpload)", "写入文件失败"});
        put("MODULE_FileUpload_SizeBeyond_block", new String[]{"E-CM04(FileUpload)", "文件超过指定大小"});
        put("MODULE_FileUpload_exception", new String[]{"E-CM05(FileUpload)", "上传内部崩溃"});
        put("MODULE_FileUpload_intercept_block", new String[]{"E-CM06(FileUpload)", "上传类型拦截"});
        //FillingHashParam
        put("MODULE_FillingHashParam_SessionGet_block", new String[]{"E-CM01(FillingHashParam)", "session获取失败"});
        put("MODULE_FillingHashParam_exception", new String[]{"E-CM02(FillingHashParam)", "填充参数崩溃"});
        //FillingParam
        put("MODULE_FillingParam_SessionGet_block", new String[]{"E-CM01(FillingParam)", "session获取失败"});
        put("MODULE_FillingParam_exception", new String[]{"E-CM02(FillingParam)", "填充参数崩溃"});
        //SessionCancel
        put("MODULE_SessionCancel_exception", new String[]{"E-CM01(SessionCancel)", "Session去掉崩溃"});
        //SessionSave
        put("MODULE_SessionSave_exception", new String[]{"E-CM01(SessionSave)", "session 存储失败"});
    }};
}
