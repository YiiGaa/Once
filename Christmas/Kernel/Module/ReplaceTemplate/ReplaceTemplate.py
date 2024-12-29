#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import json
from Kernel.Config.Config import Config
import re

class ReplaceTemplate:
    def ErrorLog():
        print('Quit! Module ReplaceTemplate Error.')
        exit(-1)
    
    def ReplaceTarget_AddSpace(line, replaceKey, tempContent):
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

    def ReplaceTarget(param, templateKey):
        content = param[templateKey]
        try:
            isReplaceHash = False
            listContent = ''
            if '@@key@@' in content and '@@value@@' in content:
                isReplaceHash = True

            for key,value in param.items():
                if key != templateKey:
                    if isinstance(value, dict) or isinstance(value, list):
                        value = json.dumps(value)
                    elif isinstance(value, int):
                        value = str(value)
                    elif isinstance(value, bool):
                        value = str(value)
                    if isReplaceHash:
                        tempContent = content.replace(f'@@key@@', key)
                        listContent += tempContent.replace(f'@@value@@', value) + '\n'
                    else:
                        content = ReplaceTemplate.ReplaceTarget_AddSpace(content, f'@@{key}@@', value)
            if isReplaceHash:
                content = listContent.removesuffix('\n')
        except Exception as e:
            print(f'Error: {e}')
            ReplaceTemplate.ErrorLog()
        return content

    def Traverse(param, frontKey, templateKey):
        if isinstance(param, list):
            isContract = False
            for index in range(len(param)):
                if isinstance(param[index], dict) or isinstance(param[index], list):
                    param[index] = ReplaceTemplate.Traverse(param[index], frontKey, templateKey)
                    if isinstance(param[index], str):
                        isContract = True
            if isContract == True:
                content = ''
                for value in param:
                    if isinstance(value, dict) or isinstance(value, list):
                        value = json.dumps(value)
                    elif isinstance(value, int):
                        value = str(value)
                    elif isinstance(value, bool):
                        value = str(value)
                    content += value + '\n'
                param = content.removesuffix('\n')
        elif isinstance(param, dict):
            isReplace = False
            for key,value in param.items():
                if isinstance(value, str):
                    if key == templateKey:
                        isReplace = True
                else:
                    param[key] = ReplaceTemplate.Traverse(value, key, templateKey)
            if isReplace == True:
                print(f'{Config.logPrefix}{frontKey}')
                param = ReplaceTemplate.ReplaceTarget(param, templateKey)
        return param

    def DoStart(targetParam, moduleParam):
        templateKey = moduleParam['mod_templateKey'] if 'mod_templateKey' in moduleParam else ''

        targetParam = ReplaceTemplate.Traverse(targetParam, '', templateKey)  
        return targetParam

    def Start(targetParam, moduleParam):
        return ReplaceTemplate.DoStart(targetParam, moduleParam)