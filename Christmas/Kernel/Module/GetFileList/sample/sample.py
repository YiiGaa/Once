#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append('../../../../')
sys.dont_write_bytecode = True
from Kernel.Module.GetFileList.GetFileList import GetFileList

if __name__ == '__main__':
    targetParam = [   
        {
            "Xmas_path":"./test",
            "Xmas_contain":"test1,test2",
            "Xmas_except":""
        },{
            "Xmas_path":"./test",
            "Xmas_contain":"",
            "Xmas_except":"find1_2"
        }
    ]

    targetParam = GetFileList.Start(targetParam, {
        'mod_targetKey':'',
        'mod_resultKey':'Xmas_clean',
        'mod_pathKey':'Xmas_path',
        'mod_containKey':'Xmas_contain',
        'mod_exceptKey':'Xmas_except',
    })

    print(targetParam)