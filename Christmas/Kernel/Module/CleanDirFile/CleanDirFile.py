#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import json
from Kernel.Config.Config import Config
import os
import time
import random
import shutil

class CleanDirFile:
    def ErrorLog():
        print('Quit! Module CleanDirFile Error.')
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
            CleanDirFile.ErrorLog()
    
    def Clean(targetFile, isClean, isNewDir):
        if os.path.isfile(targetFile):
            if isClean == True:
                print(f'{Config.logPrefix}{targetFile} as File >> clean')
                os.remove(targetFile)
            else:
                print(f'{Config.logPrefix}{targetFile} as File >> backup')
                CleanDirFile.BackUp(targetFile)
        if os.path.exists(targetFile):
            if isClean == True:
                print(f'{Config.logPrefix}{targetFile} as Dir >> clean')
                shutil.rmtree(targetFile)
            else:
                print(f'{Config.logPrefix}{targetFile} as Dir >> backup')
                CleanDirFile.BackUp(targetFile)
            if isNewDir == True:
                os.makedirs(targetFile, 0o777)

    def DoStart(targetParam, moduleParam):
        targeteKey = moduleParam['mod_targetKey'] if 'mod_targetKey' in moduleParam else 'Xmas_clean'
        isNewDir = moduleParam['mod_isNewDir'] if 'mod_isNewDir' in moduleParam else True

        if targeteKey in targetParam:
            cleanList = targetParam[targeteKey]
            if isinstance(cleanList, dict):
                for key,value in cleanList.items():
                    if value == 'backup':
                        CleanDirFile.Clean(key, False, isNewDir)
                    else:
                        CleanDirFile.Clean(key, True, isNewDir)
            if isinstance(cleanList, list):
                for value in cleanList:
                    CleanDirFile.Clean(value, True, isNewDir)
            if isinstance(cleanList, str):
                CleanDirFile.Clean(cleanList, True, isNewDir)

        return targetParam

    def Start(targetParam, moduleParam):
        return CleanDirFile.DoStart(targetParam, moduleParam)