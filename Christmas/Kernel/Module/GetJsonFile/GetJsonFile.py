#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import json

class GetJsonFile:
    def DoStart(dataPool, moduleParam):
        mod_path = moduleParam['mod_path'] if 'mod_path' in moduleParam else 'Xmas_templ'
        mod_target = moduleParam['mod_target'] if 'mod_target' in moduleParam else 'GetJsonFile'
        mod_isToHash = moduleParam['mod_isToHash'] if 'mod_isToHash' in moduleParam else False

        content = ''
        targetFile = open(mod_path, encoding='utf-8')
        line = targetFile.readline()
        while line:
            content = content + line
            line = targetFile.readline()
        targetFile.close()

        content = json.loads(content) if mod_isToHash == True else content
        dataPool[mod_target] = content

    def Start(dataPool, moduleParam):
        GetJsonFile.DoStart(dataPool, moduleParam)