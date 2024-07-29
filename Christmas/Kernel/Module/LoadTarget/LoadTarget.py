#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Config.Config import Config
import glob
import re
import copy


class LoadTarget:
    def ErrorLog():
        print('Quit! Module LoadTarget Error.')
        exit(-1)

    def DoStart(targetParam, moduleParam):
        try:
            targetKey = moduleParam['mod_targetKey'] if 'mod_targetKey' in moduleParam else ''
            urlKey = moduleParam['mod_urlKey'] if 'mod_urlKey' in moduleParam else ''
            indexKey = moduleParam['mod_indexKey'] if 'mod_indexKey' in moduleParam else ''

            targetPath = targetParam[targetKey] if targetKey in targetParam else ''
            urlPath = targetParam[urlKey] if urlKey in targetParam else ''
            index = targetParam[indexKey] if indexKey in targetParam else ''
            # targetPath = (targetPath+'/'+index).replace('//', '/')
            # urlPath = (urlPath+'/'+index).replace('//', '/')

            if '*' in targetPath:
                resultList = []
                fileList = glob.glob(targetPath)
                matchKey = targetPath.replace('*', '(.*?)')
                if matchKey.startswith('(.*?)'):
                    matchKey = '(.*)' + matchKey[5:]
                if matchKey.endswith('(.*?)'):
                    matchKey = matchKey[:-5] + '(.*)'
                
                for filePath in fileList:
                    replaceCount = urlPath.count('*')
                    findKey = re.findall(matchKey, filePath)
                    findCount = len(findKey)
                    if findCount >0:
                        if not isinstance(findKey[0], str):
                            findKey = findKey[0]
                            findCount = len(findKey)

                    resultUrlPath = urlPath
                    if findCount>0 and findCount<replaceCount:
                        for i in range(replaceCount):
                            if i < findCount:
                                resultUrlPath = resultUrlPath.replace('*', findKey[i], 1)
                            else:
                                resultUrlPath = resultUrlPath.replace('*', findKey[findCount-1], 1)
                    elif findCount==0 and findCount<replaceCount:
                        print(f'Illegal {urlKey} settings, {urlKey}:{urlPath}')
                        LoadTarget.ErrorLog()
                    else:
                        for item in findKey[findCount-replaceCount:]:
                            resultUrlPath = resultUrlPath.replace('*', item, 1)
                    resultPiece = copy.deepcopy(targetParam)
                    resultPiece[targetKey] = filePath
                    resultPiece[urlKey] = resultUrlPath
                    resultPiece[indexKey] = index
                    print(f'{Config.logPrefix}{filePath} << {resultUrlPath}')
                    resultList.append(resultPiece)
                targetParam = resultList
            else:
                targetParam[targetKey] = targetPath
                targetParam[urlKey] = urlPath
                targetParam[indexKey] = index
                print(f'{Config.logPrefix}{targetPath} << {urlPath}')
                return targetParam
        except Exception as e:
            print(f'Error: {e}')
            LoadTarget.ErrorLog()
        return targetParam

    def Start(targetParam, moduleParam):
        return LoadTarget.DoStart(targetParam, moduleParam)