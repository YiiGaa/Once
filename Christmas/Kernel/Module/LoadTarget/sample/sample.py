#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append('../../../../')
sys.dont_write_bytecode = True
from Kernel.Module.LoadTarget.LoadTarget import LoadTarget

if __name__ == '__main__':
    targetParam = {
        "Xmas_target":"./test/*/*/",
        "Xmas_url":"http://192.168.3.200:8001/framework-module/hive/*/.../",
        "Xmas_index":"Load.json",
        "aa":"bb"
    }

    targetParam = LoadTarget.Start(targetParam, {
        'mod_targetKey':'Xmas_target',
        'mod_urlKey':'Xmas_url',
        'mod_indexKey':'Xmas_index'
    })

    print(targetParam)