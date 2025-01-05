#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import json
from Kernel.Config.Config import Config
import os
import time
import random
import shutil
import fnmatch
import re
import glob

class GenFile:
    def ErrorLog():
        print('Quit! Module GenFile Error.')
        exit(-1)

    def BackUp(targetFile):
        renameFile = f'{targetFile}_bak_{time.strftime("%Y_%m_%d_%H_%M_%S", time.localtime())}'
        tryTime = 0
        while os.path.isfile(renameFile) and tryTime < 20:
            renameFile += '_'+random.randint(0,1000)
            tryTime+=1
        if os.path.isfile(renameFile)==False:
            os.rename(targetFile, renameFile)
        else:
            print('Error! BackUp fail. Please try again after 1 second.')
            GenFile.ErrorLog()

    def GenFile_FindList_Check(param, contain, excepts):
        if param == '':
            return True

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

    def GenFile_FindList_Select(tempPath, keyTarget, matchPath, outPath):
        keyParm = {}
        if matchPath != '' and keyTarget:
            maches = re.match(rf'{matchPath}', tempPath)
            if maches:
                index = 0
                for value in maches.groups():
                    if index < len(keyTarget):
                        keyParm[keyTarget[index]] = value
                    index += 1
        for key,value in keyParm.items():
            outPath = outPath.replace(f'{key}', value)
        return outPath

    markFile = []
    def GenFile_FindList(param, outPath, isSingleOut, isOutput):
        findList = {}
        path = param[GenFile.pathKey] if GenFile.pathKey in param else ''
        orginPath = path
        contain = param[GenFile.containKey] if GenFile.containKey in param else ''
        excepts = param[GenFile.exceptKey] if GenFile.exceptKey in param else ''
        
        if path != '':
            matchPath = ''
            keyTarget = None
            if '<' in path and '>' in path:
                keyTarget = re.findall(r'(\<.*?\>)', path)
                matchPath = re.sub(r'\<.*?\>', '(.*?)', path)
                if matchPath.startswith('(.*?)'):
                    matchPath = '(.*)' + matchPath[5:]
                if matchPath.endswith('(.*?)'):
                    matchPath = matchPath[:-5] + '(.*)'
                path = re.sub(r'\<.*?\>', '*', path)
            
            findPath = ''
            for value in path.split('/'):
                if '*' in value:
                    break
                findPath = findPath + value 
                findPath = findPath + '/' if findPath != path else findPath

            result = glob.glob(path)
            allResult = result
            for value in result:
                if os.path.isfile(value):
                    continue
                else:
                    allResult += glob.glob(value+'**' if value[-1]=='/' else value+'/**')

            for value in allResult:
                if os.path.isfile(value):
                    if isSingleOut:
                        if GenFile.GenFile_FindList_Check(value.replace(findPath, ''), contain, excepts):
                            findList[value] = GenFile.GenFile_FindList_Select(value, keyTarget, matchPath, outPath)
                    else:
                        tempOutPath = GenFile.GenFile_FindList_Select(value, keyTarget, matchPath, outPath)
                        tempInPath = GenFile.GenFile_FindList_Select(value, keyTarget, matchPath, orginPath)
                        if os.path.isfile(tempInPath):
                            tempPath = ''
                            for piece in tempInPath.split('/'):
                                if tempPath + piece == tempInPath:
                                    break
                                else:
                                    tempPath+=piece+'/'
                            tempInPath = tempPath
                        elif tempInPath[-1] != '/':
                            tempInPath += '/'

                        if GenFile.GenFile_FindList_Check(value.replace(findPath, ''), contain, excepts):
                            findList[value] = value.replace(tempInPath, tempOutPath)

                    if isOutput == True and value in findList and os.path.isfile(findList[value]) and findList[value] not in GenFile.markFile:
                        enter = 'yes'
                        if GenFile.isJudgeExist == True:
                            print(f'**warning: {findList[value]} already exists, continue generating?')
                            cover = 'continue will be covered.' if GenFile.isExistBackup == False else 'continue will be backed up.'
                            print(cover)
                            enter = input('yes/no: ')
                            if enter != 'y' and enter != 'yes':
                                print('Quit!')
                                exit(-1)
                        if (enter == 'y' or enter == 'yes') and os.path.exists(findList[value]):
                            if GenFile.isExistBackup == True:
                                renameFile = f'{findList[value]}_bak_{time.strftime("%Y_%m_%d_%H_%M_%S", time.localtime())}'
                                tryTime = 0
                                while os.path.isfile(renameFile) and tryTime < 20:
                                    renameFile += '_'+random.randint(0,1000)
                                    tryTime+=1
                                if os.path.isfile(renameFile)==False:
                                    os.rename(findList[value], renameFile)
                            else:
                                os.remove(findList[value])
                    if value in findList and findList[value] not in GenFile.markFile:
                        GenFile.markFile.append(findList[value])
                elif not isSingleOut:
                    tempOutPath = GenFile.GenFile_FindList_Select(value, keyTarget, matchPath, outPath)
                    tempInPath = GenFile.GenFile_FindList_Select(value, keyTarget, matchPath, orginPath)
                    if not os.path.isfile(tempInPath) and tempInPath[-1]!='/':
                        tempInPath += '/' 
                    if GenFile.GenFile_FindList_Check(value.replace(findPath, ''), contain, excepts):
                        tempOutPath = value.replace(tempInPath, tempOutPath)               
                        if os.path.exists(tempOutPath)==False:
                            os.makedirs(tempOutPath, 0o777)
            if findList == {}:
                print(f'**warning: in Xmas_path({path}), no file found!')

        for key,value in findList.items():
            checkDir = ''
            for list in value.split('/'):
                checkDir += list
                if checkDir == value:
                    break
                checkDir += '/'
                if os.path.exists(checkDir)==False and os.path.isfile(checkDir)==False:
                    os.mkdir(checkDir, 0o777)  

        return findList
    
    def GenFile_Output_Replace_AddSpace(line, replaceKey, tempContent):
        if '\n' in tempContent:
            blank = re.findall(rf'^\s+{replaceKey}|\n\s+{replaceKey}', line)
            tempMark = []
            for list_1 in blank:
                list_1 = list_1.replace('\n', '').replace(f'{replaceKey}', '')
                tempVlaue = list_1 + tempContent.replace('\n', '\n'+list_1)
                tempMark.append(tempVlaue)
                line = line.replace(f'{list_1}{replaceKey}', '@Christmas_mulitline_replace@', 1)
            line = line.replace(replaceKey, tempContent)
            for list_1 in tempMark:
                line = line.replace('@Christmas_mulitline_replace@', list_1, 1)
        else:
            line = line.replace(replaceKey, tempContent)
        return line
    
    def GenFile_Output_Replace(line, replace, outPath):
        for key,value in replace.items():
            matchKey = ''
            keyTarget = None
            if '{' in key and '}' in key:
                keyTarget = re.findall(r'(\{.*?\})', key)
                matchKey = key.replace('(', '\\(')
                matchKey = matchKey.replace(')', '\\)')
                matchKey = re.sub(r'\{.*?\}', '(.*?)', matchKey)
                if matchKey.startswith('(.*?)'):
                    matchKey = '(.*)' + matchKey[5:]
                if matchKey.endswith('(.*?)'):
                    matchKey = matchKey[:-5] + '(.*)'
                maches = re.findall(rf'{matchKey}', line)

                for list_1 in maches:
                    replaceKey = key
                    nextValue = value
                    if isinstance(list_1, tuple):
                        for index_2 in range(len(list_1)):
                            if index_2 < len(keyTarget):
                                replaceKey = replaceKey.replace(keyTarget[index_2], list_1[index_2], 1)
                                if isinstance(nextValue, dict) or isinstance(nextValue, list):
                                    nextValue = json.dumps(nextValue)
                                    nextValue = nextValue.replace(keyTarget[index_2], list_1[index_2])
                                    nextValue = json.loads(nextValue)
                                elif isinstance(nextValue, str):
                                    nextValue = nextValue.replace(keyTarget[index_2], list_1[index_2])
                    else:
                        replaceKey = replaceKey.replace(keyTarget[0], list_1, 1)
                        if isinstance(nextValue, dict) or isinstance(nextValue, list):
                            nextValue = json.dumps(nextValue)
                            nextValue = nextValue.replace(keyTarget[0], list_1)
                            nextValue = json.loads(nextValue)
                        elif isinstance(nextValue, str):
                            nextValue = nextValue.replace(keyTarget[0], list_1)
                    
                    if replaceKey in line:
                        if isinstance(nextValue, dict) or isinstance(nextValue, list):
                            tempContent = GenFile.GenFile(nextValue, outPath, True, False)
                            line = GenFile.GenFile_Output_Replace_AddSpace(line, replaceKey, tempContent)
                        else:
                            line = line.replace(replaceKey, nextValue)
                    
            else:
                if key in line:
                    if isinstance(value, dict) or isinstance(value, list):
                        tempContent = GenFile.GenFile(value, outPath, True, False)
                        line = GenFile.GenFile_Output_Replace_AddSpace(line, key, tempContent)
                    else:
                        line = line.replace(key, value)
        return line

    def GenFile_Output_File(outPath, filePath, filterStr, selectStr, replace, isOutput):
        content = ''
        #STEP::Copy file when not require select text
        if isOutput == True and not replace and filterStr == '' and not os.path.exists(outPath):
            print(f'{Config.logPrefix}{outPath}')
            if os.path.isfile(filePath):
                shutil.copy(filePath, outPath)
            else:
                shutil.copytree(filePath, outPath)
            return content

        #STEP::Add \n when output file exist
        if isOutput == True:
            isAddLine = True if os.path.exists(outPath) else False
            outFile = open(outPath, 'a', 0o777, encoding='utf-8')
            if isAddLine:
                outFile.write('\n')
        
        #STEP::When no need file text
        if filterStr == '-' and selectStr == '-':
            content = GenFile.GenFile_Output_Replace(content, replace, outPath)
            content += '\n'
            return content

        #STEP::Open file and select text
        sourceFile = open(filePath, encoding='utf-8')
        line = sourceFile.readline()
        isFirstLine = True
        isMark = False if filterStr != '' else True
        while line:
            if filterStr != '' and filterStr in line:
                isMark = not isMark
            elif isMark == True:
                if selectStr != '':
                    maches = re.finditer(rf'{selectStr}', line)
                    tmpContent = ''
                    for match in maches: 
                        tmpContent += match.group()
                    line = tmpContent
                    if line == '':
                        line = sourceFile.readline()
                        continue
                    else:
                        line += '\n'

                line = GenFile.GenFile_Output_Replace(line, replace, outPath)
                if isOutput == False:
                    content += line
                else:
                    if filterStr != '' or selectStr != '':
                        if not isFirstLine:
                            line = '\n'+line
                        line = line.removesuffix('\n')
                        isFirstLine = False
                    outFile.write(line)
            line = sourceFile.readline()
        
        sourceFile.close()
        if isOutput == True:
            print(f'{Config.logPrefix}{outPath}')
            outFile.close()
        return content
    
    def GenFile_Output_PathFind(param, path, tempPath):
        if '<' in path and '>' in path:
            param = json.dumps(param)
            keyTarget = re.findall(r'(\<.*?\>)', path)
            matchPath = re.sub(r'\<.*?\>', '(.*?)', path)
            if matchPath.startswith('(.*?)'):
                matchPath = '(.*)' + matchPath[5:]
            if matchPath.endswith('(.*?)'):
                matchPath = matchPath[:-5] + '(.*)'
            maches = re.match(rf'{matchPath}', tempPath)
            if maches:
                index = 0
                for value in maches.groups():
                    if index < len(keyTarget):
                        param = param.replace(keyTarget[index], value)
                    index += 1
            param = json.loads(param)
        return param
      
    def GenFile_Output(param, fileList, isOutput):
        content = ''
        replace = {}
        filterStr = param[GenFile.filterKey] if GenFile.filterKey in param else ''
        selectStr = param[GenFile.selectKey] if GenFile.selectKey in param else ''
        if not isinstance(filterStr, str):
            filterStr = ''
        if not isinstance(selectStr, str):
            selectStr = ''
        for key,value in param.items():
            if GenFile.replaceExtraKey not in key:
                replace[key] = value
        for key,value in fileList.items(): 
            tempReplace = replace
            if GenFile.pathKey in param:
                tempReplace = GenFile.GenFile_Output_PathFind(replace, param[GenFile.pathKey], key)
            content += GenFile.GenFile_Output_File(value, key, filterStr, selectStr, tempReplace, isOutput)  
        return content   
    
    def GenFile(param, outPath, isSingleOut, isOutput):
        content = ''
        if isinstance(param, list):
            for index in range(len(param)):
                content = content + GenFile.GenFile(param[index], outPath, isSingleOut, isOutput) + '\n'
        elif isinstance(param, dict):
            fileList = GenFile.GenFile_FindList(param, outPath, isSingleOut, isOutput)
            content = GenFile.GenFile_Output(param, fileList, isOutput)
        elif isinstance(param, str):
            content = param
        else:
            content = str(param)
        return content.removesuffix('\n')

    def DoStart(param, outFileKey, sourceKey):
        if isinstance(param, list):
            for value in param:
                GenFile.DoStart(value, outFileKey, sourceKey)
        elif isinstance(param, dict):
            if outFileKey in param:
                if sourceKey not in param:
                    param[sourceKey] = ''
                outPath = param[outFileKey]
                isSingleOut = False if outPath[-1] == '/' else True

                GenFile.GenFile(param[sourceKey], param[outFileKey], isSingleOut, True)
            else:
                GenFile.DoStart(param, outFileKey, sourceKey)
    
    filterKey = ''
    selectKey = ''
    pathKey = ''
    containKey = ''
    exceptKey = ''
    replaceExtraKey = ''
    isJudgeExist = ''
    isExistBackup = ''

    def Start(targetParam, moduleParam):
        taskKey = moduleParam['mod_taskKey'] if 'mod_taskKey' in moduleParam else 'Xmas_task'
        outFileKey = moduleParam['mod_outFileKey'] if 'mod_outFileKey' in moduleParam else 'Xmas_target'
        sourceKey = moduleParam['mod_sourceKey'] if 'mod_sourceKey' in moduleParam else 'Xmas_source'
        
        GenFile.filterKey = moduleParam['mod_filterKey'] if 'mod_filterKey' in moduleParam else 'Xmas_filter'
        GenFile.selectKey = moduleParam['mod_selectKey'] if 'mod_selectKey' in moduleParam else 'Xmas_select'
        GenFile.pathKey = moduleParam['mod_pathKey'] if 'mod_pathKey' in moduleParam else 'Xmas_path'
        GenFile.containKey = moduleParam['mod_containKey'] if 'mod_containKey' in moduleParam else 'Xmas_contain'
        GenFile.exceptKey = moduleParam['mod_exceptKey'] if 'mod_exceptKey' in moduleParam else 'Xmas_except'

        GenFile.replaceExtraKey = moduleParam['mod_replaceExtraKey'] if 'mod_replaceExtraKey' in moduleParam else 'Xmas_'

        GenFile.isJudgeExist = moduleParam['mod_isJudgeExist'] if 'mod_isJudgeExist' in moduleParam else True
        GenFile.isExistBackup = moduleParam['mod_isExistBackup'] if 'mod_isExistBackup' in moduleParam else True

        if taskKey in targetParam:
            taskParam = targetParam[taskKey]
            GenFile.DoStart(taskParam, outFileKey, sourceKey)
        return targetParam