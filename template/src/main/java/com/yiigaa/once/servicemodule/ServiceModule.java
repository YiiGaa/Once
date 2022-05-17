package com.yiigaa.once.servicemodule;
import com.yiigaa.once.servicemodule.DeleteFile.DeleteFile;

import com.yiigaa.once.servicemodule.FillingParams.FillingParams;
import java.util.HashMap;

/*
* Special Note:
* When using the command "Christmas.rb Menu/MakeEngineeringNormal/AutoLinkModule@Controller/",
* Everything in this file will be updated.
* So don't add any code here
* If you need to modify this file, you need to modify it in /Christmas/Template/AutoLinkModule , and then execute the command "Christmas.rb Menu/MakeEngineeringNormal/AutoLinkModule@Controller/".
*/
public class ServiceModule {
    public static HashMap<String, Object> invokeMethod(String methodParam, HashMap<String, Object> param){
        HashMap<String, Object> returnParam =  moduleMaps.get(methodParam).start(param);
        return returnParam;
    }

    private static HashMap<String, Link> moduleMaps = new HashMap<String, Link>(){{
        put("DeleteFile", new DeleteFile());

        put("FillingParams", new FillingParams());
    }};
}
