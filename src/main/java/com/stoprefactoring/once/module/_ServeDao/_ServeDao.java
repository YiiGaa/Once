package com.stoprefactoring.once.module._ServeDao;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.stoprefactoring.once.common.ERRORCODE;
import com.stoprefactoring.once.common.LOGGER;
import com.stoprefactoring.once.common.TOOLS;
import com.stoprefactoring.once.module.Link;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Matcher.quoteReplacement;

public class _ServeDao extends Link {
    public static ThreadLocal<TransactionStatus> transactionHandler = new ThreadLocal<>();
    public static ThreadLocal<Boolean> transactionIsAutoErrorRollBack = new ThreadLocal<>();
    public static ThreadLocal<String> transactionPosition = new ThreadLocal<>();

    String Tools_SQLPieceEscape(String param){
        return param.replace("'", "''");
    }

    ERRORCODE TXNStart(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        try{
            //STEP::Already create Transaction
            if(transactionHandler.get()!=null){
                LOGGER.DEBUG("Module-_ServeDao TXNStart already create Transaction");
                return ERRORCODE.ERR_Module__ServeDao_TXNStart_Create_Already;
            }

            //STEP::Get setting
            Boolean _isAutoErrorRollBack = TOOLS.ReadParam("_isAutoErrorRollBack", true, moduleParam, passParam);
            String _level = TOOLS.ReadParam("_level", "", moduleParam, passParam);
            LOGGER.DEBUG("Module-_ServeDao TXNStart param, _isAutoErrorRollBack:" + _isAutoErrorRollBack);
            LOGGER.DEBUG("Module-_ServeDao TXNStart param, _level:" + _level);

            //STEP::Get level
            int level = TransactionDefinition.ISOLATION_DEFAULT;
            switch (_level){
                case "read uncommitted":
                    level = TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
                    break;
                case "read committed":
                    level = TransactionDefinition.ISOLATION_READ_COMMITTED;
                    break;
                case "repeatable read":
                    level = TransactionDefinition.ISOLATION_REPEATABLE_READ;
                    break;
                case "serializable":
                    level = TransactionDefinition.ISOLATION_SERIALIZABLE;
                    break;
                case "":
                    level = TransactionDefinition.ISOLATION_DEFAULT;
                    break;
                default:
                    LOGGER.DEBUG("Module-_ServeDao TXNStart _level is illegal, _level:" + _level);
                    return ERRORCODE.ERR_Module__ServeDao_TXNStart_Level_Illegal;
            }

            //STEP::Create transaction
            DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
            transactionDefinition.setIsolationLevel(level);
            TransactionStatus transactionStatus = _ServeDaoConfig.transactionManager.getTransaction(transactionDefinition);
            String position = LOGGER.GetCallPosition();
            transactionHandler.set(transactionStatus);
            transactionIsAutoErrorRollBack.set(_isAutoErrorRollBack);
            transactionPosition.set(position);

            LOGGER.INFO("Module-_ServeDao Transaction start");
        }catch (Exception e){
            LOGGER.ERROR("Module-_ServeDao TXNStart exception:"+ TOOLS.GetExceptionInfo(e), moduleParam, passParam, returnParam);
            result = ERRORCODE.ERR_Module__ServeDao_TXNStart_Exception;
        }

        return result;
    }

    ERRORCODE TXNEnd(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check whether it has transaction handler
        TransactionStatus transactionStatus = transactionHandler.get();
        if(transactionStatus==null){
            return result;
        }

        try{
            //STEP::Get setting
            Boolean _isCommit = TOOLS.ReadParam("_isCommit", true, moduleParam, passParam);
            LOGGER.DEBUG("Module-_ServeDao TXNEnd param, _isCommit:" + _isCommit);

            //STEP::Rollback or commit transaction
            if(_isCommit) {
                LOGGER.INFO("Module-_ServeDao Transaction commit");
                _ServeDaoConfig.transactionManager.commit(transactionStatus);
            } else {
                LOGGER.INFO("Module-_ServeDao Transaction roll back");
                _ServeDaoConfig.transactionManager.rollback(transactionStatus);
            }

            //STEP::Clean handler
            transactionHandler.remove();
            transactionIsAutoErrorRollBack.remove();
            transactionPosition.remove();
        }catch (Exception e){
            LOGGER.ERROR("Module-_ServeDao TXNEnd exception:"+ TOOLS.GetExceptionInfo(e), moduleParam, passParam, returnParam);
            result = ERRORCODE.ERR_Module__ServeDao_TXNEnd_Exception;
        }

        return result;
    }

