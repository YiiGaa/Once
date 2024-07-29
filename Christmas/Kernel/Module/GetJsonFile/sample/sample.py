#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.GetJsonFile.GetJsonFile import GetJsonFile

if __name__ == '__main__':
    dataPool = {}

    GetJsonFile.Start(dataPool, {
        'mod_path':'test/test.json',
        'mod_target':"target",
        'mod_isToHash':True
    })

    print(dataPool)