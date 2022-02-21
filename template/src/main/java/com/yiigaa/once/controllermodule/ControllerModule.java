package com.yiigaa.once.controllermodule;
import com.yiigaa.once.controllermodule.CheckHashParam.CheckHashParam;
import com.yiigaa.once.controllermodule.CheckNecessaryParam.CheckNecessaryParam;
import com.yiigaa.once.controllermodule.ErgodicGetParam.ErgodicGetParam;
import com.yiigaa.once.controllermodule.FileUpload.FileUpload;
import com.yiigaa.once.controllermodule.FillingHashParam.FillingHashParam;
import com.yiigaa.once.controllermodule.FillingParam.FillingParam;
import com.yiigaa.once.controllermodule.SessionCancel.SessionCancel;
import com.yiigaa.once.controllermodule.SessionSave.SessionSave;
import java.util.HashMap;

/* 
* Special Note: 
* When using the command "Christmas.rb Menu/MakeEngineeringNormal/AutoLinkModule@Controller/", 
* Everything in this file will be updated.
* So don't add any code here
* If you need to modify this file, you need to modify it in /Christmas/Template/AutoLinkModule , and then execute the command "Christmas.rb Menu/MakeEngineeringNormal/AutoLinkModule@Controller/".
*/
public class ControllerModule {
    public static HashMap<String, Object> invokeMethod(String methodParam, HashMap<String, Object> param){
        HashMap<String, Object> returnParam =  moduleMaps.get(methodParam).start(param);
        return returnParam;
    }

    private static HashMap<String, Link> moduleMaps = new HashMap<String, Link>(){{
        put("CheckHashParam", new CheckHashParam());
        put("CheckNecessaryParam", new CheckNecessaryParam());
        put("ErgodicGetParam", new ErgodicGetParam());
        put("FileUpload", new FileUpload());
        put("FillingHashParam", new FillingHashParam());
        put("FillingParam", new FillingParam());
        put("SessionCancel", new SessionCancel());
        put("SessionSave", new SessionSave());
    }};
}
