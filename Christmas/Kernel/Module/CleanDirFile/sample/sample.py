#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append('../../../../')
sys.dont_write_bytecode = True
from Kernel.Module.CleanDirFile.CleanDirFile import CleanDirFile

if __name__ == '__main__':
    targetParam = {
        'Xmas_clean':{
            './test':'clean'
        },
        'Xmas_clean_1':'./test',
        'Xmas_clean_2':['./test', './test_1']
    }

    targetParam = CleanDirFile.Start(targetParam, {
        'mod_targetKey':'Xmas_clean'
    })

    print(targetParam)