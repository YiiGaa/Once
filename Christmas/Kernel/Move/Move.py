#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Move.CleanCode.CleanCode import CleanCode
from Kernel.Move.DownloadCode.DownloadCode import DownloadCode
from Kernel.Move.GenCode.GenCode import GenCode
from Kernel.Move.MergeCode.MergeCode import MergeCode
from Kernel.Move.ShellExcute.ShellExcute import ShellExcute
from Kernel.Config.Config import Config
import json
import re

class Move:
    map = {
        'CleanCode': CleanCode,
        'DownloadCode': DownloadCode,
        'GenCode': GenCode,
        'MergeCode': MergeCode,
        'ShellExcute': ShellExcute
    }

    def DefaultConfig(param):
        defaultConfig = {
            'targetFile':'',
            'isJudgeExist':True,
            'isExistBackup':True,
			'isFillLack':False,
			'fillLackMap':{},
            'templatePath':{}
        }
        for key,value in defaultConfig.items():
            if key not in param:
                param[key] = value
        return param

    def ErrorLog(param):
        print('Quit! '+param)
        exit(-1)
    
    def ReadConfig(param):
        content = ''
        try:
            targetFile = open(param, encoding='utf-8')
            line = targetFile.readline()
            while line:
                content = content + line
                line = targetFile.readline()
            targetFile.close()
            content = json.loads(content)

        except Exception as e:
            print(f'Error: {e}')
            Move.ErrorLog('load '+param+' fail')
        return content

    def ReplaceSetting_Check(content, inputSign, isFailEnd):
        if re.search(rf'\{inputSign}(\d+?)\{inputSign}', content):
            maches = re.search(rf'\{inputSign}(\d+?)\{inputSign}', content)
            if isFailEnd:
                Move.ErrorLog(f'Please input sufficient param! Param not met: {maches[0]}')
            else:
                return False

        if re.search( rf'\{inputSign}\{inputSign}', content):
            if isFailEnd:
                Move.ErrorLog(f'Please input sufficient param! Param not met: {inputSign}{inputSign}')
            else:
                return False
        return True
    
    def ReplaceSetting(target, config, setting):
        if 'inputSign' in config:
            inputSign = config['inputSign']
            rewaitInput = config['rewaitInput'] if 'rewaitInput' in config else None
            content = json.dumps(target)

            for list in range(len(setting)):
                if list == 0:
                    content = content.replace(f'{inputSign}{inputSign}', setting[list].replace('"','\\"'))
                content = content.replace(f'{inputSign}{list+1}{inputSign}', setting[list].replace('"','\\"'))
            
            if not Move.ReplaceSetting_Check(content, inputSign, False if rewaitInput else True):
                print('')
                print('※ Please input necessary param.')
                print(f'※ Param tip: {rewaitInput}')

                content = json.dumps(target)
                setting = input('')
                print('')
                setting = re.findall(r'"([^\\"]*(?:\\.[^\\"]*)*)"|(\S+)', setting)

                paramList = []
                for list in range(len(setting)):
                    paramList.append('')
                    for value in setting[list]:
                        if value != '':
                            paramList[list] = value

                for list in range(len(paramList)):
                    if list == 0:
                        content = content.replace(f'{inputSign}{inputSign}', paramList[list])
                    content = content.replace(f'{inputSign}{list+1}{inputSign}', paramList[list])
                
                Move.ReplaceSetting_Check(content, inputSign, True)
            
            target = json.loads(content)
        return target      

    def Start(action, setting):
        print('STEP::Load Target & Config')
        targetParam = {}
        configParam = {}
        isFileConfig = False
        isCommonConfig = False
        execute = None
        targetPath = None

        for list in action.split('/'):
            if list in Move.map:
                isCommonConfig = True
                execute = Move.map[list]
                targetPath = '/' + list
                continue
            if targetPath != None:
                isFileConfig = True
                targetPath = targetPath + '/' + list
                break

        if isFileConfig == True:
            print(f'{Config.logPrefix}file mode.')

            loadPath = f'{Config.inputPath}{targetPath}{Config.configFile}'
            print(f'{Config.logPrefix}loading '+loadPath)
            configParam = Move.ReadConfig(loadPath)
            configParam = Move.DefaultConfig(configParam)

            loadPath = f'{Config.inputPath}{targetPath}{Config.targetFile}'
            if configParam['targetFile'] != '':
                loadPath = configParam['targetFile']
            print(f'{Config.logPrefix}loading '+loadPath)
            targetParam = Move.ReadConfig(loadPath)

            print(f'{Config.logPrefix}replace setting')
            targetParam = Move.ReplaceSetting(targetParam, configParam, setting)
            execute.Start(targetParam, configParam)
        elif isCommonConfig == True:
            print(f'{Config.logPrefix}command line mode.')
            
            print(f'{Config.logPrefix}loading target setting')
            try:
                targetParam = json.loads(setting[0])
            except Exception as e:
                print(f'Error: {e}')
                Move.ErrorLog('load '+setting[0]+' as hash fail')

            print(f'{Config.logPrefix}loading config setting')
            try:
                configParam = json.loads(setting[1])
                configParam = Move.DefaultConfig(configParam)
            except Exception as e:
                print(f'Error: {e}')
                Move.ErrorLog('load '+setting[1]+' as hash fail')
            execute.Start(targetParam, configParam)
        else:
            print('false')