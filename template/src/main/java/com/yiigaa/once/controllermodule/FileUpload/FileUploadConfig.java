package com.yiigaa.once.controllermodule.FileUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUploadConfig {
    @Value("${controller.uploadfile.rootpath}")
    public void setUploadfilepath(String uploadfilepath) {
        FileUploadConfig.uploadfilepath = uploadfilepath;
    }

    public static String uploadfilepath;
}
