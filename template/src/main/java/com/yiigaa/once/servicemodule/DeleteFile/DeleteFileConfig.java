package com.yiigaa.once.servicemodule.DeleteFile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeleteFileConfig {
    @Value("${service.deletefile.rootpath}")
    public void setRootpath(String rootpath) {
       DeleteFileConfig.rootpath = rootpath;
    }

    public static String rootpath;
}
