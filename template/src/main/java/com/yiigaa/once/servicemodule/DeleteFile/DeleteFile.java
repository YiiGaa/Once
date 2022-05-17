package com.yiigaa.once.servicemodule.DeleteFile;

import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.servicecommon.COMMON_LOGGER;
import com.yiigaa.once.servicemodule.ErrorCodes;
import com.yiigaa.once.servicemodule.Link;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        put("MODULE_DeleteFile_block", new String[]{"E-SM01(DeleteFile)", "非法文件目录"});
        put("MODULE_DeleteFile_exception", new String[]{"E-SM02(DeleteFile)", "删除文件崩溃"});
######ErrorCodes end######
*/


public class DeleteFile extends Link {
    private static final ExecutorService DELETE_SERVICE = Executors.newSingleThreadExecutor();

    public static void deleteFileAsync(final File file) {
        if (file != null) {
            DELETE_SERVICE.submit(new Runnable() {
                @Override
                public void run() {
                    file.delete();
                }
            });
        }
    }

    public static int deleteFile(String deletePath, String contain, Boolean investigate) {
        int ret = 0;
        File file = new File(deletePath);
        if (!file.isDirectory()) {
            COMMON_LOGGER.INFO(null, "ServiceModule-DeleteFile delete list: "+deletePath);
            deleteFileAsync(file);
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                if(!investigate){
                    if(new File(deletePath+"/"+filelist[i]).isDirectory()){
                        continue;
                    }
                }
                if (filelist[i].contains(contain)) {
                    ret |= deleteFile(deletePath + "/" + filelist[i], contain, investigate);
                }
            }
            COMMON_LOGGER.INFO(null, "ServiceModule-DeleteFile try delete dir: "+deletePath);
            deleteFileAsync(file);
        }
        return ret;
    }

    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String deletePath = (moduleParam.get("deletePath") == null)?"":moduleParam.get("deletePath").toString();
            deletePath = (passParam.get(deletePath) == null)?deletePath:passParam.get(deletePath).toString();
            String contain = (moduleParam.get("contain") == null)?"":moduleParam.get("contain").toString();
            contain = (passParam.get(contain) == null)? contain:passParam.get(contain).toString();
            Boolean investigate = (moduleParam.get("investigate") == null)?false:Boolean.parseBoolean(moduleParam.get("investigate").toString());

            if(deletePath.indexOf(DeleteFileConfig.rootpath)!=0){
                returnParam.put("errorCode", "MODULE_DeleteFile_block");
                return returnMap;
            }
            COMMON_LOGGER.INFO(param,"ServiceModule-DeleteFile delete file," + "deletePath:"+deletePath + ",contain:" + contain);
            deleteFile(deletePath, contain, investigate);
            return returnMap;
        } catch(Exception e){
            returnParam.put("errorCode", "MODULE_DeleteFile_exception");
            returnMap.put("returnParam", returnParam);
            
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
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


