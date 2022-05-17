package com.yiigaa.once.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiigaa.once.controllermodule.ErrorCodes;
import com.yiigaa.once.servicecommon.COMMON_LOGGER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*
**Please do not modify the following
######GradleImport start######
//DataBaseDao
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
######GradleImport end######

######ErrorCodes start######
        //DataBaseDao
        put("DAO_InsertInDb_exception", new String[]{"E-SD01(Dao)", "数据库insert崩溃"});
        put("DAO_BatchInsertInDb_exception", new String[]{"E-SD01(Dao)", "数据库batchInsert崩溃"});
        put("DAO_DeleteInDb_exception", new String[]{"E-SD02(Dao)", "数据库delete崩溃"});
        put("DAO_UpdateInDb_exception", new String[]{"E-SD03(Dao)", "数据库update崩溃"});
        put("DAO_Search_Block", new String[]{"E-SD04(Dao)", "数据search失败"});
        put("DAO_SearchInDb_exception", new String[]{"E-SD05(Dao)", "数据库search崩溃"});
        put("DAO_ExcuteUpdate_block", new String[]{"E-SD06(Dao)", "数据库执行更新失败"});
        put("DAO_ExcuteUpdate_exception", new String[]{"E-SD07(Dao)", "数据库执行更新崩溃"});
        put("DAO_ExcuteSearch_Exception", new String[]{"E-SD08(Dao)", "数据库执行更新崩溃"});
        put("DAO_Order_Block", new String[]{"E-SD09(Dao)", "非法Order"});
######ErrorCodes end######
*/

@Component
@Repository("DataBase")
public class DataBaseDao {
    @Autowired
    private JdbcTemplate jdbcTemplate ;

    private HashMap<String,Object> updateDb(String sqlParam){
        COMMON_LOGGER.INFO(null,"Dao update, sqlParam:"+sqlParam);
        HashMap<String, Object> returnParam = new HashMap<String,Object>();
        int ret = 0;
        try{

            ret = jdbcTemplate.update(sqlParam);
            if(ret == -1){
                returnParam.put("errorCode","DAO_ExcuteUpdate_block");
            } else {
                returnParam.put("errorCode","Success");
            }
        } catch (Exception e){
            e.printStackTrace();
            returnParam.put("errorCode", "DAO_ExcuteUpdate_exception");
        }
        return returnParam;
    }

    private HashMap<String,Object> searchDb(String sqlParam, int page, int maxCountInPage, boolean isAllGet, boolean isNullError, boolean isNotNullError){
        HashMap<String, Object> returnParams = new HashMap<String, Object>();

        try{
            String sqlForRow = "SELECT count(*) "+ sqlParam.substring(sqlParam.toLowerCase().indexOf("from"));
            COMMON_LOGGER.INFO(null,"Dao search Count, sqlParam:"+sqlForRow);
            int rowCount = jdbcTemplate.queryForObject(sqlForRow, Integer.class);


            //List list = query.getResultList();
            if(isNullError && (rowCount == 0)){
                returnParams.put("errorCode", "DAO_Search_Block");
                return returnParams;
            }

            if(isNotNullError && (rowCount != 0)){
                returnParams.put("errorCode", "DAO_Search_Block");
                return returnParams;
            }

            JSONArray returnList = new JSONArray();
            if(rowCount > 0) {
                if (!isAllGet) {
                    if(sqlParam.indexOf("limit")==-1) {
                        sqlParam += " limit " + ((page - 1) * maxCountInPage) + "," + maxCountInPage;
                    }
                }
                COMMON_LOGGER.INFO(null,"Dao search, sqlParam:"+sqlParam);
                List list = jdbcTemplate.queryForList(sqlParam);
                for (int i = 0; i < list.size(); i++) {
                    returnList.add(new JSONObject((Map<String, Object>) list.get(i)));
                }
            }
            returnParams.put("resultList", returnList);
            returnParams.put("resultCount", rowCount);
        }catch(Exception e){
            returnParams.put("errorCode", "DAO_ExcuteSearch_Exception");
            COMMON_LOGGER.ERROR(returnParams, ErrorCodes.getErrorInfoFromException(e));
        }
        return returnParams;
    }

