#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.GenFile.GenFile import GenFile

if __name__ == '__main__':
    targetParam = {
        'Xmas_task':[
            {
                'Xmas_target':'./test1/<1>/',
                'Xmas_source':[
                    {
                        'Xmas_path':'./test/<1>/<2>/find.txt', 'Xmas_contain':'', 'Xmas_except':'', 'Xmas_filter':'',
                        '<!-- {1}:{2} -->':[
                            {'Xmas_path':'./test/floor1_1/text1.txt', 'Xmas_contain':'', 'Xmas_except':'', 'Xmas_filter':'###'}
                        ]
                    }
                ]
            }
        ]
    }

    targetParam = GenFile.Start(targetParam, {
        'mod_taskKey':'Xmas_task',
        'mod_outFileKey':'Xmas_target',
        'mod_sourceKey':'Xmas_source',
        'mod_filterKey':'Xmas_filter',
        'mod_pathKey':'Xmas_path',
        'mod_containKey':'Xmas_contain',
        'mod_exceptKey':'Xmas_except',
        'mod_replaceExtraKey':'Xmas_',
        'mod_isJudgeExist':True,
        'mod_isExistBackup':True,
    })

    #print(targetParam)