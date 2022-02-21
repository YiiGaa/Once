package com.yiigaa.once.servicemodule.DeleteFile;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.servicecommon.COMMON_LOGGER;
import com.yiigaa.once.servicemodule.ErrorCodes;
import com.yiigaa.once.servicemodule.Link;

import java.io.File;
import java.util.HashMap;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.servicemodule.DeleteFile.DeleteFile;
######LinkpackageImport end######

######LinkObjectImport start######
        put("DeleteFile", new DeleteFile());
######LinkObjectImport end######

######GradleImport start######
//DeleteFile
######GradleImport end######

######ErrorCodes start######
        //DeleteFile
        put("MODULE_DeleteFile_block", new String[]{"E-SM01(DeleteFile)", "删除文件失败"});
        put("MODULE_DeleteFile_exception", new String[]{"E-SM02(DeleteFile)", "删除文件崩溃"});
######ErrorCodes end######
*/


public class DeleteFile extends Link {
    public static int deleteFile(String delpath,String name) {
        int ret = 0;
        File file = new File(delpath);
        if (!file.isDirectory()) {
            ret = file.getAbsoluteFile().delete()==true?0:1;
            if(ret == 1 ){
                System.out.println("Error,delete file fail:"+delpath);
            }
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                if (filelist[i].contains(name)) {
                    ret |= deleteFile(delpath + "/" + filelist[i],name);
                }
            }
        }
        return ret;

    }

    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");

        try {
            String deletepath = (moduleParam.get("deletepath") == null)?"":moduleParam.get("deletepath").toString();
            String delpath = (passParam.get(deletepath) == null)?deletepath:passParam.get(deletepath).toString();
            String name = (moduleParam.get("name") == null)?"":moduleParam.get("name").toString();
            String realname = (passParam.get(name) == null)? name:passParam.get(name).toString();

            COMMON_LOGGER.INFO(param,"ServiceModule-DeleteFile delete file," + "delpath:"+delpath + ",name:" + name);
            int ret = deleteFile(delpath,realname);
            if(ret !=0){
                COMMON_LOGGER.ERROR(param,"DELETE FAIL");
                returnParam.put("errorCode", "MODULE_DeleteFile_block");
            }
            return returnMap;
        } catch(Exception e){
            returnParam.put("errorCode", "MODULE_DeleteFile_exception");
            returnMap.put("returnParam", returnParam);
            
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ServiceModule-DeleteFile start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ServiceModule-DeleteFile end");
        return returnParam;
    }
}