    private HashMap<String, Object> insertInDb(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String query = (moduleParam.get("query") == null)?"":moduleParam.get("query").toString();
            boolean isReplace = (moduleParam.get("isReplace") == null)?false:Boolean.parseBoolean(moduleParam.get("isReplace").toString());

            if(query.compareTo("")==0) {
                String form_String = moduleParam.get("form").toString();
                String keys = "";
                String values = "";
                HashMap<String, Boolean[]> formData = DataBaseFormConfig.fromMaps.get(form_String);
                for (Map.Entry<String, Boolean[]> entry : formData.entrySet()) {
                    String key = entry.getKey();
                    if (passParam.get(key) == null && entry.getValue()[3]) {
                        keys += key+",";
                        values += "'',";
                    } else if (passParam.get(key) != null) {
                        keys += key + ",";
                        values += "'" + passParam.get(key).toString().replace("'", "\\'") + "',";
                    }
                }
                String Control = isReplace==true?"REPLACE":"INSERT";
                query = Control+" INTO " + form_String + " (" + keys.substring(0, keys.length() - 1) + ") VALUES (" + values.substring(0, values.length() - 1) + ")";
            } else {
                for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                    query = query.replace("@" + entry.getKey() + "@", entry.getValue().toString().replace("'", "\\'"));
                }
            }

            HashMap<String,Object> excuteResult = updateDb(query);
            if(excuteResult.get("errorCode")!=null){
                returnParam.put("errorCode", excuteResult.get("errorCode"));
            }

