#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Config.Config import Config
import json
import os
import time
import random
import re

class WriteFile:
    def ErrorLog():
        print('Quit! Module WriteFile Error.')
        exit(-1)

    def FillingLack(content, isFillLack, fillLackMap):
        if isFillLack != True:
            return content
        for key,value in fillLackMap.items():
            content = content.replace(f'@@{key}@@', str(value))
        content = re.sub(r'@@.*?@@', "", content)
        return content

    def Output(param, fileKey, extraKey, isJudgeExist, isExistBackup, isFillLack, fillLackMap):
        content = ''
        targetFile = param[fileKey]
        if targetFile[-1] == '/':
            targetFile+='target.txt'
        
        try:
            for key,value in param.items():
                if key != fileKey and extraKey not in key:
                    content += value + '\n'
            content = content.removesuffix('\n')
            content = WriteFile.FillingLack(content, isFillLack, fillLackMap)
            
            enter = 'yes'
            if isJudgeExist == True and os.path.isfile(targetFile):
                print(f'**warning: {targetFile} already exists, continue generating?')
                cover = 'continue will be covered.' if isExistBackup == False else 'continue will be backed up.'
                print(cover)
                enter = input('yes/no: ')
                if enter != 'y' and enter != 'yes':
                    print('Quit!')
                    exit(-1)
            if (enter == 'y' or enter == 'yes') and os.path.exists(targetFile):
                if isExistBackup == True:
                    renameFile = f'{targetFile}_bak_{time.strftime("%Y_%m_%d_%H_%M_%S", time.localtime())}'
                    tryTime = 0
                    while os.path.isfile(renameFile) and tryTime < 20:
                        renameFile += '_'+random.randint(0,1000)
                        tryTime+=1
                    if os.path.isfile(renameFile)==False:
                        os.rename(targetFile, renameFile)

            checkDir = ''
            for list in targetFile.split('/'):
                checkDir += list
                if checkDir == targetFile:
                    break
                checkDir += '/'
                if os.path.exists(checkDir)==False and os.path.isfile(targetFile)==False:
                    os.mkdir(checkDir, 0o777)

            print(f'{Config.logPrefix}{targetFile}')
            targetFile = open(targetFile, 'w', 0o777)
            targetFile.write(content)
            targetFile.close()
        except Exception as e:
            print(f'Error: {e}')
            WriteFile.ErrorLog()

    def Traverse(param, fileKey, extraKey,  isJudgeExist, isExistBackup, isFillLack, fillLackMap):
        content = ''
        if isinstance(param, list):
            for index in range(len(param)):
                content += WriteFile.Traverse(param[index], fileKey, extraKey, isJudgeExist, isExistBackup, isFillLack, fillLackMap)
        elif isinstance(param, dict):
            isOutput = False
            for key,value in param.items():
                if isinstance(value, str):
                    if key == fileKey:
                        isOutput = True
                else:
                    param[key] = WriteFile.Traverse(value, fileKey, extraKey, isJudgeExist, isExistBackup, isFillLack, fillLackMap)

            if isOutput == True:
                WriteFile.Output(param, fileKey, extraKey, isJudgeExist, isExistBackup, isFillLack, fillLackMap)
                param = ''
            else:
                for key,value in param.items():
                    content += value
        else:
            content = str(param)

        return content

    def DoStart(targetParam, moduleParam):
        extraKey = moduleParam['mod_extraKey'] if 'mod_extraKey' in moduleParam else 'Xmas_'
        fileKey = moduleParam['mod_fileKey'] if 'mod_fileKey' in moduleParam else ''
        isJudgeExist = moduleParam['mod_isJudgeExist'] if 'mod_isJudgeExist' in moduleParam else True
        isExistBackup = moduleParam['mod_isExistBackup'] if 'mod_isExistBackup' in moduleParam else True
        isFillLack = moduleParam['mod_isFillLack'] if 'mod_isFillLack' in moduleParam else False
        fillLackMap = moduleParam['mod_fillLackMap'] if 'mod_fillLackMap' in moduleParam else False

        if isFillLack == True:
            print(f'{Config.logPrefix}>>Filling all lack setting:{fillLackMap}')

        targetParam = WriteFile.Traverse(targetParam, fileKey, extraKey, isJudgeExist, isExistBackup, isFillLack, fillLackMap)
        return targetParam

    def Start(targetParam, moduleParam):
        return WriteFile.DoStart(targetParam, moduleParam)