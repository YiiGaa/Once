#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import json
from Kernel.Config.Config import Config

class ReplaceConfig:
    def ErrorLog():
        print('Quit! Module ReplaceConfig Error.')
        exit(-1)

    def DoStart(targetParam, moduleParam):
        templateKey = moduleParam['mod_configKey'] if 'mod_configKey' in moduleParam else 'Xmas_config'

        content = json.dumps(targetParam)
        if templateKey in targetParam:
            replaceList = targetParam[templateKey]
            if isinstance(replaceList, dict):
                for key,value in replaceList.items():
                    print(f'{Config.logPrefix}{key} >> {value}')
                    content = content.replace(key, value)
        return json.loads(content)

    def Start(targetParam, moduleParam):
        return ReplaceConfig.DoStart(targetParam, moduleParam)