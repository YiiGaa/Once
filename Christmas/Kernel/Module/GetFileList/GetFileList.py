#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Config.Config import Config
import json
import os
import time
import random
import shutil
import os

class GetFileList:
    def ErrorLog():
        print('Quit! Module GetFileList Error.')
        exit(-1)

    def Find_Check(param, contain, excepts):
        isMark = True
        if contain != '':
            isMark = False
            for value in contain.split(','):
                if value in param:
                    isMark = True
        if isMark == False:
            return False

        if excepts != '':
            for value in excepts.split(','):
                if value in param:
                    return False

        return isMark 
    
    def Find(param, pathKey, containKey, exceptKey):
        fileList = []
        findPath = param[pathKey] if pathKey in param else ''
        contain = param[containKey] if containKey in param else ''
        excepts = param[exceptKey] if exceptKey in param else ''
                
        for root, dirs, files in os.walk(findPath, topdown=False):
            tempPath = ''
            for name in files:
                tempPath = os.path.join(root, name)
                if GetFileList.Find_Check(name, contain, excepts):
                    fileList.append(tempPath)
            for name in dirs:
                tempPath = os.path.join(root, name)+'/'
                if GetFileList.Find_Check(name, contain, excepts):
                    fileList.append(tempPath)

        if os.path.isfile(findPath):
            fileList.append(findPath)
        elif contain == '' and excepts == '' and os.path.exists(findPath):
            fileList.append(findPath)

        return fileList

    def DoStart(targetParam, moduleParam):
        targeteKey = moduleParam['mod_targetKey'] if 'mod_targetKey' in moduleParam else ''
        resultKey = moduleParam['mod_resultKey'] if 'mod_resultKey' in moduleParam else ''

        pathKey = moduleParam['mod_pathKey'] if 'mod_pathKey' in moduleParam else 'Xmas_path'
        containKey = moduleParam['mod_containKey'] if 'mod_containKey' in moduleParam else 'Xmas_contain'
        exceptKey = moduleParam['mod_exceptKey'] if 'mod_exceptKey' in moduleParam else 'Xmas_except'
        
        findList = []
        resultList = []
        if targeteKey != '' and targeteKey in targetParam:
            findList = targetParam[targeteKey]
        elif targeteKey == '':
            findList = targetParam
        
        if isinstance(findList, dict):
            resultList += GetFileList.Find(findList, pathKey, containKey, exceptKey)
        if isinstance(findList, list):
            for value in findList:
                resultList += GetFileList.Find(value, pathKey, containKey, exceptKey)
        if isinstance(findList, str):
            resultList += GetFileList.Find(findList, pathKey, containKey, exceptKey)
        
        if resultKey != '' and isinstance(targetParam, dict):
            targetParam[resultKey] = resultList
        elif resultKey != '':
            targetParam={resultKey:resultList}
        else:
            targetParam = resultList

        return targetParam

    def Start(targetParam, moduleParam):
        return GetFileList.DoStart(targetParam, moduleParam)