    ERRORCODE SQLExecute(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        try {
            //STEP::Get setting
            Boolean _isPutReturn = TOOLS.ReadParam("_isPutReturn", true, moduleParam, passParam);
            String _inner_SQLString = TOOLS.ReadParam("_inner_SQLString", "", moduleParam, passParam);
            String _inner_SQLType = TOOLS.ReadParam("_inner_SQLType", "", moduleParam, passParam);
            String _inner_resultKey = TOOLS.ReadParam("_inner_resultKey", "", moduleParam, passParam);
            Boolean _inner_isCheckUpdateAffect = TOOLS.ReadParam("_inner_isCheckUpdateAffect", false, moduleParam, passParam);
            Boolean _inner_isCheckSelectNullError = TOOLS.ReadParam("_inner_isCheckSelectNullError", false, moduleParam, passParam);
            Boolean _inner_isCheckSelectNotNullError = TOOLS.ReadParam("_inner_isCheckSelectNotNullError", false, moduleParam, passParam);
            LOGGER.DEBUG("Module-_ServeDao SQLExecute param, _inner_SQLString:" + _inner_SQLString);
            LOGGER.DEBUG("Module-_ServeDao SQLExecute param, _inner_SQLType:" + _inner_SQLType);
            LOGGER.DEBUG("Module-_ServeDao SQLExecute param, _inner_resultKey:" + _inner_resultKey);
            LOGGER.DEBUG("Module-_ServeDao SQLExecute param, _inner_isCheckUpdateAffect:" + _inner_isCheckUpdateAffect);
            LOGGER.DEBUG("Module-_ServeDao SQLExecute param, _inner_isCheckSelectNullError:" + _inner_isCheckSelectNullError);
            LOGGER.DEBUG("Module-_ServeDao SQLExecute param, _inner_isCheckSelectNotNullError:" + _inner_isCheckSelectNotNullError);

            //STEP::Execute SQL
            Object resultExecute;
            switch (_inner_SQLType) {
                case "update":
                    LOGGER.INFO("Module-_ServeDao SQLExecute update, sql:" + _inner_SQLString);
                    resultExecute = (Object) _ServeDaoConfig.daoHandler.update(_inner_SQLString);
                    if(_inner_isCheckUpdateAffect){
                        if ((Integer)resultExecute<=0){
                            return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Update_Fail;
                        }
                    }
                    break;
                case "select string":
                    LOGGER.INFO("Module-_ServeDao SQLExecute select string, sql:" + _inner_SQLString);
                    resultExecute = (Object) _ServeDaoConfig.daoHandler.queryForObject(_inner_SQLString, String.class);
                    if(_inner_isCheckSelectNotNullError && resultExecute!=null){
                        return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_NotNull_Error;
                    } else if(_inner_isCheckSelectNullError && resultExecute==null){
                        return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_Null_Error;
                    }
                    break;
                case "select int":
                    LOGGER.INFO("Module-_ServeDao SQLExecute select int, sql:" + _inner_SQLString);
                    resultExecute = (Object) _ServeDaoConfig.daoHandler.queryForObject(_inner_SQLString, Integer.class);
                    if(_inner_isCheckSelectNotNullError && resultExecute!=null && ((Integer)resultExecute)>0){
                        return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_NotNull_Error;
                    } else if(_inner_isCheckSelectNullError && (resultExecute==null || ((Integer)resultExecute)<=0)){
                        return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_Null_Error;
                    }
                    break;
                case "select list":
                    {
                        LOGGER.INFO("Module-_ServeDao SQLExecute select list, sql:" + _inner_SQLString);
                        List<Map<String, Object>> resultList = _ServeDaoConfig.daoHandler.queryForList(_inner_SQLString);
                        JSONArray resultArray = new JSONArray();
                        if (resultList.size() > 0 && _inner_isCheckSelectNotNullError) {
                            return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_NotNull_Error;
                        } else if (resultList.size() > 0) {
                            for (Map<String, Object> item : resultList) {
                                JSONObject jsonObject = new JSONObject(item);
                                resultArray.add(jsonObject);
                            }
                        } else if (_inner_isCheckSelectNullError){
                            return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_Null_Error;
                        }
                        resultExecute = resultArray;
                    }
                    break;
                case "select object":
                    {
                        LOGGER.INFO("Module-_ServeDao SQLExecute select object, sql:" + _inner_SQLString);
                        List<Map<String, Object>> resultList = _ServeDaoConfig.daoHandler.queryForList(_inner_SQLString);
                        JSONObject resultObject = new JSONObject();
                        if (resultList.size() > 0 && _inner_isCheckSelectNotNullError) {
                            return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_NotNull_Error;
                        } else if (resultList.size() > 0) {
                            resultObject = new JSONObject(resultList.get(0));
                        } else if (_inner_isCheckSelectNullError){
                            return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Select_Null_Error;
                        }
                        resultExecute = resultObject;
                    }
                    break;
                default:
                    LOGGER.DEBUG("Module-_ServeDao SQLStringDeal type is illegal, _inner_SQLType:" + _inner_SQLType);
                    return ERRORCODE.ERR_Module__ServeDao_SQLExecute_Type_Illegal;
            }

            //STEP::Mark result
            if(!_inner_resultKey.equals("")) {
                if (_isPutReturn)
                    returnParam.put(_inner_resultKey, resultExecute);
                else
                    passParam.put(_inner_resultKey, resultExecute);
            } else if(_inner_resultKey.equals("") && resultExecute instanceof JSONObject) {
                if (_isPutReturn)
                    returnParam.putAll((JSONObject)resultExecute);
                else
                    passParam.putAll((JSONObject)resultExecute);
            }

        }catch (Exception e){
            LOGGER.ERROR("Module-_ServeDao SQLExecute exception:"+ TOOLS.GetExceptionInfo(e), moduleParam, passParam, returnParam);
            result = ERRORCODE.ERR_Module__ServeDao_SQLExecute_Exception;
        }

        return result;
    }

