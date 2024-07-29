#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Module.Module import Module

class DownloadCode:
    def DoStart(targetParam, configParam):
        print('STEP::Get Local Target')
        targetParam = Module.Start('LoadTarget', targetParam, {
            'mod_targetKey':'Xmas_target',
            'mod_urlKey':'Xmas_url',
            'mod_indexKey':'Xmas_index'
        })

        print('STEP::Update Target')
        targetParam = Module.Start('LoadFile', targetParam, {
            'mod_urlKey':'Xmas_url',
            'mod_indexKey':'Xmas_index',
            'mod_targetKey':'Xmas_target',
            'mod_licenseKey':'Xmas_license',
            'mod_replaceExtraKey':'Xmas_',
            'mod_isJudgeExist':configParam['isJudgeExist'],
            'mod_isExistBackup':configParam['isExistBackup']
        })

    def Start(targetParam, configParam):
        DownloadCode.DoStart(targetParam, configParam)
        print('')
        print('SUCCESS')