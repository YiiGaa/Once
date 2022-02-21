package com.yiigaa.once.dao;

import java.util.HashMap;

public class DataBaseFormConfig {
//    static private HashMap<String, Boolean[]> formName = new HashMap<String, Boolean[]>(){{
//        put("key",new Boolean[]{ is primary key, is can as result, is can save session, is insert '' });
//    }};

    //工程表
    static private HashMap<String, Boolean[]> t_test = new HashMap<String, Boolean[]>(){{
        put("test_id",new Boolean[]{ true, true, false, false });
        put("test_name",new Boolean[]{ false, true, false, false });
    }};


    static public HashMap<String, HashMap<String, Boolean[]>> fromMaps= new HashMap<String, HashMap<String, Boolean[]>>(){{
        put("t_test", t_test);
    }};
}
