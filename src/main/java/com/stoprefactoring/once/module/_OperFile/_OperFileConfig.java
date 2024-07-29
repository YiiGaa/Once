package com.stoprefactoring.once.module._OperFile;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.module.Module;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

@Component
public class _OperFileConfig {
    public _OperFileConfig(){
        LOGGER.StraightInfo("Module-_OperFile: Load configuration");
    }

    @PostConstruct
    //This function will be called, after this module's config has been loaded
    public void Init() {
        //STEP::Register module
        LOGGER.StraightInfo("Module-_OperFile: Register module");
        Module.moduleMaps.put("_OperFile", new _OperFile());
        Module.moduleNeedCallEnd.put("_OperFile", true);

        //STEP::Init module
        LOGGER.StraightInfo("Module-_OperFile: Init module");
        _OperFileConfig.asyncExecutorHandler = new ThreadPoolExecutor(
                1,
                _OperFileConfig.poolSize,
                10000L, TimeUnit.MILLISECONDS,
                _OperFileConfig.poolQueue>0?new ArrayBlockingQueue<>(_OperFileConfig.poolQueue):new LinkedBlockingQueue<>());

        List<String> tempCheckPath = new ArrayList<>();
        if(!_OperFileConfig.checkPath.equals(""))
            tempCheckPath = new ArrayList<>(Arrays.asList(_OperFileConfig.checkPath.split(",")));
        tempCheckPath.add(_OperFileConfig.rootPath);
        _OperFileConfig.checkRootPath = tempCheckPath;
    }

    @PreDestroy
    //This function will be called, before application close
    public void Destroy(){
        //STEP::Destroy module
        LOGGER.StraightInfo("Module-_OperFile: Destroy module");
        asyncExecutorHandler.shutdown();
        try {
            if (!asyncExecutorHandler.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutorHandler.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Tasks interrupted");
            asyncExecutorHandler.shutdownNow();
        }
        LOGGER.StraightInfo("Module-_OperFile show down async thread pool");
    }

    public static ExecutorService asyncExecutorHandler;

    public static boolean CheckRootPath(String path){
        for (String checkPath : _OperFileConfig.checkRootPath) {
            if(path.startsWith(checkPath)){
                return true;
            }
        }
        return false;
    }
    private static List<String> checkRootPath;

    @Value("${module._OperFile.poolSize:20}")
    public void _poolSize(Integer value) {
        _OperFileConfig.poolSize = value;
    }
    public static Integer poolSize;

    @Value("${module._OperFile.poolQueue:10000}")
    public void _poolQueue(Integer value) {
        _OperFileConfig.poolQueue = value;
    }
    public static Integer poolQueue;

    @Value("${module._OperFile.rootPath}")
    public void _rootPath(String value) {
        _OperFileConfig.rootPath = value;
    }
    public static String rootPath;

    @Value("${module._OperFile.checkPath:}")
    public void _checkPath(String value) {
        _OperFileConfig.checkPath = value;
    }
    public static String checkPath;
}