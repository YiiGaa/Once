#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Module.Module import Module

class GenCode:
    def DoStart(targetParam, configParam):
        print('STEP::Read Template')
        targetParam = Module.Start('GetTemplate', targetParam, {
            'mod_templateKey':'Xmas_templ',
            'mod_templatePath':configParam['templatePath']
        })

        print('STEP::Recursive Processing')
        targetParam = Module.Start('ReplaceTemplate', targetParam, {
            'mod_templateKey':'Xmas_templ'
        })

        print('STEP::Write File')
        targetParam = Module.Start('WriteFile', targetParam, {
            'mod_fileKey':'Xmas_path',
            'mod_isJudgeExist':configParam['isJudgeExist'],
            'mod_isExistBackup':configParam['isExistBackup'],
            'mod_isFillLack':configParam['isFillLack'],
            'mod_fillLackMap':configParam['fillLackMap']
        })

    def Start(targetParam, configParam):
        GenCode.DoStart(targetParam, configParam)
        print('')
        print('SUCCESS')