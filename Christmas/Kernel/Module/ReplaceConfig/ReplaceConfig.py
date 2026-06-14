#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
import Kernel.Common.Lib.json5 as json5
import json
from Kernel.Common.Config.Config import Config
from Kernel.Common.Logger.Logger import Logger

class ReplaceConfig:
    def ErrorLog():
        Logger.Error('Quit! Module ReplaceConfig Error.')
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
        return json5.loads(content)

    def Start(targetParam, moduleParam):
        return ReplaceConfig.DoStart(targetParam, moduleParam)