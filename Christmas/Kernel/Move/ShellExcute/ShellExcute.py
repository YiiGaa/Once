#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Module.Module import Module

class ShellExcute:
    def DoStart(targetParam, configParam):
        print('STEP::Excute Shell Command')
        targetParam = Module.Start('ShellExcute', targetParam, {
            'mod_commandKey':'Xmas_shell',
            'mod_cwdKey':'Xmas_cwd',
            'mod_judgeKey':'Xmas_judge',
            "mod_inputExtraKey":'Xmas_'
        })

    def Start(targetParam, configParam):
        ShellExcute.DoStart(targetParam, configParam)
        print('')
        print('SUCCESS')