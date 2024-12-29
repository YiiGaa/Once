#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Config.Config import Config
import json
import urllib.request
import os
import random
import time
import re
from urllib.parse import quote

class LoadFile:
    def ErrorLog():
        print('Quit! Module LoadFile Error.')
        exit(-1)

    def LoadFile_Http(sourcePath, targetPath, replace, license):
        try:
            result = urllib.request.urlopen(f'{sourcePath}?license={license}')         
            lines = result.readlines()
            targetFile = open(targetPath, 'ab', 0o777)
            if len(lines)>=1 and 'License forbidden.' in str(lines[0]):
                print('')
                print('Error: The request is rejected, the license may not have been added, or the license has expired.')
                LoadFile.ErrorLog()
            for line in lines:
                try:
                    line = line.decode('utf-8')
                    for key,value in replace.items():
                        line = line.replace(key, value)
                    targetFile.write(line.encode('utf-8'))
                except:
                    targetFile.write(line)
            targetFile.close()
        except urllib.error.HTTPError as e:
            print(e)
            print('Error: loading fail')
            LoadFile.ErrorLog()

    def LoadFile_Local(sourcePath, targetPath, replace):
        with open(sourcePath, 'r', encoding='utf-8') as file:
            lines = file.readlines()
            targetFile = open(targetPath, 'a', 0o777)
            for line in lines:
                for key,value in replace.items():
                    line = line.replace(key, value)            
                targetFile.write(line)
            targetFile.close()
    
    def LoadVersion_Http(urlPath, license):
        print(f'{Config.logPrefix}{urlPath}')
        try:
            result = urllib.request.urlopen(f'{urlPath}?license={license}')
            content = result.read()
            if 'License forbidden.' in str(content):
                print('')
                print('Error: The request is rejected, the license may not have been added, or the license has expired.')
                LoadFile.ErrorLog()
            content = json.loads(content)
            return content
        except urllib.error.HTTPError as e:
            print(e)
            print('Error: loading fail')
        return {}
    
    def LoadVersion_File(path):
        localLoad = {}
        try:
            content = ''
            targetFile = open(path)
            line = targetFile.readline()
            while line:
                content = content + line
                line = targetFile.readline()
            targetFile.close()
            localLoad = json.loads(content)
        except:
            pass
        return localLoad
    
    def LoadVersion(path, license):
        if path[0] == '.' or path[0] == '/':
            return LoadFile.LoadVersion_File(path)
        else:
            return LoadFile.LoadVersion_Http(path, license)
    
    def CleanFile(filePath, isJudgeExist, isExistBackup):
        #STEP::Judge file is exists
        if not os.path.exists(filePath):
            return

        #STEP::Clean file
        enter = 'yes'
        if isJudgeExist == True:
            print(f'**warning: {filePath} already exists, continue generating?')
            cover = 'continue will be covered.' if isExistBackup == False else 'continue will be backed up.'
            print(cover)
            enter = input('yes/no: ')
            if enter != 'y' and enter != 'yes':
                print('Quit!')
                exit(-1)
        if (enter == 'y' or enter == 'yes') and os.path.exists(filePath):
            if isExistBackup == True:
                renameFile = f'{filePath}_bak_{time.strftime("%Y_%m_%d_%H_%M_%S", time.localtime())}'
                tryTime = 0
                while os.path.exists(renameFile) and tryTime < 20:
                    renameFile += '_'+random.randint(0,1000)
                    tryTime+=1
                if os.path.exists(renameFile)==False:
                    os.rename(filePath, renameFile)
            else:
                folderPath = os.path.dirname(filePath)
                os.remove(filePath)
                try:
                    os.removedirs(folderPath)
                except:
                    pass
    
    def IsInList(param, list):
        return any(param in string for string in list)
    
    def LoadFiles(targetParam, moduleParam, localPath, localLoadFile, updatePath, license):
        replaceExtraKey = moduleParam['mod_replaceExtraKey'] if 'mod_replaceExtraKey' in moduleParam else 'Xmas_'
        isJudgeExist = moduleParam['mod_isJudgeExist'] if 'mod_isJudgeExist' in moduleParam else True
        isExistBackup = moduleParam['mod_isExistBackup'] if 'mod_isExistBackup' in moduleParam else True
        indexKey = moduleParam['mod_indexKey'] if 'mod_indexKey' in moduleParam else ''
        index = targetParam[indexKey] if indexKey in targetParam else ''
        isUpdateLackContinue = moduleParam['mod_isUpdateLackContinue'] if 'mod_isUpdateLackContinue' in moduleParam else True
        index = targetParam[indexKey] if indexKey in targetParam else ''

        #STEP::Get update load file 
        updateLoadFile = LoadFile.LoadVersion(updatePath, license)
        #STEP::Judge is get update fail
        if updateLoadFile == {} and isUpdateLackContinue:
            print(f'{Config.logPrefix}skip:{updatePath}')
            return
        elif updateLoadFile == {}:
            LoadFile.ErrorLog()
            return
        
        localLoadFileName = (localPath+'/'+index).replace('//', '/')
        updateLoadFileName = updatePath
        updatePath = updatePath.rsplit(index, 1)[0]
        updateVersion = updateLoadFile['version'] if 'version' in updateLoadFile else 'none'
        print(f'{Config.logPrefix}target:{localPath}')
        print(f'{Config.logPrefix}update:{updatePath}, version:{updateVersion}')

        #STEP::Get skip file(Priority retention of local files) list
        customSkipList = localLoadFile['customSkip'] if 'customSkip' in localLoadFile and isinstance(localLoadFile['customSkip'], list) else []
        skipList = customSkipList
        skipLocalList = localLoadFile['skip'] if 'skip' in localLoadFile and isinstance(localLoadFile['skip'], list) else []
        skipLoadList = updateLoadFile['skip'] if 'skip' in updateLoadFile and isinstance(updateLoadFile['skip'], list) else []
        skipList = skipList+skipLoadList
        warningList = [item for item in skipLocalList if item not in skipLoadList]

        #STEP::Clean loacl file
        if 'file' in localLoadFile and isinstance(localLoadFile['file'], list):
            for item in localLoadFile['file']:
                filePath = localPath+item
                if not LoadFile.IsInList(item, skipList):
                    LoadFile.CleanFile(filePath, isJudgeExist, isExistBackup)
        elif 'file' in localLoadFile and not isinstance(localLoadFile['file'], list):
            print('Error: Illegal local file, quit!')
            LoadFile.ErrorLog()
        
        #STEP::Clean update file 
        if 'file' in updateLoadFile and isinstance(updateLoadFile['file'], list):
            for item in updateLoadFile['file']:
                filePath = localPath+item
                if not LoadFile.IsInList(item, skipList):
                    LoadFile.CleanFile(filePath, isJudgeExist, isExistBackup)
        elif 'file' in updateLoadFile and not isinstance(updateLoadFile['file'], list):
            print('Error: Illegal update file, quit!')
            LoadFile.ErrorLog()
        
        #STEP::Organize the data of replace
        replace = {}
        for key,value in targetParam.items():
            if replaceExtraKey not in key:
                replace[key] = value

        #STEP::Update file
        if 'file' in updateLoadFile and isinstance(updateLoadFile['file'], list):
            for item in updateLoadFile['file']:
                sourcePath = updatePath+item
                targetPath = localPath+item
                if updateLoadFileName == targetPath:
                    continue
                if os.path.exists(targetPath) and LoadFile.IsInList(item, skipList):
                    print(f'{Config.logPrefix}skip {item}')
                    continue
                checkDir = ''
                for item_2 in targetPath.split('/'):
                    checkDir += item_2
                    if checkDir == targetPath:
                        break
                    checkDir += '/'
                    if os.path.exists(checkDir)==False and os.path.isfile(targetPath)==False:
                        os.mkdir(checkDir, 0o777)
                print(f'{Config.logPrefix}>> {item}')
                if sourcePath[0] == '.' or sourcePath[0] == '/':
                    LoadFile.LoadFile_Local(sourcePath, targetPath, replace)
                else:
                    sourcePath = updatePath+quote(item)
                    LoadFile.LoadFile_Http(sourcePath, targetPath, replace, license)
        elif 'file' in updateLoadFile and not isinstance(updateLoadFile['file'], list):
            print('Error: Illegal local file, quit!')
            LoadFile.ErrorLog()
        
        #STEP::Write Load.json
        updateLoadFile['customSkip'] = customSkipList
        with open(localLoadFileName, 'w', encoding='utf-8') as f:
            json.dump(updateLoadFile, f, indent=4)
        
        #STEP::Warnning, 文件在本地load.json中包含在skip，但在更新的load.json中没有
        #STEP::Warnning, Some files is included in 'skip' of the local Load.json, but not in the updated Load.json.
        if warningList != []:
            print('')
            print(f'**warning: Some files is included in \'skip\' of the local Load.json, but not in the updated Load.json. These files has been replace or delelte')
            for item in warningList:
                print(f'{Config.logPrefix}- {item}')
            print('')

    def DoStart(targetParam, moduleParam):
        targetKey = moduleParam['mod_targetKey'] if 'mod_targetKey' in moduleParam else ''
        urlKey = moduleParam['mod_urlKey'] if 'mod_urlKey' in moduleParam else ''
        indexKey = moduleParam['mod_indexKey'] if 'mod_indexKey' in moduleParam else ''
        isUpdateLackContinue = moduleParam['mod_isUpdateLackContinue'] if 'mod_isUpdateLackContinue' in moduleParam else True
        licenseKey = moduleParam['mod_licenseKey'] if 'mod_licenseKey' in moduleParam else ''

        targetPath = targetParam[targetKey] if targetKey in targetParam else ''
        urlPath = targetParam[urlKey] if urlKey in targetParam else ''
        index = targetParam[indexKey] if indexKey in targetParam else ''
        targetPath = (targetPath+'/').replace('//', '/')
        targetFile = targetPath+index
        urlFile = (urlPath+'/'+index)
        urlFile = re.sub(r'(?<!:)//', '/', urlFile)
        license = targetParam[licenseKey] if licenseKey in targetParam else ''

        #STEP::Get local load file
        localLoadFile = LoadFile.LoadVersion(targetFile, license)
        localVersion = localLoadFile['version'] if 'version' in localLoadFile else 'none'
        print(f'{Config.logPrefix}local:{targetPath}, version:{localVersion}')

        #STEP::Comfirm update version
        if '...' in urlFile or '>>>' in urlFile or '!!!' in urlFile:
            updateFileList = urlFile.replace('...', index).replace('>>>', index).replace('!!!', index)
            updateFileList = updateFileList.split(index)
            updatePath = ''
            for item in updateFileList[:-2]:
                updatePath = updatePath+item+index
                updateList = LoadFile.LoadVersion(updatePath, license)

                #STEP-IN::Judge is get update fail
                if updateList == {} and isUpdateLackContinue:
                    print(f'{Config.logPrefix}skip update:{updatePath}')
                    return targetParam
                elif updateList == {}:
                    LoadFile.ErrorLog()
                    return targetParam

                #STEP-IN::Comfirm ...(need user select)
                if urlFile.startswith(updatePath.replace(index, '...')):
                    print(f'{Config.logPrefix}**Update selectable versions({updatePath}): ')
                    for item_1 in updateList:
                        print(f'{Config.logPrefix}{Config.logPrefix}{item_1}')
                    enter = input(f'{Config.logPrefix}version select: ')

                    if '...' in targetPath:
                        targetPath = targetPath.replace('...', enter, 1)
                        targetFile = (targetPath+'/'+index).replace('//', '/')
                        localLoadFile = LoadFile.LoadVersion(targetFile, license)

                    if enter == '>>>':
                        versionIndex = 0
                        try:
                            versionIndex = updateList.index(localLoadFile['version'])
                            versionIndex = versionIndex - 1 if versionIndex>0 else 0
                        except:
                            versionIndex = len(updateList)-1
                        enter = updateList[versionIndex]
                    elif enter == '!!!':
                        enter = updateList[0]

                    if enter in updateList:
                        updatePath = updatePath.replace(index, enter)
                        urlFile = urlFile.replace('...', enter, 1)
                    else:
                        print('Illegal version, quit!')
                        exit(-1)

                #STEP-IN::Comfirm >>>(get next version)
                elif urlFile.startswith(updatePath.replace(index, '>>>')):
                    versionIndex = 0
                    try:
                        versionIndex = updateList.index(localLoadFile['version'])
                        versionIndex = versionIndex - 1 if versionIndex>0 else 0
                    except:
                        versionIndex = len(updateList)-1
                    updatePath = updatePath.replace(index, updateList[versionIndex])
                    urlFile = urlFile.replace('>>>', updateList[versionIndex], 1)
                    if '>>>' in targetPath:
                        targetPath = targetPath.replace('>>>', updateList[versionIndex], 1)
                        targetFile = (targetPath+'/'+index).replace('//', '/')
                        localLoadFile = LoadFile.LoadVersion(targetFile, license)
                
                #STEP-IN::Comfirm !!!(get last version)
                elif urlFile.startswith(updatePath.replace(index, '!!!')):
                    versionIndex = 0
                    updatePath = updatePath.replace(index, updateList[versionIndex])
                    urlFile = urlFile.replace('!!!', updateList[versionIndex], 1)
                    if '!!!' in targetPath:
                        targetPath = targetPath.replace('!!!', updateList[versionIndex], 1)
                        targetFile = (targetPath+'/'+index).replace('//', '/')
                        localLoadFile = LoadFile.LoadVersion(targetFile, license)
            updatePath = updatePath+'/'+index
            LoadFile.LoadFiles(targetParam, moduleParam, targetPath, localLoadFile, updatePath, license)
        else:
            LoadFile.LoadFiles(targetParam, moduleParam, targetPath, localLoadFile, urlFile, license)
        return targetParam

    def Start(targetParam, moduleParam):
        if isinstance(targetParam, list):
            result = {}
            for item in targetParam:
                result = LoadFile.DoStart(item, moduleParam)
                print('')
            return result
        else: 
            return LoadFile.DoStart(targetParam, moduleParam)