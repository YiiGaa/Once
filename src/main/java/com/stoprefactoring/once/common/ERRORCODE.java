/*
* 本文件内容不要人为修改，本文件由"python3 Christmas.py Input/MergeCode/LinkModule"生成
* 如果再次运行生成脚本，人为修改的代码将被删除
* 本文件模板请修改Christmas/Template/LinkModule/ErrorCode.templ
* 模块ErrorCode请修改src/main/java/com/stoprefactoring/once/module/??/Once.Link
* 修改结束后，终端执行"python3 Christmas.py Input/MergeCode/LinkModule"重新生成此文件
*
* The contents of this file should not be modified. This file is generated by "python3 Christmas.py Input/MergeCode/LinkModule"
* If the generation script is run again, the artificially modified code will be deleted
* This file template settings, please modify Christmas/Template/LinkModule/ErrorCode.templ
* Module ErrorCode settings, please modify src/main/java/com/stoprefactoring/once/module/??/Once.Link
* After the modification, execute "python3 Christmas.py Input/MergeCode/LinkModule" in the terminal to regenerate this file.
*/
package com.stoprefactoring.once.common;

import com.alibaba.fastjson2.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum ERRORCODE {
    ERR_OK,
    ERR_Ignore,
    ERR_Api_Exception,
    ERR_Api_Inner_Exception,
    ERR_Api_Switch_Lack,
    ERR_ApiInit_Param_Exception,
    ERR_ApiInit_ContentType_Illegal,
    ERR_Service_Exception,

    //_OperFile
    ERR_Module__OperFile_Exception,
    ERR_Module__OperFile_Action_Illegal,
    ERR_Module__OperFile_Upload_File_Lack,
    ERR_Module__OperFile_Upload_FileCount_Limit,
    ERR_Module__OperFile_Upload_FileSize_Limit,
    ERR_Module__OperFile_Upload_File_Empty,
    ERR_Module__OperFile_Upload_File_FilePreCheck_Empty,
    ERR_Module__OperFile_Upload_FileName_Illegal,
    ERR_Module__OperFile_Upload_FileSuffix_Block,
    ERR_Module__OperFile_Upload_File_Exist,
    ERR_Module__OperFile_Upload_FilePreCheck_Exist,
    ERR_Module__OperFile_Upload_UploadListSize_Incorrect,
    ERR_Module__OperFile_Upload_CreateParentDirs_Fail,
    ERR_Module__OperFile_Upload_Exception,
    ERR_Module__OperFile_Delete_Path_Empty,
    ERR_Module__OperFile_Delete_Path_Illegal,
    ERR_Module__OperFile_Delete_Path_Lack,
    ERR_Module__OperFile_Delete_Exception,
    ERR_Module__OperFile_Delete_File_Fail,
    ERR_Module__OperFile_Delete_Directory_Fail,
    ERR_Module__OperFile_UploadRollBack_Exception,
    ERR_Module__OperFile_Move_SourcePath_Empty,
    ERR_Module__OperFile_Move_SourcePath_Illegal,
    ERR_Module__OperFile_Move_TargetPath_Empty,
    ERR_Module__OperFile_Move_TargetSource_Same,
    ERR_Module__OperFile_Move_TargetPath_Illegal,
    ERR_Module__OperFile_Move_SourcePath_Lack,
    ERR_Module__OperFile_Move_TargetPath_Exist,
    ERR_Module__OperFile_Move_TargetPath_CoverDirs_Fail,
    ERR_Module__OperFile_Move_Exception,
    ERR_Module__OperFile_End_Exception,
    //_ServeDao
    ERR_Module__ServeDao_Init_GetDataBaseName_Exception,
    ERR_Module__ServeDao_Exception,
    ERR_Module__ServeDao_Action_Illegal,
    ERR_Module__ServeDao_SQLMake_Empty,
    ERR_Module__ServeDao_TableCheck_TableName_Illegal,
    ERR_Module__ServeDao_TableCheck_Table_Lack,
    ERR_Module__ServeDao_TableCheck_TableInfo_Lack,
    ERR_Module__ServeDao_TableCheck_TableType_Illegal,
    ERR_Module__ServeDao_Insert_Column_Lack,
    ERR_Module__ServeDao_Insert_ColumnValueLength_Limit,
    ERR_Module__ServeDao_InsertBatch_Data_Empty,
    ERR_Module__ServeDao_InsertBatch_Column_Lack,
    ERR_Module__ServeDao_InsertBatch_ColumnValueLength_Limit,
    ERR_Module__ServeDao_Update_ColumnValueLength_Limit,
    ERR_Module__ServeDao_SQLStringDeal_Template_Illegal,
    ERR_Module__ServeDao_SQLStringDeal_Replace_Illegal,
    ERR_Module__ServeDao_SQLExecute_Type_Illegal,
    ERR_Module__ServeDao_SQLExecute_Exception,
    ERR_Module__ServeDao_SQLExecute_Update_Fail,
    ERR_Module__ServeDao_SQLExecute_Select_Null_Error,
    ERR_Module__ServeDao_SQLExecute_Select_NotNull_Error,
    ERR_Module__ServeDao_SQLSelect_Type_Illegal,
    ERR_Module__ServeDao_TXNStart_Exception,
    ERR_Module__ServeDao_TXNStart_Create_Already,
    ERR_Module__ServeDao_TXNStart_Level_Illegal,
    ERR_Module__ServeDao_TXNEnd_Exception,
    //_DataFilling
    ERR_Module__DataFilling_Exception,
    ERR_Module__DataFilling_JsonPath_Illegal,
    ERR_Module__DataFilling_Session_Fail,
    //_DataCheck
    ERR_Module__DataCheck_Exception,
    ERR_Module__DataCheck_Check_Block,
    ERR_Module__DataCheck_Lack_Block,
    ERR_Module__DataCheck_LensLimit_Over,
    ERR_Module__DataCheck_List_Empty,
    ERR_Module__DataCheck_JsonPath_Illegal,
    //_OperSession
    ERR_Module__OperSession_Exception,
    ERR_Module__OperSession_Action_Illegal,
    ERR_Module__OperSession_Save_Empty,
    ;

    private static HashMap<ERRORCODE, String[]> errorCodeMaps = new HashMap<>() {{
        put(ERR_OK, new String[]{"200", "OK"});
        put(ERR_Api_Exception, new String[]{"E-WS(API)", "API exception"});
        put(ERR_Api_Inner_Exception, new String[]{"E-WS(API)", "API Inner exception"});
        put(ERR_Api_Switch_Lack, new String[]{"E-WS(API)", "API Switch lack case"});
        put(ERR_ApiInit_Param_Exception, new String[]{"E-WS(API)", "API Init get request param exception"});
        put(ERR_ApiInit_ContentType_Illegal, new String[]{"E-WS(API)", "API Init request content type illegal"});
        put(ERR_Service_Exception, new String[]{"E-WS(SERVICE)", "Service exception"});

        //_OperFile
        put(ERR_Module__OperFile_Exception, new String[]{"E-WS(_OperFile)", "_OperFile exception"});
        put(ERR_Module__OperFile_Action_Illegal, new String[]{"E-WS(_OperFile)", "_OperFile Action illegal"});
        put(ERR_Module__OperFile_Upload_File_Lack, new String[]{"E-WS(_OperFile)", "_OperFile Upload file lack"});
        put(ERR_Module__OperFile_Upload_FileCount_Limit, new String[]{"E-WS(_OperFile)", "_OperFile Upload file count over limit"});
        put(ERR_Module__OperFile_Upload_FileSize_Limit, new String[]{"E-WS(_OperFile)", "_OperFile Upload file size over limit"});
        put(ERR_Module__OperFile_Upload_File_Empty, new String[]{"E-WS(_OperFile)", "_OperFile Upload file is empty"});
        put(ERR_Module__OperFile_Upload_File_FilePreCheck_Empty, new String[]{"E-WS(_OperFile)", "_OperFile Upload file is empty(pre-check)"});
        put(ERR_Module__OperFile_Upload_FileName_Illegal, new String[]{"E-WS(_OperFile)", "_OperFile Upload file name is illegal, contain '\\' or '/'"});
        put(ERR_Module__OperFile_Upload_FileSuffix_Block, new String[]{"E-WS(_OperFile)", "_OperFile Upload file suffix is not allow"});
        put(ERR_Module__OperFile_Upload_File_Exist, new String[]{"E-WS(_OperFile)", "_OperFile Upload file is already exist"});
        put(ERR_Module__OperFile_Upload_FilePreCheck_Exist, new String[]{"E-WS(_OperFile)", "_OperFile Upload file(pre-check) is already exist"});
        put(ERR_Module__OperFile_Upload_UploadListSize_Incorrect, new String[]{"E-WS(_OperFile)", "_OperFile Upload length of uploadList is incorrect"});
        put(ERR_Module__OperFile_Upload_CreateParentDirs_Fail, new String[]{"E-WS(_OperFile)", "_OperFile Upload create parent dirs fail"});
        put(ERR_Module__OperFile_Upload_Exception, new String[]{"E-WS(_OperFile)", "_OperFile Upload file exception"});
        put(ERR_Module__OperFile_Delete_Path_Empty, new String[]{"E-WS(_OperFile)", "_OperFile Delete path empty"});
        put(ERR_Module__OperFile_Delete_Path_Illegal, new String[]{"E-WS(_OperFile)", "_OperFile Delete path illegal"});
        put(ERR_Module__OperFile_Delete_Path_Lack, new String[]{"E-WS(_OperFile)", "_OperFile Delete path is not exists"});
        put(ERR_Module__OperFile_Delete_Exception, new String[]{"E-WS(_OperFile)", "_OperFile Delete exception"});
        put(ERR_Module__OperFile_Delete_File_Fail, new String[]{"E-WS(_OperFile)", "_OperFile Delete file fail"});
        put(ERR_Module__OperFile_Delete_Directory_Fail, new String[]{"E-WS(_OperFile)", "_OperFile Delete directory fail"});
        put(ERR_Module__OperFile_UploadRollBack_Exception, new String[]{"E-WS(_OperFile)", "_OperFile UploadRollBack file rollback exception(queue full)"});
        put(ERR_Module__OperFile_Move_SourcePath_Empty, new String[]{"E-WS(_OperFile)", "_OperFile Move source path empty"});
        put(ERR_Module__OperFile_Move_SourcePath_Illegal, new String[]{"E-WS(_OperFile)", "_OperFile Move source path illegal"});
        put(ERR_Module__OperFile_Move_TargetPath_Empty, new String[]{"E-WS(_OperFile)", "_OperFile Move target path empty"});
        put(ERR_Module__OperFile_Move_TargetSource_Same, new String[]{"E-WS(_OperFile)", "_OperFile Move target/source path is same"});
        put(ERR_Module__OperFile_Move_TargetPath_Illegal, new String[]{"E-WS(_OperFile)", "_OperFile Move target path illegal"});
        put(ERR_Module__OperFile_Move_SourcePath_Lack, new String[]{"E-WS(_OperFile)", "_OperFile Move source path is not exists"});
        put(ERR_Module__OperFile_Move_TargetPath_Exist, new String[]{"E-WS(_OperFile)", "_OperFile Move target path is exists"});
        put(ERR_Module__OperFile_Move_TargetPath_CoverDirs_Fail, new String[]{"E-WS(_OperFile)", "_OperFile Move target path is exists, and directory can not be covered"});
        put(ERR_Module__OperFile_Move_Exception, new String[]{"E-WS(_OperFile)", "_OperFile Move exception"});
        put(ERR_Module__OperFile_End_Exception, new String[]{"E-WS(_OperFile)", "_OperFile End file rollback exception(queue full)"});
        //_ServeDao
        put(ERR_Module__ServeDao_Exception, new String[]{"E-WS(_ServeDao)", "_ServeDao exception"});
        put(ERR_Module__ServeDao_Action_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao Action illegal"});
        put(ERR_Module__ServeDao_SQLMake_Empty, new String[]{"E-WS(_ServeDao)", "_ServeDao Make SQL fail"});
        put(ERR_Module__ServeDao_TableCheck_TableName_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao TableCheck table name is illegal"});
        put(ERR_Module__ServeDao_TableCheck_Table_Lack, new String[]{"E-WS(_ServeDao)", "_ServeDao TableCheck table lack"});
        put(ERR_Module__ServeDao_TableCheck_TableInfo_Lack, new String[]{"E-WS(_ServeDao)", "_ServeDao TableCheck table info lack"});
        put(ERR_Module__ServeDao_TableCheck_TableType_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao TableCheck table type illegal"});
        put(ERR_Module__ServeDao_Insert_Column_Lack, new String[]{"E-WS(_ServeDao)", "_ServeDao Insert non-nullable column lack"});
        put(ERR_Module__ServeDao_Insert_ColumnValueLength_Limit, new String[]{"E-WS(_ServeDao)", "_ServeDao Insert column value length over limit"});
        put(ERR_Module__ServeDao_InsertBatch_Data_Empty, new String[]{"E-WS(_ServeDao)", "_ServeDao InsertBatch target value is empty"});
        put(ERR_Module__ServeDao_InsertBatch_Column_Lack, new String[]{"E-WS(_ServeDao)", "_ServeDao InsertBatch non-nullable column lack"});
        put(ERR_Module__ServeDao_InsertBatch_ColumnValueLength_Limit, new String[]{"E-WS(_ServeDao)", "_ServeDao InsertBatch column value length over limit"});
        put(ERR_Module__ServeDao_Update_ColumnValueLength_Limit, new String[]{"E-WS(_ServeDao)", "_ServeDao Update column value length over limit"});
        put(ERR_Module__ServeDao_SQLStringDeal_Template_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLStringDeal sql template is empty"});
        put(ERR_Module__ServeDao_SQLStringDeal_Replace_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLStringDeal value for replacing sql template is illegal"});
        put(ERR_Module__ServeDao_SQLExecute_Type_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLExecute type is illegal"});
        put(ERR_Module__ServeDao_SQLExecute_Exception, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLExecute exception"});
        put(ERR_Module__ServeDao_SQLExecute_Update_Fail, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLExecute update fail"});
        put(ERR_Module__ServeDao_SQLExecute_Select_Null_Error, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLExecute select null error"});
        put(ERR_Module__ServeDao_SQLExecute_Select_NotNull_Error, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLExecute select not null error"});
        put(ERR_Module__ServeDao_SQLSelect_Type_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao SQLSelect type is illegal"});
        put(ERR_Module__ServeDao_TXNStart_Exception, new String[]{"E-WS(_ServeDao)", "_ServeDao TXNStart create transaction exception"});
        put(ERR_Module__ServeDao_TXNStart_Level_Illegal, new String[]{"E-WS(_ServeDao)", "_ServeDao TXNStart transaction level illegal"});
        put(ERR_Module__ServeDao_TXNStart_Create_Already, new String[]{"E-WS(_ServeDao)", "_ServeDao TXNStart transaction already create"});
        put(ERR_Module__ServeDao_TXNEnd_Exception, new String[]{"E-WS(_ServeDao)", "_ServeDao TXNEnd end transaction exception"});
        //_DataFilling
        put(ERR_Module__DataFilling_Exception, new String[]{"E-WS(_DataFilling)", "_DataFilling exception"});
        put(ERR_Module__DataFilling_JsonPath_Illegal, new String[]{"E-WS(_DataFilling)", "_DataFilling Json path illegal"});
        put(ERR_Module__DataFilling_Session_Fail, new String[]{"E-WS(_DataFilling)", "_DataFilling session get fail"});
        //_DataCheck
        put(ERR_Module__DataCheck_Exception, new String[]{"E-WS(_DataCheck)", "_DataCheck exception"});
        put(ERR_Module__DataCheck_Check_Block, new String[]{"E-WS(_DataCheck)", "_DataCheck Check block"});
        put(ERR_Module__DataCheck_Lack_Block, new String[]{"E-WS(_DataCheck)", "_DataCheck Check lack param"});
        put(ERR_Module__DataCheck_LensLimit_Over, new String[]{"E-WS(_DataCheck)", "_DataCheck Check list over length limit"});
        put(ERR_Module__DataCheck_List_Empty, new String[]{"E-WS(_DataCheck)", "_DataCheck Check list is empty"});
        put(ERR_Module__DataCheck_JsonPath_Illegal, new String[]{"E-WS(_DataCheck)", "_DataCheck Check json path is illegal"});
        //_OperSession
        put(ERR_Module__OperSession_Exception, new String[]{"E-WS(_OperSession)", "_OperSession exception"});
        put(ERR_Module__OperSession_Action_Illegal, new String[]{"E-WS(_OperSession)", "_OperSession Action illegal"});
        put(ERR_Module__OperSession_Save_Empty, new String[]{"E-WS(_OperSession)", "_OperSession Save value is empty"});
    }};

    public static boolean IsError(ERRORCODE code){
        if(code == ERRORCODE.ERR_OK){
            return false;
        }
        return true;
    }

    public static boolean IsError(ERRORCODE code, ERRORCODE[] skipList){
        if(code == ERRORCODE.ERR_OK) {
            return false;
        } else if (skipList.length!=0){
            List<ERRORCODE> tempList = Arrays.asList(skipList);
            return !tempList.contains(ERRORCODE.ERR_Ignore) && !tempList.contains(code);
        }
        return true;
    }

    public static JSONObject GetErrorCode(ERRORCODE code, JSONObject param){
        if(param.get("message") != null){
            return param;
        }

        String[] errorCodeList = (String[]) errorCodeMaps.get(code);
        if(errorCodeList == null){
            param.put("code", "unknown");
            param.put("message", "unknown");
        } else {
            param.put("code", errorCodeList[0]);
            param.put("message", errorCodeList[1]);
        }
        return param;
    }
}