            return returnMap;
        } catch(Exception e){
            returnParam.put("errorCode", "DAO_InsertInDb_exception");
            returnMap.put("returnParam", returnParam);

            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    private HashMap<String, Object> batchInsertInDb(HashMap<String, Object> param) {
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest) param.get("httpRequest");

        try {
            String query = (moduleParam.get("query") == null) ? "" : moduleParam.get("query").toString();
            String targetKey = (moduleParam.get("targetKey") == null) ? "" : moduleParam.get("targetKey").toString();
            JSONArray jsonArray = passParam.getJSONArray(targetKey);
            Iterator<Object> iterator = jsonArray.iterator();
            if (query.compareTo("") == 0) {
                String form_String = moduleParam.get("form").toString();
                HashMap<String, Boolean[]> formData = DataBaseFormConfig.fromMaps.get(form_String);
                JSONObject firstObject = (JSONObject) jsonArray.get(0);
                Set<String> firstKeys = firstObject.keySet();
                StringBuilder sqlValueStringBuilder = new StringBuilder();
                StringBuilder sqlKeyStringBuilder = new StringBuilder();
                StringBuilder tempSqlKeyStringBuilder = new StringBuilder();
                String tempsqlValueString = "";
                for (Map.Entry<String, Boolean[]> entry : formData.entrySet()) {
                    String entryKey = entry.getKey();
                    if (firstKeys.contains(entryKey)) {
                        sqlKeyStringBuilder.append(entryKey).append(",");
                    } else if (entry.getValue()[3]) {
                        tempSqlKeyStringBuilder.append(entryKey).append(",");
                        tempsqlValueString += "'',";
                    }
                }

                sqlKeyStringBuilder.append(tempSqlKeyStringBuilder).insert(0, "(");
                sqlKeyStringBuilder.deleteCharAt(sqlKeyStringBuilder.length() - 1);
                if (!tempsqlValueString.equals("")) {
                    tempsqlValueString = tempsqlValueString.substring(0, tempsqlValueString.length() - 1);
                }
                sqlKeyStringBuilder.append(")");

                while (iterator.hasNext()) {
                    String tempSql = "";
                    StringBuilder tempSringBuilder = new StringBuilder();
                    JSONObject values = (JSONObject) iterator.next();

                    for (String key : firstKeys) {
                        String valuesString = values.getString(key).replace("'", "\\'");
                        tempSql += "'" + valuesString + "',";
                    }

                    if (tempsqlValueString.equals("")){
                        tempSql=tempSql.substring(0,tempSql.length()-1);
                        tempSringBuilder.append(tempSql);
                    }else{
                        tempSringBuilder.append(tempSql);
                        tempSringBuilder.append(tempsqlValueString);
                    }

                    tempSringBuilder.insert(0, "(");
                    tempSringBuilder.append("),");
                    sqlValueStringBuilder.append(tempSringBuilder);
                }
                sqlValueStringBuilder.deleteCharAt(sqlValueStringBuilder.length() - 1);
                query = "INSERT INTO " + form_String + sqlKeyStringBuilder.toString() + " VALUES" + sqlValueStringBuilder;
            } else {
                StringBuilder valueBatch = new StringBuilder();
                int indexOf = query.indexOf("#");
                int lastIndexOf = query.lastIndexOf("#");
                String subValue = query.substring(indexOf + 1, lastIndexOf);
                String subQuery = query.substring(0, indexOf);

                while (iterator.hasNext()) {
                    String tempSql = subValue;
                    JSONObject values = (JSONObject) iterator.next();
                    Set<String> firstKeys = values.keySet();
                    for (String key : firstKeys) {
                        tempSql = tempSql.replace("@" + key + "@", values.getString(key).replace("'", "\\'"));
                    }
                    valueBatch.append("(").append(tempSql).append("),");
                }

                valueBatch.deleteCharAt(valueBatch.length() - 1);
                query = subQuery + valueBatch.toString();
            }

            HashMap<String, Object> excuteResult = updateDb(query);
            if (excuteResult.get("errorCode") != null) {
                returnParam.put("errorCode", excuteResult.get("errorCode"));
            }

            return returnMap;
        } catch (Exception e) {
            returnParam.put("errorCode", "DAO_BatchInsertInDb_exception");
            returnMap.put("returnParam", returnParam);

            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    private HashMap<String, Object> deleteInDb(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String query = (moduleParam.get("query") == null)?"":moduleParam.get("query").toString();
            String filter = (moduleParam.get("filter") == null)?"":moduleParam.get("filter").toString();

            if(query.compareTo("")==0) {
                String form_String = moduleParam.get("form").toString();
                String keys = "";
                String values = "";
                String where = "";
                HashMap<String, Boolean[]> formData = DataBaseFormConfig.fromMaps.get(form_String);

                if(filter.compareTo("")!=0){
                    for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                        String entryKey = entry.getKey();
                        Object entryValue = entry.getValue();
                        if (entryValue instanceof JSONArray) {
                            JSONArray entryValueArr = (JSONArray) entryValue;
                            if (entryValueArr.size() == 0) {
                                continue;
                            }
                            String tempCaluse = "";
                            for (Object templist : entryValueArr) {
                                tempCaluse += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                            }
                            tempCaluse = tempCaluse.substring(0, tempCaluse.length() - 1);
                            filter = filter.replace("#" + entryKey + "#", tempCaluse);
                        } else {
                            filter = filter.replace("@" + entryKey + "@", entryValue.toString().replace("'", "\\'"));
                        }
                    }
                    where = filter;
                } else {
                    for (Map.Entry<String, Boolean[]> entry : formData.entrySet()) {
                        String key = entry.getKey();
                        Object passObj = passParam.get(key);
                        if (passObj == null) {
                            continue;
                        } else {
                            if (passObj.toString().contains("%")) {
                                where += key + " LIKE '" + passObj.toString().replace("'", "\\'") + "' and ";
                            } else if (passObj instanceof JSONArray) {
                                JSONArray valueArr = (JSONArray) passObj;
                                if (valueArr.size() == 0) {
                                    continue;
                                }
                                String tempWhere = "";
                                for (Object templist : valueArr) {
                                    tempWhere += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                                }
                                tempWhere = tempWhere.substring(0, tempWhere.length() - 1);
                                where += key + " IN (" + tempWhere + ")" + " and ";
                            } else {
                                where += key + " = '" + passObj.toString().replace("'", "\\'") + "' and ";
                            }
                        }
                    }
                    where = where.substring(0,where.length()-4);
                }

                query = "DELETE FROM " + form_String + " where " + where;
            } else {
                for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                    String entryKey = entry.getKey();
                    Object entryValue = entry.getValue();
                    if (entryValue instanceof JSONArray) {
                        JSONArray entryValueArr = (JSONArray) entryValue;
                        if (entryValueArr.size() == 0) {
                            continue;
                        }
                        String tempCaluse = "";
                        for (Object templist : entryValueArr) {
                            tempCaluse += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                        }
                        tempCaluse = tempCaluse.substring(0, tempCaluse.length() - 1);
                        query = query.replace("#" + entryKey + "#", tempCaluse);
                    } else {
                        query = query.replace("@" + entryKey + "@", entryValue.toString().replace("'", "\\'"));
                    }
                }
            }
            HashMap<String,Object> excuteResult = updateDb(query);
            if(excuteResult.get("errorCode")!=null){
                returnParam.put("errorCode", excuteResult.get("errorCode"));
            }

            return returnMap;
        } catch(Exception e){
            returnParam.put("errorCode", "DAO_DeleteInDb_exception");
            returnMap.put("returnParam", returnParam);

            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    private HashMap<String, Object> updateInDb(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            String query = (moduleParam.get("query") == null)?"":moduleParam.get("query").toString();
            String filter = (moduleParam.get("filter") == null)?"":moduleParam.get("filter").toString();


            if(query.compareTo("")==0) {
                String form_String = moduleParam.get("form").toString();
                String keys = "";
                String values = "";
                String where = "";
                HashMap<String, Boolean[]> formData = DataBaseFormConfig.fromMaps.get(form_String);

                if(filter.compareTo("")!=0){
                    for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                        String entryKey = entry.getKey();
                        Object entryValue = entry.getValue();
                        if (entryValue instanceof JSONArray) {
                            JSONArray entryValueArr = (JSONArray) entryValue;
                            if (entryValueArr.size() == 0) {
                                continue;
                            }
                            String tempCaluse = "";
                            for (Object templist : entryValueArr) {
                                tempCaluse += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                            }
                            tempCaluse = tempCaluse.substring(0, tempCaluse.length() - 1);
                            filter = filter.replace("#" + entryKey + "#", tempCaluse);
                        } else {
                            filter = filter.replace("@" + entryKey + "@", entryValue.toString().replace("'", "\\'"));
                        }
                    }
                    where = filter;
                } else {
                    for (Map.Entry<String, Boolean[]> entry : formData.entrySet()) {
                        String key = entry.getKey();
                        Object passObj = passParam.get(key);
                        if (passObj == null) {
                            continue;
                        } else {
                            if (entry.getValue()[0]) {
                                if (passObj instanceof JSONArray) {
                                    JSONArray valueArr = (JSONArray) passObj;
                                    if (valueArr.size() == 0) {
                                        continue;
                                    }
                                    String tempWhere = "";
                                    for (Object templist : valueArr) {
                                        tempWhere += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                                    }
                                    tempWhere = tempWhere.substring(0, tempWhere.length() - 1);
                                    where += key + " IN (" + tempWhere + ")" + " and ";
                                } else {
                                    where += key + " = '" + passObj.toString().replace("'", "\\'") + "' and ";
                                }
                            } else {
                                values += key + " = '" + passObj.toString().replace("'", "\\'") + "',";
                            }
                        }
                    }
                    where = where.substring(0,where.length()-4);
                }
                query = "UPDATE "+ form_String + " SET "+values.substring(0, values.length() - 1)+" WHERE "+ where ;
            } else {
                for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                    String entryKey = entry.getKey();
                    Object entryValue = entry.getValue();
                    if (entryValue instanceof JSONArray) {
                        JSONArray entryValueArr = (JSONArray) entryValue;
                        if (entryValueArr.size() == 0) {
                            continue;
                        }
                        String tempCaluse = "";
                        for (Object templist : entryValueArr) {
                            tempCaluse += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                        }
                        tempCaluse = tempCaluse.substring(0, tempCaluse.length() - 1);
                        query = query.replace("#" + entryKey + "#", tempCaluse);
                    } else {
                        query = query.replace("@" + entryKey + "@", entryValue.toString().replace("'", "\\'"));
                    }
                }
            }
            HashMap<String,Object> excuteResult = updateDb(query);
            if(excuteResult.get("errorCode")!=null){
                returnParam.put("errorCode", excuteResult.get("errorCode"));
            }

            return returnMap;
        } catch(Exception e){
            returnParam.put("errorCode", "DAO_UpdateInDb_exception");
            returnMap.put("returnParam", returnParam);

            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }

    private HashMap<String, Object> selectInDb(HashMap<String, Object> param){
        HashMap<String, Object> returnMap = param;
        JSONObject passParam = (JSONObject) param.get("passParam");
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        JSONObject sessionSave = (JSONObject) param.get("sessionSave");
        JSONObject returnParam = (JSONObject) param.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)param.get("httpRequest");

        try {
            int page = (passParam.get("page") == null)?1:Integer.parseInt(passParam.get("page").toString());
            int per_page = (moduleParam.get("per_page") == null)?1:Integer.parseInt(moduleParam.get("per_page").toString());
            per_page = (passParam.get("per_page") == null)?per_page:Integer.parseInt(passParam.get("per_page").toString());
            boolean isNullError = (moduleParam.get("isNullError") == null)?false:Boolean.parseBoolean(moduleParam.get("isNullError").toString());
            boolean isNotNullError = (moduleParam.get("isNotNullError") == null)?false:Boolean.parseBoolean(moduleParam.get("isNotNullError").toString());
            boolean isAsResult = (moduleParam.get("isAsResult") == null)?false:Boolean.parseBoolean(moduleParam.get("isAsResult").toString());
            boolean isAllGet = (moduleParam.get("isAllGet") == null)?false:Boolean.parseBoolean(moduleParam.get("isAllGet").toString());
            boolean isOnlyCount = (moduleParam.get("isOnlyCount") == null)?false:Boolean.parseBoolean(moduleParam.get("isOnlyCount").toString());
            boolean isOnlyList = (moduleParam.get("isOnlyList") == null)?false:Boolean.parseBoolean(moduleParam.get("isOnlyList").toString());
            boolean isSaveSession = (moduleParam.get("isSaveSession") == null)?false:Boolean.parseBoolean(moduleParam.get("isSaveSession").toString());
            String resultKey = (moduleParam.get("resultKey") == null)?"dataResult":moduleParam.get("resultKey").toString();
            String countKey = (moduleParam.get("countKey") == null)?"dataCount":moduleParam.get("countKey").toString();
            String filter = (moduleParam.get("filter") == null)?"":moduleParam.get("filter").toString();
            String query = (moduleParam.get("query") == null)?"":moduleParam.get("query").toString();

            boolean isJudgeOrder = false;
            String order = (passParam.get("order") == null) ? "" : passParam.get("order").toString();

            if (order != "") {
                isJudgeOrder = true;
            }
            String moduleOrder = moduleParam.get("order");
            if (moduleParam.get("order") != null) {
                isJudgeOrder = false;
                order = moduleOrder;
            }
            //order = (moduleOrder == null) ? order: moduleParam.get("order").toString();

            boolean isWhereAnd = false;

            if(query.compareTo("")==0){
                String form_String = moduleParam.get("form").toString();
                String[] formList = form_String.split(",");
                String select = "";
                String where = "";

                List<String> forms = new ArrayList<String>();
                List<String> formskeys = new ArrayList<String>();

                for(int i=0;i<formList.length;i++){
                    HashMap<String, Boolean[]> formData = DataBaseFormConfig.fromMaps.get(formList[i]);
                    for (Map.Entry<String, Boolean[]> entry : formData.entrySet()) {
                        entry.getKey();
                        int index = formskeys.indexOf(entry.getKey());
                        formskeys.add(entry.getKey());
                        forms.add(formList[i]);

                        if (index == -1) {
                            Object passObj = passParam.get(entry.getKey());
                            if (passObj != null) {
                                if (passObj.toString().contains("%")) {
                                    where += " " + formList[i] + "." + "" + entry.getKey() + " LIKE '" + passObj.toString().replace("'", "\\'") + "'" + " and ";//数组解析
                                } else if (passObj instanceof JSONArray) {
                                    JSONArray valueArr = (JSONArray) passObj;
                                    if (valueArr.size() == 0) {
                                        continue;
                                    }
                                    String tempWhere = "";
                                    for (Object templist : valueArr) {
                                        tempWhere += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                                    }
                                    tempWhere = tempWhere.substring(0, tempWhere.length() - 1);
                                    where += " " + formList[i] + "." + "" + entry.getKey() + " IN (" + tempWhere + ")" + " and ";
                                } else {
                                    where += " " + formList[i] + "." + "" + entry.getKey() + "='" + passObj.toString().replace("'", "\\'") + "'" + " and ";
                                }
                                isWhereAnd = true;
                            }

                            if(isAsResult && !(entry.getValue()[1])){
                                continue;
                            }
                            if(isSaveSession && !(entry.getValue()[2])){
                                continue;
                            }
                            select += formList[i]+"."+entry.getKey()+",";

                        } else {
                            where += " "+forms.get(index)+"."+formskeys.get(index)+" = "+formList[i]+"."+""+entry.getKey()+ " and ";
                            isWhereAnd = true;
                        }
                    }
                }

                if(isWhereAnd) {
                    where = where.substring(0, where.length() - 4);
                }

                if(filter.compareTo("")!=0){
                    for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                        String entryKey = entry.getKey();
                        Object entryValue = entry.getValue();
                        if (entryValue instanceof JSONArray) {
                            JSONArray entryValueArr = (JSONArray) entryValue;
                            if (entryValueArr.size() == 0) {
                                continue;
                            }
                            String tempCaluse = "";
                            for (Object templist : entryValueArr) {
                                tempCaluse += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                            }
                            tempCaluse = tempCaluse.substring(0, tempCaluse.length() - 1);
                            filter = filter.replace("#" + entryKey + "#", tempCaluse);
                        } else {
                            filter = filter.replace("@" + entryKey + "@", entryValue.toString().replace("'", "\\'"));
                        }

                    }
                    where = filter;
                }

                if(select.compareTo("")==0) {
                    COMMON_LOGGER.ERROR(param, "select parm don not right");
                }
                //judge order
                if (isJudgeOrder) {
                    String[] orderArray = {"desc", "asc", "ASC", "DESC"};
                    List<String> orderList = Arrays.asList(orderArray);
                    String[] splitOrder = order.split(",");
                    for (String childSplit : splitOrder) {
                        String[] splitSpace = childSplit.split(" ", 2);
                        if (splitSpace.length == 1) {
                            if (!formskeys.contains(splitSpace[0])) {
                                returnParam.put("errorCode", "DAO_Order_Block");
                                return returnMap;
                            }
                        } else {
                            if (!(formskeys.contains(splitSpace[0]) && orderList.contains(splitSpace[1].toLowerCase()))) {
                                returnParam.put("errorCode", "DAO_Order_Block");
                                return returnMap;
                            }
                        }
                    }
                }

                query = "SELECT " + select.substring(0, select.length() - 1) + " FROM "+ form_String + (where.compareTo("")==0?"":" WHERE "+where) + (order.compareTo("")==0?"":" ORDER BY "+order);
            } else {
                for (Map.Entry<String, Object> entry : passParam.entrySet()) {
                    String entryKey = entry.getKey();
                    Object entryValue = entry.getValue();
                    if (entryValue instanceof JSONArray) {
                        JSONArray entryValueArr = (JSONArray) entryValue;
                        if (entryValueArr.size() == 0) {
                            continue;
                        }
                        String tempCaluse = "";
                        for (Object templist : entryValueArr) {
                            tempCaluse += "'" + String.valueOf(templist).replace("'", "\\'") + "',";
                        }
                        tempCaluse = tempCaluse.substring(0, tempCaluse.length() - 1);
                        query = query.replace("#" + entryKey + "#", tempCaluse);
                    } else {
                        query = query.replace("@" + entryKey + "@", entryValue.toString().replace("'", "\\'"));
                    }
                }
            }
            HashMap<String,Object> excuteResult = searchDb(query, page, per_page, isAllGet, isNullError, isNotNullError);
            if(excuteResult.get("errorCode")!=null){
                returnParam.put("errorCode", excuteResult.get("errorCode"));
            }
            if(excuteResult.get("resultList")!=null){
                JSONObject tempResult = new JSONObject();
                if(isOnlyCount){
                    tempResult.put("count", excuteResult.get("resultCount"));
                } else if(isOnlyList){
                    tempResult.put("resultList", excuteResult.get("resultList"));
                } else {
                    tempResult.put("count", excuteResult.get("resultCount"));
                    tempResult.put("resultList", excuteResult.get("resultList"));
                }

                if(isAsResult){
                    JSONObject result = returnParam.get(resultKey)==null?new JSONObject():(JSONObject) returnParam.get(resultKey);
                    result.putAll(tempResult);

                    returnParam.put(resultKey,result);
                } else {
                    if(tempResult.get("resultList")!=null){
                        if (resultKey.compareTo("dataResult") == 0) {
                            if (((JSONArray) tempResult.get("resultList")).size() != 0) {
                                if (isSaveSession) {
                                    sessionSave.putAll((JSONObject) ((JSONArray) tempResult.get("resultList")).get(0));
                                }
                                passParam.putAll((JSONObject) ((JSONArray) tempResult.get("resultList")).get(0));

                            }
                        } else {
                            if (isSaveSession) {
                                sessionSave.put(resultKey, (JSONArray) tempResult.get("resultList"));
                            }
                            passParam.put(resultKey, (JSONArray) tempResult.get("resultList"));
                        }
                    }
                    if(tempResult.get("count")!=null){
                        passParam.put(countKey, tempResult.get("count").toString());
                    }
                }
            }

            return returnMap;
        } catch(Exception e){
            returnParam.put("errorCode", "DAO_SearchInDb_exception");
            returnMap.put("returnParam", returnParam);

            COMMON_LOGGER.ERROR(returnMap, ErrorCodes.getErrorInfoFromException(e));
        } finally {
            returnMap.put("passParam", passParam);
            returnMap.put("sessionSave", sessionSave);
            returnMap.put("returnParam", returnParam);
            returnMap.put("httpRequest", request);
        }

        return returnMap;
    }


    public HashMap<String, Object> start(HashMap<String, Object> param) {
        COMMON_LOGGER.DEBUG(param, "Dao start");
        HashMap<String, Object> returnParam = param;
        HashMap<String, String> moduleParam = (HashMap<String, String>) param.get("moduleParam");
        switch(moduleParam.get("control")){
            case "insert":
                returnParam = insertInDb(param);
                break;
            case "select":
                returnParam = selectInDb(param);
                break;
            case "delete":
                returnParam = deleteInDb(param);
                break;
            case "update":
                returnParam = updateInDb(param);
                break;
            case "batchInsert":
                returnParam = batchInsertInDb(param);
                break;
            default:
                break;
        }
        COMMON_LOGGER.DEBUG(returnParam, "Dao end");
        return returnParam;
    }
}