    ERRORCODE SQLStringDeal(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        Boolean _isSQLReplaceStrict = TOOLS.ReadParam("_isSQLReplaceStrict", true, moduleParam, passParam);
        String _inner_SQLString = TOOLS.ReadParam("_inner_SQLString", "", moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao SQLStringDeal param, _isSQLReplaceStrict:" + _isSQLReplaceStrict);
        LOGGER.DEBUG("Module-_ServeDao SQLStringDeal param, _inner_SQLString:" + _inner_SQLString);

        //STEP::Check whether _sql is empty
        if(_inner_SQLString.equals("")){
            return ERRORCODE.ERR_Module__ServeDao_SQLStringDeal_Template_Illegal;
        }

        //STEP::Replace '@xxx@' by passParam data
        String executeSQL = _inner_SQLString;
        Pattern pattern = Pattern.compile("'@(.*?)@'");
        Matcher matcher = pattern.matcher(_inner_SQLString);
        String backupString = _inner_SQLString;
        while (matcher.find()) {
            String keyPass = TOOLS.JsonPathChangeReal(matcher.group(1));
            Object returnValue = passParam.getByPath(keyPass);
            if(returnValue != null){
                String targetValue = "";
                if(returnValue instanceof JSONArray){
                    for(Object item : (JSONArray)returnValue){
                        targetValue += "'"+Tools_SQLPieceEscape(item.toString())+"',";
                    }
                    if (targetValue.endsWith(","))
                        targetValue = targetValue.substring(0, targetValue.length() - 1);
                }else{
                    targetValue = "'"+Tools_SQLPieceEscape(returnValue.toString())+"'";
                }
                executeSQL = executeSQL.replaceFirst(Pattern.quote("'@"+matcher.group(1)+"@'"), quoteReplacement(targetValue));
            }
            backupString = backupString.replaceFirst(Pattern.quote("'@"+matcher.group(1)+"@'"), "");
        }
        _inner_SQLString = backupString;

        //STEP::Replace @xxx@ by passParam data
        pattern = Pattern.compile("(?<!['])@(.*?)@(?!')");
        matcher = pattern.matcher(_inner_SQLString);
        while (matcher.find()) {
            String keyPass = TOOLS.JsonPathChangeReal(matcher.group(1));
            Object returnValue = passParam.getByPath(keyPass);
            if(returnValue != null){
                String targetValue = "";
                if(returnValue instanceof JSONArray){
                    for(Object item : (JSONArray)returnValue){
                        if(_isSQLReplaceStrict){
                            String testStr = item.toString();
                            if(!testStr.matches("^[A-Za-z0-9_\\$]*$")){
                                LOGGER.DEBUG("Module-_ServeDao SQLStringDeal value for replacing is illegal(limit [A-Za-z0-9_$]), value:" + testStr);
                                return ERRORCODE.ERR_Module__ServeDao_SQLStringDeal_Replace_Illegal;
                            }
                        }
                        targetValue += item.toString()+",";
                    }
                    if (targetValue.endsWith(","))
                        targetValue = targetValue.substring(0, targetValue.length() - 1);
                }else{
                    targetValue = returnValue.toString();
                    if(_isSQLReplaceStrict){
                        if(!targetValue.matches("^[A-Za-z0-9_\\$]*$")){
                            LOGGER.DEBUG("Module-_ServeDao SQLStringDeal value for replacing is illegal(limit [A-Za-z0-9_$]), value:" + targetValue);
                            return ERRORCODE.ERR_Module__ServeDao_SQLStringDeal_Replace_Illegal;
                        }
                    }
                }
                executeSQL = executeSQL.replaceFirst(Pattern.quote("@"+matcher.group(1)+"@"), quoteReplacement(targetValue));
            }
        }

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", executeSQL);

        return result;
    }

    ERRORCODE SQLUpdate(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _sql = TOOLS.ReadParam("_sql", "", moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "result", moduleParam, passParam);
        Boolean _isCheckAffect = TOOLS.ReadParam("_isCheckAffect", true, moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao SQLUpdate param, _sql:" + _sql);
        LOGGER.DEBUG("Module-_ServeDao SQLUpdate param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao SQLUpdate param, _isCheckAffect:" + _isCheckAffect);

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", _sql);
        moduleParam.put("_inner_SQLType", "update");
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckUpdateAffect", _isCheckAffect);

        return result;
    }

    ERRORCODE SQLSelect(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _sql = TOOLS.ReadParam("_sql", "", moduleParam, passParam);
        String _selectType = TOOLS.ReadParam("_selectType", "select list", moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "result", moduleParam, passParam);
        Boolean _isGetNullError = TOOLS.ReadParam("_isGetNullError", false, moduleParam, passParam);
        Boolean _isGetNotNullError = TOOLS.ReadParam("_isGetNotNullError", false, moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao SQLSelect param, _sql:" + _sql);
        LOGGER.DEBUG("Module-_ServeDao SQLSelect param, _selectType:" + _selectType);
        LOGGER.DEBUG("Module-_ServeDao SQLSelect param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao SQLSelect param, _isGetNullError:" + _isGetNullError);
        LOGGER.DEBUG("Module-_ServeDao SQLSelect param, _isGetNotNullError:" + _isGetNotNullError);

        //STEP::Check _selectType
        if(!(_selectType.equals("select string") ||
             _selectType.equals("select int") ||
             _selectType.equals("select list") ||
             _selectType.equals("select object"))){
            LOGGER.DEBUG("Module-_ServeDao SQLSelect type is illegal, _selectType:" + _selectType);
            return ERRORCODE.ERR_Module__ServeDao_SQLSelect_Type_Illegal;
        }

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", _sql);
        moduleParam.put("_inner_SQLType", _selectType);
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckSelectNullError", _isGetNullError);
        moduleParam.put("_inner_isCheckSelectNotNullError", _isGetNotNullError);

        return result;
    }

    ERRORCODE TableCheck(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        String _inner_tableType = TOOLS.ReadParam("_inner_tableType", "all", moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao TableCheck param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao TableCheck param, _inner_tableType:" + _inner_tableType);

        //STEP::Check whether the table name is illegal(may has "" or "'")
        if(_table.matches(".*[' ].*")){
            LOGGER.DEBUG("Module-_ServeDao TableCheck table name illegal:" + _table);
            return ERRORCODE.ERR_Module__ServeDao_TableCheck_TableName_Illegal;
        }

        //STEP::Check tableObject(auto generate database table when module init)
        //should set module._ServeDao.isAutoGenerateMap = true
        JSONObject tableObject = (JSONObject)_ServeDaoConfig.tableViewMap.get(_table);
        if(tableObject == null || tableObject.isEmpty()){
            LOGGER.DEBUG("Module-_ServeDao TableCheck lack table:" + _table);
            return ERRORCODE.ERR_Module__ServeDao_TableCheck_Table_Lack;
        } else if(!tableObject.containsKey("type") ||
                  !tableObject.containsKey("keyList") ||
                  !tableObject.containsKey("column")){
            LOGGER.DEBUG("Module-_ServeDao TableCheck lack table info:" + _table);
            return ERRORCODE.ERR_Module__ServeDao_TableCheck_TableInfo_Lack;
        }

        //STEP::Check table type
        if(!_inner_tableType.equals("all") &&
           !tableObject.get("type").toString().equals(_inner_tableType)){
                LOGGER.DEBUG("Module-_ServeDao TableCheck table type is illegal, table:" + _table+", type:"+tableObject.get("type").toString());
                return ERRORCODE.ERR_Module__ServeDao_TableCheck_TableType_Illegal;
        }

        return result;
    }

    ERRORCODE Insert(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        JSONObject _target = TOOLS.ReadParam("_target", passParam, moduleParam, passParam);
        Boolean _isReplace = TOOLS.ReadParam("_isReplace", false, moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "", moduleParam, passParam);
        Boolean _isCheckAffect = TOOLS.ReadParam("_isCheckAffect", true, moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao Insert param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao Insert param, _target:" + _isReplace);
        LOGGER.DEBUG("Module-_ServeDao Insert param, _isReplace:" + _target.toString());
        LOGGER.DEBUG("Module-_ServeDao Insert param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao Insert param, _isCheckAffect:" + _isCheckAffect);

        //STEP::Mark SQL piece(insert column and insert value)
        JSONObject tableObject = (JSONObject)_ServeDaoConfig.tableViewMap.get(_table);
        JSONObject columnMap = (JSONObject)tableObject.get("column");
        String insertColumn = "";
        String insertValue = "";
        for(String key : columnMap.keySet()){
            JSONObject columnsInfo = (JSONObject)columnMap.get(key);
            Boolean isNullAble = (Boolean) columnsInfo.get("isNullAble");
            String type = (String)columnsInfo.get("type");
            Long textMaxLength = (Long) columnsInfo.get("textMaxLength");

            //STEP-IN::Check whether it lack necessary column
            if(!_target.containsKey(key) && !isNullAble){
                LOGGER.DEBUG("Module-_ServeDao Insert non-nullable column lack:" + key);
                return ERRORCODE.ERR_Module__ServeDao_Insert_Column_Lack;
            }

            //WHEN-IN::Continue when lack column
            if(!_target.containsKey(key)){
                continue;
            }

            //STEP-IN::Get column value
            String value = Tools_SQLPieceEscape(_target.get(key).toString());

            //STEP-IN::Check text length limit
            if(type.equals("text") && value.length()>textMaxLength){
                LOGGER.DEBUG("Module-_ServeDao Insert column length over limit, column:"+key+", limit:"+textMaxLength+", length:"+value.length());
                return ERRORCODE.ERR_Module__ServeDao_Insert_ColumnValueLength_Limit;
            }

            //STEP-IN::Stitching insertColumn and insertValue
            insertColumn += key+",";
            insertValue += "'"+value+"',";
        }

        //STEP::Check whether insertColumn/insertValue is empty
        if(insertColumn.isEmpty() || insertValue.isEmpty()){
            return ERRORCODE.ERR_Module__ServeDao_SQLMake_Empty;
        }

        //STEP::Stitching SQL
        if (insertValue.endsWith(","))
            insertValue = insertValue.substring(0, insertValue.length() - 1);
        if (insertColumn.endsWith(","))
            insertColumn = insertColumn.substring(0, insertColumn.length() - 1);
        String executeSQL = (_isReplace?"REPLACE":"INSERT")+" INTO "+_table+" ("+insertColumn+") VALUES (" + insertValue + ")";

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "update");
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckUpdateAffect", _isCheckAffect);

        return result;
    }

    ERRORCODE InsertBatch(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        JSONArray _target = TOOLS.ReadParam("_target", new JSONArray(), moduleParam, passParam);
        Boolean _isReplace = TOOLS.ReadParam("_isReplace", false, moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "", moduleParam, passParam);
        Boolean _isCheckAffect = TOOLS.ReadParam("_isCheckAffect", true, moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao InsertBatch param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao InsertBatch param, _target:" + _target.toString());
        LOGGER.DEBUG("Module-_ServeDao InsertBatch param, _isReplace:" + _isReplace);
        LOGGER.DEBUG("Module-_ServeDao InsertBatch param, _isReplace:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao InsertBatch param, _isReplace:" + _isCheckAffect);

        //STEP::Check whether _target is empty
        if(_target.isEmpty()){
            LOGGER.DEBUG("Module-_ServeDao InsertBatch data empty");
            return ERRORCODE.ERR_Module__ServeDao_InsertBatch_Data_Empty;
        }

        //STEP::Mark SQL piece(insert column and insert value list)
        JSONObject tableObject = (JSONObject)_ServeDaoConfig.tableViewMap.get(_table);
        JSONObject columnMap = (JSONObject)tableObject.get("column");
        String insertColumn = "";
        List<String> insertValueList = new ArrayList<>();
        for(Object item:_target)
            insertValueList.add("");
        for(String key : columnMap.keySet()){
            JSONObject columnsInfo = (JSONObject)columnMap.get(key);
            Boolean isNullAble = (Boolean) columnsInfo.get("isNullAble");
            String type = (String)columnsInfo.get("type");
            Long textMaxLength = (Long) columnsInfo.get("textMaxLength");

            //STEP-IN::Stitching insertValueList
            int index = 0;
            for(Object item:_target){
                if(item instanceof JSONObject){
                    String valueListPiece = insertValueList.get(index);
                    //WHEN-IN::Contains key
                    if(((JSONObject)item).containsKey(key)){
                        String valuePiece = Tools_SQLPieceEscape(((JSONObject)item).get(key).toString());

                        //STEP-IN-IN::Check text length limit
                        if(type.equals("text") && valuePiece.length()>textMaxLength){
                            LOGGER.DEBUG("Module-_ServeDao InsertBatch column length over limit, column:"+key+", limit:"+textMaxLength+", length:"+valuePiece.length());
                            return ERRORCODE.ERR_Module__ServeDao_InsertBatch_ColumnValueLength_Limit;
                        }

                        valueListPiece += "'"+valuePiece+"',";
                    }
                    //WHEN-IN::Lack non-nullable key
                    else if (!isNullAble){
                        LOGGER.DEBUG("Module-_ServeDao InsertBatch non-nullable column lack:" + key);
                        return ERRORCODE.ERR_Module__ServeDao_InsertBatch_Column_Lack;
                    }
                    //WHEN-IN::Lack nullable key
                    else {
                        valueListPiece += "DEFAULT,";
                    }
                    insertValueList.set(index, valueListPiece);
                }
                index++;
            }

            //STEP-IN::Stitching insertColumn
            insertColumn += key+",";
        }

        //STEP::Stitching insertValue
        String insertValue = "";
        for(String list:insertValueList){
            if(!list.equals("")) {
                if (list.endsWith(","))
                    list = list.substring(0, list.length() - 1);
                insertValue += "(" + list + "),";
            }
        }

        //STEP::Check whether insertValue/insertColumn is empty
        if(insertColumn.equals("")||insertValue.equals("")){
            return ERRORCODE.ERR_Module__ServeDao_SQLMake_Empty;
        }

        //STEP::Stitching SQL
        if (insertValue.endsWith(","))
            insertValue = insertValue.substring(0, insertValue.length() - 1);
        if (insertColumn.endsWith(","))
            insertColumn = insertColumn.substring(0, insertColumn.length() - 1);
        String executeSQL = (_isReplace?"REPLACE":"INSERT")+" INTO "+_table+" ("+insertColumn+") VALUES " + insertValue;

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "update");
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckUpdateAffect", _isCheckAffect);

        return result;
    }

    ERRORCODE Delete(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        JSONObject _target = TOOLS.ReadParam("_target", passParam, moduleParam, passParam);
        String _filter = TOOLS.ReadParam("_filter", "", moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "", moduleParam, passParam);
        Boolean _isCheckAffect = TOOLS.ReadParam("_isCheckAffect", true, moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao Delete param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao Delete param, _target:" + _target.toString());
        LOGGER.DEBUG("Module-_ServeDao Delete param, _filter:" + _filter);
        LOGGER.DEBUG("Module-_ServeDao Delete param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao Delete param, _isCheckAffect:" + _isCheckAffect);

        //STEP::Make SQL piece(judgment conditions)
        JSONObject tableObject = (JSONObject)_ServeDaoConfig.tableViewMap.get(_table);
        JSONObject columnMap = (JSONObject)tableObject.get("column");
        String whereSetting = "";
        //WHEN::_filter is set(replace @xxx@ by SQLStringDeal())
        if(!_filter.equals("")){
            whereSetting = _filter;
            moduleParam.put("_inner_SQLString", whereSetting);
            result = SQLStringDeal(moduleParam, passParam, returnParam, result);
            if(ERRORCODE.IsError(result))return result;
            whereSetting = (String) moduleParam.get("_inner_SQLString");
        }
        //WHEN::Need to generate by map
        else {
            for (String key : columnMap.keySet()) {
                JSONObject columnsInfo = (JSONObject) columnMap.get(key);
                String type = (String)columnsInfo.get("type");
                Long textMaxLength = (Long) columnsInfo.get("textMaxLength");

                //WHEN-IN::Continue when lack column
                if (!_target.containsKey(key)) {
                    continue;
                }

                //STEP-IN::Stitching whereSetting
                if (_target.get(key) instanceof JSONArray) {
                    String value = "";
                    for (Object item : (JSONArray) _target.get(key)) {
                        String valuePiece = Tools_SQLPieceEscape(item.toString());
                        value += "'" + valuePiece + "',";
                    }
                    if (value.equals("")) {
                        continue;
                    }
                    if (value.endsWith(","))
                        value = value.substring(0, value.length() - 1);
                    whereSetting += key + " IN (" + value + ")" + " and ";
                } else {
                    String valuePiece = Tools_SQLPieceEscape(_target.get(key).toString());
                    whereSetting += key + "='" + valuePiece + "'" + " and ";
                }
            }
        }

        //STEP::Check whether whereSetting is empty
        if (whereSetting.equals("")) {
            return ERRORCODE.ERR_Module__ServeDao_SQLMake_Empty;
        }

        //STEP::Stitching SQL
        if (whereSetting.endsWith("and "))
            whereSetting = whereSetting.substring(0, whereSetting.length() - "and ".length());
        String executeSQL = "DELETE FROM "+_table+" WHERE " + whereSetting;

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "update");
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckUpdateAffect", _isCheckAffect);

        return result;
    }

    ERRORCODE Update(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        JSONObject _target = TOOLS.ReadParam("_target", passParam, moduleParam, passParam);
        String _update = TOOLS.ReadParam("_update", "", moduleParam, passParam);
        String _filter = TOOLS.ReadParam("_filter", "", moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "", moduleParam, passParam);
        Boolean _isCheckAffect = TOOLS.ReadParam("_isCheckAffect", true, moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao Update param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao Update param, _target:" + _target.toString());
        LOGGER.DEBUG("Module-_ServeDao Update param, _update:" + _update);
        LOGGER.DEBUG("Module-_ServeDao Update param, _filter:" + _filter);
        LOGGER.DEBUG("Module-_ServeDao Update param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao Update param, _isCheckAffect:" + _isCheckAffect);

        //STEP::Make SQL piece(update setting and judgment conditions)
        String updateSetting = "";
        String whereSetting = "";
        //WHEN::_update is set(replace @xxx@ by SQLStringDeal())
        if(!_update.equals("")){
            updateSetting = _update;
            moduleParam.put("_inner_SQLString", updateSetting);
            result = SQLStringDeal(moduleParam, passParam, returnParam, result);
            if(ERRORCODE.IsError(result))return result;
            updateSetting = (String) moduleParam.get("_inner_SQLString");
        }
        //WHEN::_filter is set(replace @xxx@ by SQLStringDeal())
        if(!_filter.equals("")){
            whereSetting = _filter;
            moduleParam.put("_inner_SQLString", whereSetting);
            result = SQLStringDeal(moduleParam, passParam, returnParam, result);
            if(ERRORCODE.IsError(result))return result;
            whereSetting = (String) moduleParam.get("_inner_SQLString");
        }
        //WHEN::Need to generate by map
        JSONObject tableObject = (JSONObject)_ServeDaoConfig.tableViewMap.get(_table);
        JSONObject columnMap = (JSONObject)tableObject.get("column");
        if(_filter.equals("")||_update.equals("")) {
            Boolean isNeedGenerateUpdate = _update.equals("");
            Boolean isNeedGenerateWhere = _filter.equals("");
            for (String key : columnMap.keySet()) {
                JSONObject columnsInfo = (JSONObject)columnMap.get(key);
                Boolean isNullAble = (Boolean) columnsInfo.get("isNullAble");
                String type = (String)columnsInfo.get("type");
                Long textMaxLength = (Long) columnsInfo.get("textMaxLength");
                Boolean isKey = (Boolean) columnsInfo.get("isKey");

                //WHEN-IN::Continue when lack column
                if (!_target.containsKey(key)) {
                    continue;
                }
                Object targetValue = _target.get(key);

                //STEP-IN::Stitching updateSetting(when not key column and not JSONArray)
                if(isNeedGenerateUpdate && !isKey && !(targetValue instanceof JSONArray)){
                    String valuePiece = Tools_SQLPieceEscape(targetValue.toString());
                    if(type.equals("text") && valuePiece.length()>textMaxLength){
                        LOGGER.DEBUG("Module-_ServeDao Update key column length over limit, column:"+key+", limit:"+textMaxLength+", length:"+valuePiece.length());
                        return ERRORCODE.ERR_Module__ServeDao_Update_ColumnValueLength_Limit;
                    }
                    updateSetting += key + "='" + valuePiece + "'" + ",";
                }

                //STEP-IN::Stitching whereSetting
                else if (isNeedGenerateWhere) {
                    if(targetValue instanceof JSONArray){
                        String value = "";
                        for (Object item : (JSONArray) targetValue) {
                            String valuePiece = Tools_SQLPieceEscape(item.toString());
                            if(type.equals("text") && valuePiece.length()>textMaxLength){
                                LOGGER.DEBUG("Module-_ServeDao Update column length over limit, column:"+key+", limit:"+textMaxLength+", length:"+valuePiece.length());
                                return ERRORCODE.ERR_Module__ServeDao_Update_ColumnValueLength_Limit;
                            }
                            value += "'" + valuePiece + "',";
                        }
                        if (value.equals("")) {
                            continue;
                        }
                        if (value.endsWith(","))
                            value = value.substring(0, value.length() - 1);
                        whereSetting += key + " IN (" + value + ")" + " and ";
                    } else {
                        String valuePiece = Tools_SQLPieceEscape(targetValue.toString());
                        if(type.equals("text") && valuePiece.length()>textMaxLength){
                            LOGGER.DEBUG("Module-_ServeDao Update column length over limit, column:"+key+", limit:"+textMaxLength+", length:"+valuePiece.length());
                            return ERRORCODE.ERR_Module__ServeDao_Update_ColumnValueLength_Limit;
                        }
                        whereSetting += key + "='" + valuePiece + "'" + " and ";
                    }
                }
            }
        }

        //STEP::Check whether updateSetting/whereSetting is empty
        if (updateSetting.equals("") || whereSetting.equals("")) {
            return ERRORCODE.ERR_Module__ServeDao_SQLMake_Empty;
        }

        //STEP::Stitching SQL
        if (updateSetting.endsWith(","))
            updateSetting = updateSetting.substring(0, updateSetting.length() - ",".length());
        if (whereSetting.endsWith("and "))
            whereSetting = whereSetting.substring(0, whereSetting.length() - "and ".length());
        String executeSQL = "UPDATE "+_table+" SET "+updateSetting+" WHERE " + whereSetting;

        //STEP::Mark info for executing SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "update");
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckUpdateAffect", _isCheckAffect);

        return result;
    }

    ERRORCODE Select(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        JSONObject _target = TOOLS.ReadParam("_target", passParam, moduleParam, passParam);
        String _get = TOOLS.ReadParam("_get", "", moduleParam, passParam);
        String _filter = TOOLS.ReadParam("_filter", "", moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao Select param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao Select param, _target:" + _target.toString());
        LOGGER.DEBUG("Module-_ServeDao Select param, _get:" + _get);
        LOGGER.DEBUG("Module-_ServeDao Select param, _filter:" + _filter);

        //STEP::Init SQL main piece
        String getSetting = "";
        String whereSetting = "";

        //STEP::Make getSetting for SQL select column
        //WHEN::_get is set(replace @xxx@ by SQLStringDeal())
        if(!_get.equals("")){
            getSetting = _get;
            moduleParam.put("_inner_SQLString", getSetting);
            result = SQLStringDeal(moduleParam, passParam, returnParam, result);
            if(ERRORCODE.IsError(result))return result;
            getSetting = (String) moduleParam.get("_inner_SQLString");
        }
        //WHEN::get all column
        else{
            getSetting = "*";
        }

        //STEP::Make whereSetting for SQL select filter
        //WHEN::_filter is set(replace @xxx@ by SQLStringDeal())
        if(!_filter.equals("")){
            whereSetting = _filter;
            moduleParam.put("_inner_SQLString", whereSetting);
            result = SQLStringDeal(moduleParam, passParam, returnParam, result);
            if(ERRORCODE.IsError(result))return result;
            whereSetting = (String) moduleParam.get("_inner_SQLString");
        }
        //WHEN::Need to generate by map
        else {
            JSONObject tableObject = (JSONObject)_ServeDaoConfig.tableViewMap.get(_table);
            JSONObject columnMap = (JSONObject)tableObject.get("column");
            for (String key : columnMap.keySet()) {
                JSONObject columnsInfo = (JSONObject) columnMap.get(key);
                Boolean isNullAble = (Boolean) columnsInfo.get("isNullAble");
                String type = (String) columnsInfo.get("type");
                Long textMaxLength = (Long) columnsInfo.get("textMaxLength");
                Boolean isKey = (Boolean) columnsInfo.get("isKey");

                //WHEN-IN::Continue when lack column
                if (!_target.containsKey(key)) {
                    continue;
                }

                //STEP-IN::Stitching whereSetting
                Object targetValue = _target.get(key);
                if(targetValue instanceof JSONArray){
                    String value = "";
                    for (Object item : (JSONArray) targetValue) {
                        String itemValue = Tools_SQLPieceEscape(item.toString());
                        value = "'" + itemValue + "',";
                    }
                    if (value.equals("")) {
                        continue;
                    }
                    if (value.endsWith(","))
                        value = value.substring(0, value.length() - 1);
                    whereSetting += key + " IN (" + value + ")" + " and ";
                } else {
                    String value = Tools_SQLPieceEscape(targetValue.toString());
                    whereSetting += key + "='" + value + "'" + " and ";
                }

            }
        }

        //STEP::Check whether updateSetting is empty
        if (getSetting.equals("")) {
            return ERRORCODE.ERR_Module__ServeDao_SQLMake_Empty;
        }

        //STEP::Stitching SQL
        if (whereSetting.endsWith("and "))
            whereSetting = whereSetting.substring(0, whereSetting.length() - "and ".length());

        //STEP::Mark info for execute SQL
        moduleParam.put("_inner_SQLSelect_Get", getSetting);
        moduleParam.put("_inner_SQLSelect_Filter", whereSetting);

        return result;
    }

    ERRORCODE SelectCount(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        String _countResultKey = TOOLS.ReadParam("_countResultKey", "count", moduleParam, passParam);
        String _inner_SQLSelect_Filter = TOOLS.ReadParam("_inner_SQLSelect_Filter", "", moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao SelectCount param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao SelectCount param, _countResultKey:" + _countResultKey);
        LOGGER.DEBUG("Module-_ServeDao SelectCount param, _inner_SQLSelect_Filter:" + _inner_SQLSelect_Filter);

        //STEP::Check whether it need get count
        if(_countResultKey.equals("")){
            LOGGER.DEBUG("Module-_ServeDao SelectCount not need count");
            return result;
        }

        //STEP::Stitching SQL
        String executeSQL = "SELECT COUNT(*) FROM "+_table;
        if(!_inner_SQLSelect_Filter.isEmpty()){
            executeSQL += " WHERE " + _inner_SQLSelect_Filter;
        }

        //STEP::Execute SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "select int");
        moduleParam.put("_inner_resultKey", _countResultKey);
        result = SQLExecute(moduleParam, passParam, returnParam, result);

        return result;
    }

    ERRORCODE SelectList(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "result", moduleParam, passParam);
        String _orderBy = TOOLS.ReadParam("_orderBy", "", moduleParam, passParam);
        Integer _page = TOOLS.ReadParam("_page", -1, moduleParam, passParam);
        Integer _pageSize = TOOLS.ReadParam("_pageSize", -1, moduleParam, passParam);
        String _inner_SQLSelect_Get = TOOLS.ReadParam("_inner_SQLSelect_Get", "", moduleParam, passParam);
        String _inner_SQLSelect_Filter = TOOLS.ReadParam("_inner_SQLSelect_Filter", "", moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _orderBy:" + _orderBy);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _page:" + _page);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _pageSize:" + _pageSize);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _inner_SQLSelect_Get:" + _inner_SQLSelect_Get);
        LOGGER.DEBUG("Module-_ServeDao SelectList param, _inner_SQLSelect_Filter:" + _inner_SQLSelect_Filter);

        //STEP::Check whether it need get list
        if(_resultKey.equals("")){
            LOGGER.DEBUG("Module-_ServeDao SelectList not need count");
            return result;
        }

        //STEP::Stitching SQL select limit
        String limitStr = "";
        if(_page>=0 && _pageSize>0)
            limitStr = " LIMIT "+_page*_pageSize+","+_pageSize;

        //STEP::Stitching SQL order by
        String orderByStr = "";
        if(!_orderBy.equals("")){
            orderByStr = " ORDER BY " + _orderBy;
            moduleParam.put("_inner_SQLString", orderByStr);
            result = SQLStringDeal(moduleParam, passParam, returnParam, result);
            if(ERRORCODE.IsError(result))return result;
            orderByStr = (String) moduleParam.get("_inner_SQLString");
        }

        //STEP::Stitching SQL
        String executeSQL = "SELECT "+_inner_SQLSelect_Get+" FROM "+_table;
        if(!_inner_SQLSelect_Filter.isEmpty()){
            executeSQL += " WHERE " + _inner_SQLSelect_Filter;
        }
        executeSQL += orderByStr + limitStr;

        //STEP::Execute SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "select list");
        moduleParam.put("_inner_resultKey", _resultKey);
        result = SQLExecute(moduleParam, passParam, returnParam, result);

        return result;
    }

    ERRORCODE SelectObject(JSONObject moduleParam, JSONObject passParam, JSONObject returnParam, ERRORCODE result){
        //STEP::Check last step result
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get setting
        String _table = TOOLS.ReadParam("_table", "", moduleParam, passParam);
        String _resultKey = TOOLS.ReadParam("_resultKey", "", moduleParam, passParam);
        Boolean _isGetNullError = TOOLS.ReadParam("_isGetNullError", false, moduleParam, passParam);
        Boolean _isGetNotNullError = TOOLS.ReadParam("_isGetNotNullError", false, moduleParam, passParam);
        String _inner_SQLSelect_Get = TOOLS.ReadParam("_inner_SQLSelect_Get", "", moduleParam, passParam);
        String _inner_SQLSelect_Filter = TOOLS.ReadParam("_inner_SQLSelect_Filter", "", moduleParam, passParam);
        LOGGER.DEBUG("Module-_ServeDao SelectObject param, _table:" + _table);
        LOGGER.DEBUG("Module-_ServeDao SelectObject param, _resultKey:" + _resultKey);
        LOGGER.DEBUG("Module-_ServeDao SelectObject param, _isGetNullError:" + _isGetNullError);
        LOGGER.DEBUG("Module-_ServeDao SelectObject param, _isGetNotNullError:" + _isGetNotNullError);
        LOGGER.DEBUG("Module-_ServeDao SelectObject param, _inner_SQLSelect_Get:" + _inner_SQLSelect_Get);
        LOGGER.DEBUG("Module-_ServeDao SelectObject param, _inner_SQLSelect_Filter:" + _inner_SQLSelect_Filter);

        //STEP::Stitching SQL
        String executeSQL = "SELECT "+_inner_SQLSelect_Get+" FROM "+_table+" WHERE " + _inner_SQLSelect_Filter + " LIMIT 1";

        //STEP::Execute SQL
        moduleParam.put("_inner_SQLString", executeSQL);
        moduleParam.put("_inner_SQLType", "select object");
        moduleParam.put("_inner_resultKey", _resultKey);
        moduleParam.put("_inner_isCheckSelectNullError", _isGetNullError);
        moduleParam.put("_inner_isCheckSelectNotNullError", _isGetNotNullError);

        return result;
    }

    private ERRORCODE DoStart(JSONObject moduleParam, HashMap<String, Object> dataPool, ERRORCODE result){
        JSONObject passParam = (JSONObject) dataPool.get("passParam");
        JSONObject returnParam = (JSONObject) dataPool.get("returnParam");
        HttpServletRequest request = (HttpServletRequest)dataPool.get("httpRequest");
        HttpServletResponse response = (HttpServletResponse)dataPool.get("httpResponse");

        try{
            String _action = TOOLS.ReadParam("_action", "", moduleParam, passParam);

            switch (_action){
                case "insert":
                    moduleParam.put("_inner_tableType", "table");
                    result = TableCheck(moduleParam, passParam, returnParam, result);
                    result = Insert(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "insert batch":
                    moduleParam.put("_inner_tableType", "table");
                    result = TableCheck(moduleParam, passParam, returnParam, result);
                    result = InsertBatch(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "delete":
                    moduleParam.put("_inner_tableType", "table");
                    result = TableCheck(moduleParam, passParam, returnParam, result);
                    //SQLStringDeal() is called in Update()
                    result = Delete(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "update":
                    moduleParam.put("_inner_tableType", "table");
                    result = TableCheck(moduleParam, passParam, returnParam, result);
                    //SQLStringDeal() is called in Update()
                    result = Update(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "select object":
                    moduleParam.put("_inner_tableType", "all");
                    result = TableCheck(moduleParam, passParam, returnParam, result);
                    //SQLStringDeal() is called in Select()
                    result = Select(moduleParam, passParam, returnParam, result);
                    result = SelectObject(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "select list":
                    moduleParam.put("_inner_tableType", "all");
                    result = TableCheck(moduleParam, passParam, returnParam, result);
                    //SQLStringDeal() is called in Select()
                    result = Select(moduleParam, passParam, returnParam, result);
                    result = SelectCount(moduleParam, passParam, returnParam, result);
                    result = SelectList(moduleParam, passParam, returnParam, result);
                    //SQLExecute() is called in SelectCount()/SelectList()
                    break;
                case "sql update":
                    result = SQLUpdate(moduleParam, passParam, returnParam, result);
                    result = SQLStringDeal(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "sql select":
                    result = SQLSelect(moduleParam, passParam, returnParam, result);
                    result = SQLStringDeal(moduleParam, passParam, returnParam, result);
                    result = SQLExecute(moduleParam, passParam, returnParam, result);
                    break;
                case "txn start":
                    result = TXNStart(moduleParam, passParam, returnParam, result);
                    break;
                case "txn end":
                    result = TXNEnd(moduleParam, passParam, returnParam, result);
                    break;
                default:
                    result = ERRORCODE.ERR_Module__ServeDao_Action_Illegal;
                    break;
            }

        } catch (Exception e) {
            LOGGER.ERROR("Module-_ServeDao exception:"+ TOOLS.GetExceptionInfo(e), moduleParam, dataPool);
            result = ERRORCODE.ERR_Module__ServeDao_Exception;
        }

        return result;
    }

    @Override
    public ERRORCODE Start(JSONObject moduleParam, HashMap<String, Object> dataPool){
        LOGGER.DEBUG("Module-_ServeDao start", moduleParam, dataPool);
        ERRORCODE result = ERRORCODE.ERR_OK;
        result = DoStart(moduleParam, dataPool, result);
        LOGGER.DEBUG("Module-_ServeDao end", moduleParam, dataPool);
        return result;
    }

    @Override
    public ERRORCODE End(HashMap<String, Object> dataPool, ERRORCODE result){
        LOGGER.DEBUG("Module-_ServeDao End start", dataPool);
        try{
            if(transactionIsAutoErrorRollBack.get()!=null){
                String position = LOGGER.GetCallPosition();
                if(position.equals(transactionPosition.get())){
                    JSONObject passParam = (JSONObject) dataPool.get("passParam");
                    JSONObject returnParam = (JSONObject) dataPool.get("returnParam");
                    JSONObject moduleParam = new JSONObject();
                    boolean isAutoErrorRollBack = transactionIsAutoErrorRollBack.get();
                    boolean isError = ERRORCODE.IsError(result);
                    Boolean isCommit = (isError && !isAutoErrorRollBack) || (!isError && isAutoErrorRollBack);
                    moduleParam.put("_isCommit", isCommit);
                    LOGGER.INFO("Module-_ServeDao End clean transaction, _isCommit="+isCommit+", position:"+position);
                    TXNEnd(moduleParam, passParam, returnParam, result);
                }
            }
        }catch (Exception e){
            LOGGER.ERROR("Module-_ServeDao End for clean exception:"+ TOOLS.GetExceptionInfo(e), dataPool);
        }
        LOGGER.DEBUG("Module-_ServeDao End end", dataPool);
        return result;
    }
}