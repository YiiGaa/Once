package main.module._OperFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.common.ERRORCODE;
import main.common.APIINIT;
import main.common.LOGGER;
import main.common.TOOLS;
import main.module.Link;

public class _OperFile extends Link {
    public static ThreadLocal<ArrayList<HashMap<String, Object>>> rollBackList = new ThreadLocal<>();

    private ERRORCODE Delete_Excute(File fileHandler, Boolean isSync, Boolean isDeleteErrorInterrupt){
        ERRORCODE result = ERRORCODE.ERR_OK;
        boolean ret = false;
        if(isSync)
            LOGGER.DEBUG("Module-_OperFile Delete file sync, path:"+fileHandler.getAbsolutePath());
        else
            LOGGER.StraightDebug("Module-_OperFile Delete file async, path:"+fileHandler.getAbsolutePath());

        //WHEN::file
        if(fileHandler.isFile()){
            ret = fileHandler.delete();
            if(ret)
                return ERRORCODE.ERR_OK;
            if(isSync)
                LOGGER.ERROR("Module-_OperFile Delete file sync fail, path:"+fileHandler.getAbsolutePath());
            else
                LOGGER.StraightError("Module-_OperFile Delete file async fail, path:"+fileHandler.getAbsolutePath());
            return ERRORCODE.ERR_Module__OperFile_Delete_File_Fail;
        }

        //WHEN::directory
        if(fileHandler.isDirectory()){
            //STEP-IN::Traverse directory
            File[] innerFiles = fileHandler.listFiles();
            ERRORCODE errorMark = ERRORCODE.ERR_OK;
            if (innerFiles != null) {
                for (File item : innerFiles) {
                    //STEP-IN-IN::Deal inner items
                    if(".".equals(item.getName())||"..".equals(item.getName())){
                        continue;
                    }
                    result = Delete_Excute(item, isSync, isDeleteErrorInterrupt);
                    //STEP-IN-IN::Check whether continue when error occur
                    if(ERRORCODE.IsError(result) && isDeleteErrorInterrupt)
                        return result;
                    else if(ERRORCODE.IsError(result))
                        errorMark = result;
                }
            }

            //STEP-IN::Delete empty directory
            result = errorMark;
            if(!ERRORCODE.IsError(result)) {
                ret = fileHandler.delete();
                if(ret)
                    return ERRORCODE.ERR_OK;
                if(isSync)
                    LOGGER.ERROR("Module-_OperFile Delete empty directory sync fail, path:"+fileHandler.getAbsolutePath());
                else
                    LOGGER.StraightError("Module-_OperFile Delete empty directory async fail, path:"+fileHandler.getAbsolutePath());
                return ERRORCODE.ERR_Module__OperFile_Delete_Directory_Fail;
            }
        }

        return result;
    }

