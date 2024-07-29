#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Module.Module import Module

class MergeCode:
    def DoStart(targetParam, configParam):
        print('STEP::Replace Config')
        targetParam = Module.Start('ReplaceConfig', targetParam, {
            'mod_configKey':'Xmas_config'
        })

        print('STEP::Clean Dir or File')
        targetParam = Module.Start('CleanDirFile', targetParam, {
            'mod_targetKey':'Xmas_clean',
            'mod_isNewDir':True
        })

        print('STEP::Generate Files')
        targetParam = Module.Start('GenFile', targetParam, {
            'mod_taskKey':'Xmas_task',
            'mod_outFileKey':'Xmas_target',
            'mod_sourceKey':'Xmas_source',
            'mod_filterKey':'Xmas_filter',
            'mod_selectKey':'Xmas_select',
            'mod_pathKey':'Xmas_path',
            'mod_containKey':'Xmas_contain',
            'mod_exceptKey':'Xmas_except',
            'mod_replaceExtraKey':'Xmas_',
            'mod_isJudgeExist':configParam['isJudgeExist'],
            'mod_isExistBackup':configParam['isExistBackup'],
        })

        print('STEP::Clean Temporary Dir or File')
        targetParam = Module.Start('CleanDirFile', targetParam, {
            'mod_targetKey':'Xmas_cleanTemp',
            'mod_isNewDir':False
        })

    def Start(targetParam, configParam):
        MergeCode.DoStart(targetParam, configParam)
        print('')
        print('SUCCESS')