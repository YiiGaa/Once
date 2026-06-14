#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Common.Config.Config import Config
from Kernel.Common.Logger.Logger import Logger
import os
import time
import subprocess

class ShellExcute:
    def ErrorLog():
        Logger.Error('Quit! Module ShellExcute Error.')
        exit(-1)
    
    def Excute(param, isJudge, inputList, cwdPath, commandError):
        isSuccess = False
        try:
            print('')
            print(f'● Excute >> {param}, cwdPath:{cwdPath}')
            
            if inputList:
                handler = subprocess.Popen(param, bufsize=1, shell=True, text=True, cwd=cwdPath, stdout=subprocess.PIPE, stdin=subprocess.PIPE)
                line = handler.stdout.readline()
                while line:
                    print(f'{line}', end='')
                    if inputList:
                        for key,value in inputList.items():
                            if key in line:
                                print(f'↳ Input >> {value}')
                                handler.stdin.write(f'{value}\n')
                                break
                    line = handler.stdout.readline()
                result = handler.wait()
                isSuccess = True if result == 0 else False
            else:
                handler = subprocess.Popen(param, bufsize=1, shell=True, text=True, cwd=cwdPath)
                result = handler.wait()
                isSuccess = True if result == 0 else False

        except Exception as e:
            print(f'Error: {e}')
            ShellExcute.ErrorLog()

        if isSuccess == False and commandError != '':
            print(f'↳ Last shell error execute')
            ShellExcute.Excute(commandError, False, inputList, cwdPath, '')

        if isJudge == True and isSuccess == False:
            print('Quit! Command Error occur.')
            exit(-1)

    def Traverse(param, commandKey, judgeKey, inputExtraKey, cwdKey, errorKey):
        if isinstance(param, list):
            for index in range(len(param)):
                ShellExcute.Traverse(param[index], commandKey, judgeKey, inputExtraKey, cwdKey, errorKey)
        elif isinstance(param, dict):
            command = ''
            commandError = ''
            cwdPath = None
            inputList = {}
            isJudge = False
            for key,value in param.items():
                if key == judgeKey:
                    if value == True:
                        isJudge = True
                elif isinstance(value, str):
                    if key == commandKey:
                        command = value
                    elif key == errorKey:
                        commandError = value
                    elif key == cwdKey:
                        cwdPath = value
                    elif inputExtraKey not in key:
                        inputList[key] = value
                else:
                    ShellExcute.Traverse(value, commandKey, judgeKey, inputExtraKey, cwdKey, errorKey)
            if command != '':
                ShellExcute.Excute(command, isJudge, inputList, cwdPath, commandError)
        elif isinstance(param, str):
            ShellExcute.Excute(param, False, {}, None, '')

    def DoStart(targetParam, moduleParam):
        commandKey = moduleParam['mod_commandKey'] if 'mod_commandKey' in moduleParam else 'Xmas_shell'
        cwdKey = moduleParam['mod_cwdKey'] if 'mod_cwdKey' in moduleParam else 'Xmas_cwd'
        judgeKey = moduleParam['mod_judgeKey'] if 'mod_judgeKey' in moduleParam else 'Xmas_judge'
        errorKey = moduleParam['mod_errorKey'] if 'mod_errorKey' in moduleParam else 'Xmas_error'
        inputExtraKey = moduleParam['mod_inputExtraKey'] if 'mod_inputExtraKey' in moduleParam else 'Xmas_'

        ShellExcute.Traverse(targetParam, commandKey, judgeKey, inputExtraKey, cwdKey, errorKey)
        return targetParam

    def Start(targetParam, moduleParam):
        return ShellExcute.DoStart(targetParam, moduleParam)