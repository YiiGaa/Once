#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.dont_write_bytecode = True
from Kernel.Move.Move import Move
from Kernel.Config.Config import Config
import os
import json

def PrintVersion():
    print('')
    with open('./Load.json', 'r', encoding='utf-8') as file:
        content = json.load(file)
        content = content['version']
        print(f'Christmas v{content}')
    print('A general code generator')
    print('gitHub: https://github.com/YiiGaa/Christmas')
    print('official: https://stoprefactoring.com')
    print('Licensed under MIT')
    print('Designed by stoprefactoring.com')
    print('')


if __name__ == '__main__':
    if not (sys.version_info.major >= 3 and sys.version_info.minor >= 9):
        print('Error! Require python 3.9 or above.')
        print('Current: '+sys.version)
        exit(-1)
    
    if len(sys.argv)>1:
        rootPath = sys.argv[0]
        if rootPath != "Christmas.py" and rootPath != "./Christmas.py":
            rootPath = rootPath.replace('Christmas.py','')
            os.chdir(rootPath)
        PrintVersion()
        action = sys.argv[1]
        setting = []
        if os.name == 'nt':
            action = action.replace('\\', '/')
        for list in sys.argv[2:]:
            setting.append(list)
        Move.Start(action, setting)
    else:
        PrintVersion()
        print("Please input function parameters.")
        print("Like: python3 Christmas.py Input/GenCode/xxx")
        print("Input/GenCode/xxx is function parameters.")
        sys.exit(-1)
    sys.exit(0)