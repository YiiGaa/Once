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
        put("MODULE_CheckNecessaryParam_array_block", new String[]{"E-CM03(CheckNecessaryParam)", "存在参数的值为非数组"});
        put("MODULE_CheckNecessaryParam_arrayEmpty_block", new String[]{"E-CM04(CheckNecessaryParam)", "存在参数的值为空数组"});
        //ErgodicGetParam
        put("MODULE_ErgodicGetParam_exception", new String[]{"E-CM01(ErgodicGetParam)", "获取Url 参数崩溃"});
        //FileUpload
        put("MODULE_FileUpload_fileListEmpty_block", new String[]{"E-CM01(FileUpload)", "上传文件列表为空"});
        put("MODULE_FileUpload_fileEmpty_block", new String[]{"E-CM02(FileUpload)", "上传文件为空"});
        put("MODULE_FileUpload_writeFile_block", new String[]{"E-CM03(FileUpload)", "写入文件失败"});
        put("MODULE_FileUpload_sizeBeyond_block", new String[]{"E-CM04(FileUpload)", "文件超过指定大小"});
        put("MODULE_FileUpload_exception", new String[]{"E-CM05(FileUpload)", "上传内部崩溃"});
        put("MODULE_FileUpload_intercept_block", new String[]{"E-CM06(FileUpload)", "上传类型拦截"});
        put("MODULE_FileUpload_createDir_block", new String[]{"E-CM07(FileUpload)", "创建目录失败"});
        //FillingHashParam
        put("MODULE_FillingHashParam_sessionGet_block", new String[]{"E-CM01(FillingHashParam)", "session获取失败"});
        put("MODULE_FillingHashParam_exception", new String[]{"E-CM02(FillingHashParam)", "填充参数崩溃"});
        put("MODULE_FillingHashParam_target_block", new String[]{"E-CM03(FillingHashParam)", "目标key不存在"});
        //FillingParam
        put("MODULE_FillingParam_sessionGet_block", new String[]{"E-CM01(FillingParam)", "session获取失败"});
        put("MODULE_FillingParam_exception", new String[]{"E-CM02(FillingParam)", "填充参数崩溃"});
        //SessionOperation
        put("MODULE_SessionOperation_exception", new String[]{"E-CM01(SessionOperation)", " SessionOperation 崩溃"});
    }};
}
