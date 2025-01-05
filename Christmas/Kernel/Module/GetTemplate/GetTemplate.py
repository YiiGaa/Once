#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import json
from Kernel.Config.Config import Config

class GetTemplate:
    def ErrorLog():
        print('Quit! Module GetTemplate Error.')
        exit(-1)

    def ReadFile(fitlerKey, frontKey, templateFile):
        content = ''
        isMark = False
        try:
            targetFile = open(templateFile, encoding='utf-8')
            line = targetFile.readline()
            while line:
                if f'######{fitlerKey}######' in line:
                    isMark = not isMark
                elif isMark == True:
                    content = content + line
                line = targetFile.readline()
            if content != '':
                print(f'{Config.logPrefix}{frontKey} >> {fitlerKey}')
            targetFile.close()
        except Exception as e:
            print(f'Error: {e}')
            GetTemplate.ErrorLog()
        return content.removesuffix('\n')

    def Traverse(param, frontKey, templateKey, templatePath):
        if isinstance(param, list):
            if not param and frontKey in templatePath:
                return ''
            for index in range(len(param)):
                param[index] = GetTemplate.Traverse(param[index], frontKey, templateKey, templatePath)
        elif isinstance(param, dict):
            if param and frontKey in templatePath and templateKey not in param:
                param[templateKey] = 'DEFAULT'
            if not param and frontKey in templatePath:
                return ''
            for key,value in param.items():
                if key == templateKey:
                    if isinstance(value, str):
                        if frontKey in templatePath:
                            param[key] = GetTemplate.ReadFile(value, frontKey, templatePath[frontKey])
                else:
                    param[key] = GetTemplate.Traverse(value, key, templateKey, templatePath)
        elif isinstance(param, str):
            if frontKey in templatePath:
                content = GetTemplate.ReadFile('DEFAULT', frontKey, templatePath[frontKey])
                if '@@value@@' in content:
                    param = {
                        templateKey: content,
                        'value': param
                    }
        return param

    def DoStart(targetParam, moduleParam):
        templateKey = moduleParam['mod_templateKey'] if 'mod_templateKey' in moduleParam else ''
        templatePath = moduleParam['mod_templatePath'] if 'mod_templatePath' in moduleParam else {}

        targetParam = GetTemplate.Traverse(targetParam, '', templateKey, templatePath)
        return targetParam

    def Start(targetParam, moduleParam):
        return GetTemplate.DoStart(targetParam, moduleParam)