package main.module._ServeDao;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import main.common.ERRORCODE;
import main.common.LOGGER;
import main.common.TOOLS;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tools.jackson.databind.node.ArrayNode;

public class _ServeDaoGenerateMap {
    public static ERRORCODE GetDataBaseName(ERRORCODE result){
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get current database name
        String currentDatabase = _ServeDaoConfig.daoHandler.queryForObject("SELECT DATABASE()", String.class);
        _ServeDaoConfig.dataBaseName = currentDatabase;
        LOGGER.StraightInfo("Module-_ServeDao init, currentDatabase: "+_ServeDaoConfig.dataBaseName);

        return ERRORCODE.ERR_OK;
    }

    public static ERRORCODE GetTableAndView(ERRORCODE result){
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get Table and View list
        List<Map<String, Object>> tableAndViewList = _ServeDaoConfig.daoHandler.queryForList(
         "SELECT TABLE_NAME,TABLE_TYPE " +
             "FROM INFORMATION_SCHEMA.TABLES " +
             "WHERE TABLE_SCHEMA = '"+_ServeDaoConfig.dataBaseName+"' AND (TABLE_TYPE='BASE TABLE' or TABLE_TYPE='VIEW')"
        );

        //STEP::Insert list to _ServeDaoConfig.tableViewMap
        //_ServeDaoConfig.tableViewMap data now like:
        //{
        //    "table/view 1 name":{"type":"table/view"},
        //    "table/view 2 name":{"type":"table/view"}
        //}
        for(Map<String, Object> item : tableAndViewList){
            String name = item.get("TABLE_NAME").toString();
            String type = item.get("TABLE_TYPE").toString().equals("BASE TABLE")?"table":"view";
            ObjectNode detail = TOOLS.JsonInitObject();
            detail.put("type", type);
            ((ObjectNode)_ServeDaoConfig.tableViewMap).set(name, detail);
        }

        return ERRORCODE.ERR_OK;
    }

    public static ERRORCODE GetMapDetail(ERRORCODE result){
        if(ERRORCODE.IsError(result)){
            return result;
        }

        //STEP::Get table columns
        //_ServeDaoConfig.tableViewMap data now like:
        //{
        //    "table/view 1 name":{
        //          "type":"table/view"
        //          "keyList":["column name(key column)"]
        //          "column":{
        //              "column 1 name":{
        //                  "type":"",
        //                  "textMaxLength":255(Long type),
        //                  "isNullAble":true/false,
        //                  "isKey":true/false
        //              },
        //              "column 2 name":{...}
        //          }
        //    },
        //    "table/view 2 name":{...}
        //}
        Set<Entry<String,JsonNode>> fields = (_ServeDaoConfig.tableViewMap).properties();
        for (Entry<String,JsonNode> entry : fields) {
            String tableName = entry.getKey();
            LOGGER.StraightInfo("Module-_ServeDao init, get table detail:"+ tableName);
            ObjectNode detailObject = (ObjectNode) (_ServeDaoConfig.tableViewMap).get(tableName);

            //STEP-IN::Get single table columns
            List<Map<String, Object>> columnsList = _ServeDaoConfig.daoHandler.queryForList(
                 "SELECT COLUMN_NAME,DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE,COLUMN_KEY, EXTRA,COLUMN_DEFAULT " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = '"+_ServeDaoConfig.dataBaseName+"' AND TABLE_NAME = '"+tableName+"' "
            );

            //STEP-IN::Insert list to _ServeDaoConfig.tableViewMap
            ObjectNode columnMap = TOOLS.JsonInitObject();
            ArrayNode keyList = TOOLS.JsonInitArray();
            for(Map<String, Object> item : columnsList){
                ObjectNode mapPiece = TOOLS.JsonInitObject();

                //STEP-IN-IN::Get column name
                String name = item.get("COLUMN_NAME").toString();
                //STEP-IN-IN::Get type
                String[] textType = {"tinytext", "text", "mediumtext", "longtext", "char", "varchar"};
                boolean isTextType = Arrays.stream(textType).anyMatch(str -> str != null && str.equals(item.get("DATA_TYPE").toString()));
                mapPiece.put("type", isTextType?"text":item.get("DATA_TYPE").toString());
                //STEP-IN-IN::Get text max length
                Long textMaxLength = item.get("CHARACTER_MAXIMUM_LENGTH") != null?(Long) item.get("CHARACTER_MAXIMUM_LENGTH"):0L;
                mapPiece.put("textMaxLength", textMaxLength);
                //STEP-IN-IN::Get whether the column can be null(Excluding self-increment)
                boolean isNullAble = item.get("IS_NULLABLE").toString().equals("YES");
                isNullAble = item.get("COLUMN_DEFAULT") != null || item.get("EXTRA").toString().contains("auto_increment") || isNullAble;
                mapPiece.put("isNullAble", isNullAble);
                //STEP-IN-IN::Get whether the column is key column
                boolean isKey = item.get("COLUMN_KEY").toString().equals("PRI");
                mapPiece.put("isKey", isKey);
                if(isKey) {
                    keyList.add(name);
                }

                //STEP-IN-IN::Insert to map
                columnMap.set(name, mapPiece);
            }
            detailObject.set("column", columnMap);
            detailObject.set("keyList", keyList);
            LOGGER.StraightInfo("Module-_ServeDao init, get table detail result:"+ detailObject.toString());
        }

        return ERRORCODE.ERR_OK;
    }

    public static void Start(){
        ERRORCODE result = ERRORCODE.ERR_OK;
        result = GetDataBaseName(result);
        result = GetTableAndView(result);
        result = GetMapDetail(result);
        _ServeDaoConfig.isGenerateSuccess = !ERRORCODE.IsError(result);
    }
}