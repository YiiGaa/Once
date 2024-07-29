#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.GetTemplate.GetTemplate import GetTemplate

if __name__ == '__main__':
    targetParam = [{
        'document':{
            'Xmas_templ':'DOCUMENT-NORMAL',
            'function':{
                'Xmas_templ':'FUNCTION-NORMAL',
                'param':{
                    'param_1':'1',
                    'param_2':'2'
                }
            }
        }
    }]

    templatePath = {
        'document':'./test/test.templ',
        'function':'./test/test.templ'
    }

    targetParam = GetTemplate.Start(targetParam, {
        'mod_templateKey':'Xmas_templ',
        'mod_templatePath':templatePath
    })

    print(targetParam)