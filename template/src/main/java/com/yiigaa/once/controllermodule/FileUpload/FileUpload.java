package com.yiigaa.once.controllermodule.FileUpload;

import com.yiigaa.once.controllercommon.COMMON_LOGGER;
import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.controllermodule.Link;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/*
**Please do not modify the following
######LinkpackageImport start######
import com.yiigaa.once.controllermodule.FileUpload.FileUpload;
######LinkpackageImport end######

######LinkObjectImport start######
        put("FileUpload", new FileUpload());
######LinkObjectImport end######

######GradleImport start######
//FileUpload
######GradleImport end######

######ErrorCodes start######
        //FileUpload
        put("MODULE_FileUpload_FileListEmpty_block", new String[]{"E-CM01(FileUpload)", "上传文件列表为空"});
        put("MODULE_FileUpload_FileEmpty_block", new String[]{"E-CM02(FileUpload)", "上传文件为空"});
        put("MODULE_FileUpload_WriteFile_block", new String[]{"E-CM03(FileUpload)", "写入文件失败"});
        put("MODULE_FileUpload_SizeBeyond_block", new String[]{"E-CM04(FileUpload)", "文件超过指定大小"});
        put("MODULE_FileUpload_exception", new String[]{"E-CM05(FileUpload)", "上传内部崩溃"});
        put("MODULE_FileUpload_intercept_block", new String[]{"E-CM06(FileUpload)", "上传类型拦截"});
######ErrorCodes end######
*/

public class FileUpload extends Link {

    private HashMap<String, Object> doStart(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            //String uploadName = (moduleParam.get("uploadName") == null)?"":moduleParam.get("uploadName").toString();
            boolean isAsResult = (moduleParam.get("isAsResult") == null) ? false : Boolean.parseBoolean(moduleParam.get("isAsResult").toString());
            String resultKey = (moduleParam.get("resultKey") == null) ? "fileUpload" : moduleParam.get("resultKey").toString();
            long maxFileSize = (moduleParam.get("maxFileSize") == null) ? -1 : Long.parseLong(moduleParam.get("maxFileSize"));
            String formKey = (moduleParam.get("formKey") == null) ? "file" : moduleParam.get("formKey").toString();
            boolean isNecessary = (moduleParam.get("isNecessary") == null) ? true : Boolean.parseBoolean(moduleParam.get("isNecessary").toString());

            List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles(formKey);
            if (files.isEmpty()) {
                if (isNecessary) {
                    returnParam.put("errorCode", "MODULE_FileUpload_FileListEmpty_block");
                }
                passParam.put(resultKey, "");
                returnParam.put(resultKey, "");
                return returnMap;
            }

            String path = FileUploadConfig.uploadfilepath;
            System.out.println(FileUploadConfig.uploadfilepath);

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                long size = (long) file.getSize();
                if (maxFileSize != -1 && size > maxFileSize) {
                    returnParam.put("errorCode", "MODULE_FileUpload_SizeBeyond_block");
                    return returnMap;
                }

                if (file.isEmpty()) {
                    if (isNecessary) {
                        returnParam.put("errorCode", "MODULE_FileUpload_FileEmpty_block");
                    }
                    passParam.put(resultKey, "");
                    returnParam.put(resultKey, "");
                    return returnMap;
                }else{
                    String Name = String.valueOf(System.currentTimeMillis());// 默认上传文件名
                    String uploadName = (moduleParam.get("uploadName") == null)? Name:moduleParam.get("uploadName").toString();
                    String realname = (passParam.get(uploadName) == null)? uploadName:passParam.get(uploadName).toString();
                    String[] suffix = (moduleParam.get("allow")==null)? new String[]{} :moduleParam.get("allow").split(",");
                    realname = realname + fileName.substring(fileName.lastIndexOf("."));
                    File dest = new File(path + "/" + realname);
                    String filePath = path + "/" + realname;

                    String  fileSuffix = fileName.substring(fileName.lastIndexOf("."));
                    int i=0;
                    for(i=0;i<suffix.length;i++){
                        if(suffix[i].equals(fileSuffix)){
                            break;
                        }
                    }
                    if (suffix.length!=0 && i == suffix.length){
                        returnParam.put("errorCode", "MODULE_FileUpload_intercept_block");
                        return returnMap;
                    }
                    if (!dest.getParentFile().exists()) { // 判断文件父目录是否存在
                        dest.getParentFile().mkdirs();
                    }
                    try {
                        file.transferTo(dest);
                    }catch (Exception e) {
                        COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
                        returnParam.put("errorCode", "MODULE_FileUpload_WriteFile_block");
                        return returnMap;
                    }
                    if(isAsResult){
                        returnParam.put(resultKey, filePath);
                    }else {
                        passParam.put(resultKey, filePath);
                    }
                }
            }

            return returnMap;
        } catch(Exception e){
            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));

            returnParam.put("errorCode", "MODULE_FileUpload_exception");
            returnMap.put("returnParam", returnParam);
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    public HashMap<String, Object> start(HashMap<String, Object> param){
        COMMON_LOGGER.DEBUG(param, "ControllerModule-FileUpload start");
        HashMap<String, Object> returnParam = doStart(param);
        COMMON_LOGGER.DEBUG(returnParam, "ControllerModule-FileUpload end");
        return returnParam;
    }
}


