#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append('../../../../')
sys.dont_write_bytecode = True
from Kernel.Module.LoadFile.LoadFile import LoadFile

if __name__ == '__main__':
    targetParam = [
        {
            'Xmas_target':'./test/folder_1/folder_1-1/', 
            'Xmas_url':'./test/folder_2/>>>/', 
            'Xmas_index':'Load.json', 
            'aa':'bb'
        }
    ]

    targetParam = LoadFile.Start(targetParam, {
        'mod_urlKey':'Xmas_url',
        'mod_indexKey':'Xmas_index',
        'mod_targetKey':'Xmas_target',
        'mod_replaceExtraKey':'Xmas_',
        'mod_isJudgeExist':False,
        'mod_isExistBackup':False
    })

    print(targetParam)