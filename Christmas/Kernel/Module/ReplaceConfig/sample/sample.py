#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.ReplaceConfig.ReplaceConfig import ReplaceConfig

if __name__ == '__main__':
    targetParam = {
        'Xmas_config':{
            '@test_1@':'aa',
            '@test_2@':'bb'
        },
        'key_1':{
            '@test_1@':'aa',
            'key_2':[
                '@test_2@'
            ]
        }
    }

    targetParam = ReplaceConfig.Start(targetParam, {
        'mod_configKey':'Xmas_config'
    })

    print(targetParam)