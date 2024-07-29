#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.ShellExcute.ShellExcute import ShellExcute

if __name__ == '__main__':
    targetParam = [
        {'Xmas_shell':'python3 a.py','wait':'in'},
        {
            'Xmas_shell':'ls',
            'Xmas_cwd':'..'
        }
    ]

    targetParam = ShellExcute.Start(targetParam, {
        'mod_commandKey':'Xmas_shell',
        'mod_cwdKey':'Xmas_cwd',
        'mod_judgeKey':'Xmas_judge',
        "mod_inputExtraKey":'Xmas_'
    })

    print(targetParam)