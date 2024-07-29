#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Module.Module import Module

class CleanCode:
    def DoStart(targetParam, configParam):
        print('STEP::Get Filelist')
        targetParam = Module.Start('GetFileList', targetParam, {
            'mod_targetKey':'',
            'mod_resultKey':'Xmas_clean',
            'mod_pathKey':'Xmas_path',
            'mod_containKey':'Xmas_contain',
            'mod_exceptKey':'Xmas_except',
        })

        print('STEP::Clean Files')
        targetParam = Module.Start('CleanDirFile', targetParam, {
            'mod_targetKey':'Xmas_clean',
            'mod_isNewDir':False
        })

    def Start(targetParam, configParam):
        CleanCode.DoStart(targetParam, configParam)
        print('')
        print('SUCCESS')