    private ERRORCODE Delete(JsonNode moduleParam, JsonNode passParam, ERRORCODE result){
        String _path = TOOLS.ReadParam("_path", "", moduleParam, passParam);
        Boolean _isNullError = TOOLS.ReadParam("_isNullError", true, moduleParam, passParam);
        Boolean _isSync = TOOLS.ReadParam("_isSync", false, moduleParam, passParam);
        Boolean _isDeleteErrorInterrupt = TOOLS.ReadParam("_isDeleteErrorInterrupt", false, moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperFile Delete param, _path:" + _path);
        LOGGER.DEBUG("Module-_OperFile Delete param, _isNullError:" + _isNullError);
        LOGGER.DEBUG("Module-_OperFile Delete param, _isSync:" + _isSync);
        LOGGER.DEBUG("Module-_OperFile Delete param, _isDeleteErrorInterrupt:" + _isDeleteErrorInterrupt);

        //STEP::Require path is not empty and starts with _OperFileConfig.rootPath
        if(_path.isEmpty()){
            LOGGER.DEBUG("Module-_OperFile Delete path is empty");
            return _isNullError?ERRORCODE.ERR_Module__OperFile_Delete_Path_Empty:ERRORCODE.ERR_OK;
        }
        if(!_OperFileConfig.CheckRootPath(_path)){
            LOGGER.DEBUG("Module-_OperFile Delete path is not start with rootPath, _path:"+_path);
            return ERRORCODE.ERR_Module__OperFile_Delete_Path_Illegal;
        }

        //STEP::Get path file handler
        File pathHandler = new File(_path);
        if(!pathHandler.exists()){
            LOGGER.DEBUG("Module-_OperFile Delete path is not exists, _path:"+_path);
            return ERRORCODE.ERR_Module__OperFile_Delete_Path_Lack;
        }

        try {
            //STEP::Delete target
            if (_isSync) {
                LOGGER.INFO("Module-_OperFile Delete file sync, path:" + pathHandler.getAbsolutePath());
                result = Delete_Excute(pathHandler, _isSync, _isDeleteErrorInterrupt);
            } else {
                LOGGER.INFO("Module-_OperFile Delete file async(other thread run), path:" + pathHandler.getAbsolutePath());
                _OperFileConfig.asyncExecutorHandler.execute(() -> {
                    LOGGER.StraightInfo("Module-_OperFile Delete file async execute, path:" + pathHandler.getAbsolutePath());
                    ERRORCODE ret = Delete_Excute(pathHandler, _isSync, _isDeleteErrorInterrupt);
                });
            }
        } catch (Exception e){
            LOGGER.ERROR("Module-_OperFile Delete exception, path:"+pathHandler.getAbsolutePath()+",exception:"+TOOLS.GetExceptionInfo(e));
            return ERRORCODE.ERR_Module__OperFile_Delete_Exception;
        }

        return result;
    }

    private ERRORCODE UploadRollBack(ObjectNode moduleParam, ObjectNode passParam, HttpServletRequest request, ERRORCODE result){
        Boolean _isErrorRollBack = TOOLS.ReadParam("_isErrorRollBack", true, moduleParam, passParam);
        Boolean _isAutoRollBack = TOOLS.ReadParam("_isAutoRollBack", true, moduleParam, passParam);
        ArrayNode _inner_uploadList = TOOLS.ReadParam("_inner_uploadList", TOOLS.JsonInitArray(), moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperFile UploadRollBack param, _isErrorRollBack:" + _isErrorRollBack);
        LOGGER.DEBUG("Module-_OperFile UploadRollBack param, _isAutoRollBack:" + _isAutoRollBack);
        LOGGER.DEBUG("Module-_OperFile UploadRollBack param, _inner_uploadList:" + _inner_uploadList);

        //WHEN::Empty list
        if(_inner_uploadList.isEmpty()){
            return result;
        }

        //WHEN::Error
        if(ERRORCODE.IsError(result) && _isErrorRollBack){
            try{
                LOGGER.INFO("Module-_OperFile UploadRollBack delete file async(other thread run), list:" + _inner_uploadList.toString());
                _OperFileConfig.asyncExecutorHandler.execute(() -> {
                    LOGGER.StraightInfo("Module-_OperFile UploadRollBack delete file async execute, list:" + _inner_uploadList.toString());
                    for(Object item:_inner_uploadList){
                        if(item instanceof String) {
                            File pathHandler = new File((String)item);
                            if(pathHandler.exists()) {
                                ERRORCODE ret = Delete_Excute(pathHandler, false, false);
                            }
                        }
                    }
                });
            } catch (Exception e){
                LOGGER.ERROR("Module-_OperFile UploadRollBack exception, list:" + _inner_uploadList.toString()+",exception:"+TOOLS.GetExceptionInfo(e));
                return ERRORCODE.ERR_Module__OperFile_UploadRollBack_Exception;
            }
        }

        //WHEN::Not Error, mark info for
        if(!ERRORCODE.IsError(result) && _isAutoRollBack) {
            String position = LOGGER.GetCallPosition();
            LOGGER.DEBUG("Module-_OperFile UploadRollBack mark for auto rollback, position:"+position+", list:" + _inner_uploadList.toString());
            if(rollBackList.get()==null) {
                ArrayList<HashMap<String, Object>> rollBackMap = new ArrayList<>();
                HashMap<String, Object> rollBackItem = new HashMap<>();
                rollBackItem.put("position", "API");
                rollBackItem.put("rollback", TOOLS.JsonInitArray());
                rollBackMap.add(rollBackItem);
                rollBackList.set(rollBackMap);
            }

            ArrayList<HashMap<String, Object>> rollBackMap = rollBackList.get();
            HashMap<String, Object> rollBackItem = rollBackMap.getLast();
            if(rollBackItem==null || !((String)rollBackItem.get("position")).equals(position)){
                HashMap<String, Object> rollBackItemNew = new HashMap<>();
                rollBackItemNew.put("position", position);
                rollBackItemNew.put("rollback", _inner_uploadList);
                rollBackMap.add(rollBackItemNew);
            } else {
                ((ArrayNode)rollBackItem.get("rollback")).addAll(_inner_uploadList);
            }
        }

        return result;
    }

    private ERRORCODE Upload(ObjectNode moduleParam, ObjectNode passParam, HttpServletRequest request, ERRORCODE result){
        //STEP::Get setting
        String _targetKey = TOOLS.ReadParam("_targetKey", "file", moduleParam, passParam);
        Boolean _isNullError = TOOLS.ReadParam("_isNullError", true, moduleParam, passParam);
        Integer _maxCount = TOOLS.ReadParam("_maxCount", -1, moduleParam, passParam);
        Long _maxFileSize = TOOLS.ReadParam("_maxFileSize", -1L, moduleParam, passParam);
        String _AllowSuffix = TOOLS.ReadParam("_AllowSuffix", (String)null, moduleParam, passParam);
        String _targetFileName = TOOLS.ReadParam("_targetFileName", UUID.randomUUID().toString().replace("-", ""), moduleParam, passParam);
        String _resultPathKey = TOOLS.ReadParam("_resultPathKey", _targetKey, moduleParam, passParam);
        String _resultOriginalNameKey = TOOLS.ReadParam("_resultOriginalNameKey", "", moduleParam, passParam);
        String _resultFileSizeKey = TOOLS.ReadParam("_resultFileSizeKey", "", moduleParam, passParam);
        Boolean _isExistCover = TOOLS.ReadParam("_isExistCover", false, moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperFile Upload param, _targetKey:" + _targetKey);
        LOGGER.DEBUG("Module-_OperFile Upload param, _isNullError:" + _isNullError);
        LOGGER.DEBUG("Module-_OperFile Upload param, _maxCount:" + _maxCount);
        LOGGER.DEBUG("Module-_OperFile Upload param, _maxFileSize:" + _maxFileSize);
        LOGGER.DEBUG("Module-_OperFile Upload param, _AllowSuffix:" + _AllowSuffix);
        LOGGER.DEBUG("Module-_OperFile Upload param, _targetFileName:" + _targetFileName);
        LOGGER.DEBUG("Module-_OperFile Upload param, _resultPathKey:" + _resultPathKey);
        LOGGER.DEBUG("Module-_OperFile Upload param, _resultOriginalNameKey:" + _resultOriginalNameKey);
        LOGGER.DEBUG("Module-_OperFile Upload param, _resultFileSizeKey:" + _resultFileSizeKey);
        LOGGER.DEBUG("Module-_OperFile Upload param, _isExistCover:" + _isExistCover);

        //STEP::Check whether it is lack file
        if(!APIINIT.GetMultipartResolver().isMultipart(request)){
            LOGGER.DEBUG("Module-_OperFile Upload request data has not file");
            if(_isNullError)
                result = ERRORCODE.ERR_Module__OperFile_Upload_File_Lack;
            return result;
        }
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles(_targetKey);
        if (files.isEmpty()) {
            LOGGER.DEBUG("Module-_OperFile Upload request data has file list, but empty");
            if(_isNullError)
                result = ERRORCODE.ERR_Module__OperFile_Upload_File_Lack;
            return result;
        }

        //STEP::Check whether number of files is over max count
        if(_maxCount>0 && files.size()>_maxCount){
            LOGGER.DEBUG("Module-_OperFile Upload number of files is over limit, _maxCount:"+_maxCount+",count:"+files.size());
            return ERRORCODE.ERR_Module__OperFile_Upload_FileCount_Limit;
        }

        //STEP::Upload files
        int index = -1;
        boolean isAddNum = files.size()>1;
        ArrayNode uploadList = TOOLS.JsonInitArray();
        for (MultipartFile fileHandler : files) {
            index++;
            //STEP-IN::Check file size
            Long fileSize = fileHandler.getSize();
            if (_maxFileSize > 0 && _maxFileSize < fileSize) {
                LOGGER.DEBUG("Module-_OperFile Upload size of files is over limit, _maxFileSize:" + _maxFileSize + ",fileSize:" + fileSize);
                return ERRORCODE.ERR_Module__OperFile_Upload_FileSize_Limit;
            }

            //STEP-IN::Check file is empty
            if (fileHandler.isEmpty()) {
                LOGGER.DEBUG("Module-_OperFile Upload file empty(pre-check)");
                return ERRORCODE.ERR_Module__OperFile_Upload_File_FilePreCheck_Empty;
            }

            //STEP-IN::Get file suffix
            String fileName = fileHandler.getOriginalFilename();
            String fileSuffix = "";
            int suffixIndex = fileName.lastIndexOf('.');
            if (suffixIndex > 0) {
                fileSuffix = fileName.substring(suffixIndex);
            }

            //STEP-IN::Check fileName
            if (fileName.contains("/") || fileName.contains("\\")) {
                LOGGER.DEBUG("Module-_OperFile Upload file name illegal, contain '\\' or '/'");
                return ERRORCODE.ERR_Module__OperFile_Upload_FileName_Illegal;
            }

            //STEP-IN::Check whether file suffix is allow
            if (_AllowSuffix != null) {
                Boolean isNot = false;
                if (_AllowSuffix.startsWith("not##")) {
                    _AllowSuffix = _AllowSuffix.substring("not##".length());
                    isNot = true;
                }
                String[] tempSuffixArray = _AllowSuffix.split(Pattern.quote("|"));
                Boolean match = false;
                for (String tempSuffix : tempSuffixArray) {
                    if (tempSuffix.equals(fileSuffix)) {
                        match = true;
                        break;
                    }
                }
                if ((!isNot && !match) || (isNot && match)) {
                    LOGGER.DEBUG("Module-_OperFile Upload file suffix is not allow, _AllowSuffix:" + _AllowSuffix + ",fileSuffix:" + fileSuffix);
                    return ERRORCODE.ERR_Module__OperFile_Upload_FileSuffix_Block;
                }
            }

            //STEP-IN::Check whether file is exist
            String destFileName = isAddNum?_targetFileName+"_"+index+fileSuffix:_targetFileName+fileSuffix;
            File destPath = new File(_OperFileConfig.rootPath, destFileName);
            if(destPath.exists() && !_isExistCover){
                LOGGER.DEBUG("Module-_OperFile Upload file exists(pre-check), destPath:"+destPath.getPath());
                return ERRORCODE.ERR_Module__OperFile_Upload_FilePreCheck_Exist;
            }
            uploadList.add(destFileName);
        }

        //STEP::Check whether list size is same
        if(uploadList.size() != files.size()){
            LOGGER.ERROR("Module-_OperFile Upload length of uploadList is incorrect, filesSize:"+files.size()+",uploadListSize:"+uploadList.size());
            return ERRORCODE.ERR_Module__OperFile_Upload_UploadListSize_Incorrect;
        }

        index = -1;
        ArrayNode resultPathArray = TOOLS.JsonInitArray();
        ArrayNode resultOriginalNameArray = TOOLS.JsonInitArray();
        ArrayNode resultFileSizeArray = TOOLS.JsonInitArray();
        ArrayNode inner_uploadList = TOOLS.JsonInitArray();
        moduleParam.set("_inner_uploadList", inner_uploadList);
        for (MultipartFile fileHandler : files) {
            index++;

            //STEP-IN::Check file is empty
            if (fileHandler.isEmpty()) {
                LOGGER.DEBUG("Module-_OperFile Upload file empty");
                return ERRORCODE.ERR_Module__OperFile_Upload_File_Empty;
            }

            //STEP-IN::Check whether file is exist
            String destFileName = (String) uploadList.get(index).asString();
            File destPath = new File(_OperFileConfig.rootPath, destFileName);
            String destPathStr = destPath.getPath();
            if(destPath.exists() && !_isExistCover){
                LOGGER.ERROR("Module-_OperFile Upload file exists, destPath:"+destPathStr);
                return ERRORCODE.ERR_Module__OperFile_Upload_File_Exist;
            }

            //STEP-IN::Create parent dirs
            if (!destPath.getParentFile().exists()) {
                Boolean isSuccess = destPath.getParentFile().mkdirs();
                if(!isSuccess){
                    LOGGER.ERROR("Module-_OperFile Upload create parent dirs fail, parentPath:"+destPath.getParentFile().getPath());
                    return ERRORCODE.ERR_Module__OperFile_Upload_CreateParentDirs_Fail;
                }
            }

            //STEP-IN::Upload file
            try {
                LOGGER.INFO("Module-_OperFile Upload file path:"+destPathStr);
                fileHandler.transferTo(destPath);
            } catch (Exception e){
                LOGGER.ERROR("Module-_OperFile Upload exception, destPath:"+destPathStr+",exception:"+TOOLS.GetExceptionInfo(e));
                return ERRORCODE.ERR_Module__OperFile_Upload_Exception;
            }

            //STEP-IN::Mark result
            resultPathArray.add(destPathStr);
            resultOriginalNameArray.add(fileHandler.getOriginalFilename());
            resultFileSizeArray.add(fileHandler.getSize());

            inner_uploadList.add(destPath.getAbsolutePath());
        }

        //STEP::Put result in data pool
        ObjectNode resultTarget = passParam;
        if (resultPathArray.size() == 1) {
            if(!_resultPathKey.isEmpty())
                resultTarget.set(_resultPathKey,  resultPathArray.get(0));
            if(!_resultOriginalNameKey.isEmpty())
                resultTarget.set(_resultOriginalNameKey, resultOriginalNameArray.get(0));
            if(!_resultFileSizeKey.isEmpty())
                resultTarget.set(_resultFileSizeKey, resultFileSizeArray.get(0));
        } else {
            for (Integer i = 0; i < resultPathArray.size(); i++) {
                if(!_resultPathKey.isEmpty())
                    resultTarget.set(_resultPathKey+i,  resultPathArray.get(0));
                if(!_resultOriginalNameKey.isEmpty())
                    resultTarget.set(_resultOriginalNameKey+i, resultOriginalNameArray.get(0));
                if(!_resultFileSizeKey.isEmpty())
                    resultTarget.set(_resultFileSizeKey+i, resultFileSizeArray.get(0));
            }
        }

        //STEP::Clear request param, it will be lack when get the same _targetKey in next time
        files.clear();

        return result;
    }

    private ERRORCODE Move(JsonNode moduleParam, JsonNode passParam, ERRORCODE result){
        String _sourcePath = TOOLS.ReadParam("_sourcePath", "", moduleParam, passParam);
        String _targetPath = TOOLS.ReadParam("_targetPath", "", moduleParam, passParam);
        Boolean _isSourceNullError = TOOLS.ReadParam("_isSourceNullError", true, moduleParam, passParam);
        Boolean _isTargetCover = TOOLS.ReadParam("_isTargetCover", false, moduleParam, passParam);
        Boolean _isSync = TOOLS.ReadParam("_isSync", false, moduleParam, passParam);
        LOGGER.DEBUG("Module-_OperFile Move param, _sourcePath:" + _sourcePath);
        LOGGER.DEBUG("Module-_OperFile Move param, _targetPath:" + _targetPath);
        LOGGER.DEBUG("Module-_OperFile Move param, _isSourceNullError:" + _isSourceNullError);
        LOGGER.DEBUG("Module-_OperFile Move param, _isTargetCover:" + _isTargetCover);
        LOGGER.DEBUG("Module-_OperFile Move param, _isSync:" + _isSync);

        //STEP::Require _sourcePath is not empty and starts with _OperFileConfig.rootPath
        if(_sourcePath.isEmpty()){
            LOGGER.DEBUG("Module-_OperFile Move sourcePath is empty");
            return _isSourceNullError?ERRORCODE.ERR_Module__OperFile_Move_SourcePath_Empty:ERRORCODE.ERR_OK;
        }
        if(!_OperFileConfig.CheckRootPath(_sourcePath)){
            LOGGER.DEBUG("Module-_OperFile Move sourcePath is not start with rootPath, _sourcePath:"+_sourcePath);
            return ERRORCODE.ERR_Module__OperFile_Move_SourcePath_Illegal;
        }

        //STEP::Require _targetPath is not empty and starts with _OperFileConfig.rootPath
        if(_targetPath.isEmpty()){
            LOGGER.DEBUG("Module-_OperFile Move targetPath is empty");
            return ERRORCODE.ERR_Module__OperFile_Move_TargetPath_Empty;
        }
        if(_targetPath.equals(_sourcePath)){
            LOGGER.DEBUG("Module-_OperFile Move targetPath and sourcePath is same");
            return ERRORCODE.ERR_Module__OperFile_Move_TargetSource_Same;
        }
        if(!_OperFileConfig.CheckRootPath(_targetPath)){
            LOGGER.DEBUG("Module-_OperFile Move targetPath is not start with rootPath, _targetPath:"+_targetPath);
            return ERRORCODE.ERR_Module__OperFile_Move_TargetPath_Illegal;
        }

        //STEP::Get source file handler
        File sourceHandler = new File(_sourcePath);
        if(!sourceHandler.exists()){
            LOGGER.DEBUG("Module-_OperFile Move sourcePath is not exists, _sourcePath:"+_sourcePath);
            if(_isSourceNullError)
                return ERRORCODE.ERR_Module__OperFile_Move_SourcePath_Lack;
            return result;
        }

        //STEP::Get target file handler
        File targetHandler = new File(_targetPath);
        if(targetHandler.exists()){
            if(!_isTargetCover){
                LOGGER.DEBUG("Module-_OperFile Move targetPath is exists, _targetPath:"+_targetPath);
                return ERRORCODE.ERR_Module__OperFile_Move_TargetPath_Exist;
            }else if(targetHandler.isDirectory()){
                LOGGER.DEBUG("Module-_OperFile Move targetPath is exists, and directory can not be covered, _targetPath:"+_targetPath);
                return ERRORCODE.ERR_Module__OperFile_Move_TargetPath_CoverDirs_Fail;
            }
        }

        try {
            //STEP::Delete target
            if (_isSync) {
                LOGGER.INFO("Module-_OperFile Move file sync, targetPath:" + targetHandler.getAbsolutePath()+", sourcePath:" + sourceHandler.getAbsolutePath());
                Files.createDirectories(targetHandler.toPath().getParent());
                Files.move(sourceHandler.toPath(), targetHandler.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.INFO("Module-_OperFile Move file async(other thread run), targetPath:" + targetHandler.getAbsolutePath()+", sourcePath:" + sourceHandler.getAbsolutePath());
                _OperFileConfig.asyncExecutorHandler.execute(() -> {
                    LOGGER.StraightInfo("Module-_OperFile Move file async execute, targetPath:" + targetHandler.getAbsolutePath()+", sourcePath:" + sourceHandler.getAbsolutePath());
                    try {
                        Files.createDirectories(targetHandler.toPath().getParent());
                        Files.move(sourceHandler.toPath(), targetHandler.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        LOGGER.StraightError("Module-_OperFile Move file async exception, targetPath:" + targetHandler.getAbsolutePath()+", sourcePath:" + sourceHandler.getAbsolutePath());
                    }
                });
            }
        } catch (Exception e){
            LOGGER.ERROR("Module-_OperFile Move exception:"+TOOLS.GetExceptionInfo(e));
            return ERRORCODE.ERR_Module__OperFile_Move_Exception;
        }

        return result;
    }

    private ERRORCODE DoStart(ObjectNode moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        ObjectNode passParam = (ObjectNode) dataPool.get("passParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            String _action = TOOLS.ReadParam("_action", "", moduleParam, passParam);

            switch (_action){
                case "upload":
                    result = Upload(moduleParam, passParam, request, result);
                    result = UploadRollBack(moduleParam, passParam, request, result);
                    break;
                case "delete":
                    result = Delete(moduleParam, passParam, result);
                    break;
                case "move":
                    result = Move(moduleParam, passParam, result);
                    break;
                default:
                    result = ERRORCODE.ERR_Module__OperFile_Action_Illegal;
                    break;
            }

        } catch (Exception e) {
            LOGGER.ERROR("Module-_OperFile exception:"+TOOLS.GetExceptionInfo(e), moduleParam, dataPool);
            result = ERRORCODE.ERR_Module__OperFile_Exception;
        }

        return result;
    }

    @Override
    public ERRORCODE Start(ObjectNode moduleParam, HashMap<String, Object> dataPool){
        LOGGER.DEBUG("Module-_OperFile start", moduleParam, dataPool);
        ERRORCODE result = ERRORCODE.ERR_OK;
        result = DoStart(moduleParam, dataPool, result);
        LOGGER.DEBUG("Module-_OperFile end", moduleParam, dataPool);
        return result;
    }

    @Override
    public ERRORCODE End(HashMap<String, Object> dataPool, ERRORCODE result){
        LOGGER.DEBUG("Module-_OperFile End start", dataPool);
        try{
            if(rollBackList.get()!=null){
                //STEP::Get rollBackArray
                String position = LOGGER.GetCallPosition();
                ArrayList<HashMap<String, Object>> rollBackMap = rollBackList.get();
                HashMap<String, Object> rollBackItem = rollBackMap.getLast();

                //STEP::Try roll back
                if(rollBackItem!=null && rollBackItem.get("position").equals(position)) {
                    if(ERRORCODE.IsError(result)) {
                        ArrayNode rollBackArray = ((ArrayNode)rollBackItem.get("rollback")).deepCopy();
                        try {
                            LOGGER.INFO("Module-_OperFile End rollback file async(other thread run), position:"+position+", list:" + rollBackArray.toString());
                            _OperFileConfig.asyncExecutorHandler.execute(() -> {
                                LOGGER.StraightInfo("Module-_OperFile End rollback file async execute, list:" + rollBackArray.toString());
                                try{
                                    for (JsonNode item : rollBackArray) {
                                        if (item.isString()) {
                                            File pathHandler = new File(item.asString());
                                            if (pathHandler.exists()) {
                                                ERRORCODE ret = Delete_Excute(pathHandler, false, false);
                                                if(ERRORCODE.IsError(ret))
                                                    LOGGER.StraightError("Module-_OperFile End rollback file async execute fail, path:" + item.asString());
                                            }
                                        }
                                    }
                                } catch(Exception e){
                                    LOGGER.StraightError("Module-_OperFile End rollback file async execute exception:" + TOOLS.GetExceptionInfo(e));
                                }
                            });
                        } catch (Exception e) {
                            LOGGER.ERROR("Module-_OperFile End rollback file async(other thread run) exception:" + TOOLS.GetExceptionInfo(e));
                            result = ERRORCODE.ERR_Module__OperFile_End_Exception;
                        }
                    }else if(!position.equals("API")){
                        HashMap<String, Object> rollBackItemLast = rollBackMap.get(rollBackMap.size()-2);
                        ArrayNode rollBackArrayLast = ((ArrayNode)rollBackItemLast.get("rollback"));
                        rollBackArrayLast.addAll(((ArrayNode)rollBackItem.get("rollback")));
                    }
                    rollBackMap.removeLast();
                }

                //STEP::Try remove rollBackList
                if(position.equals("API")){
                    rollBackList.remove();
                }
            }
        }catch (Exception e){LOGGER.ERROR("Module-_OperFile End for clean exception:"+ TOOLS.GetExceptionInfo(e), dataPool);}
        LOGGER.DEBUG("Module-_OperFile End end", dataPool);
        return result;
    }
}