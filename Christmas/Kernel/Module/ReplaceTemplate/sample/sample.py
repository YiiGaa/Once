#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.ReplaceTemplate.ReplaceTemplate import ReplaceTemplate

if __name__ == '__main__':
    targetParam = [{
        'document':{
            'Xmas_templ':'#!/usr/bin/env python3\n# -*- coding: UTF-8 -*-\n\nclass GetTemplate:\n    @@function@@\n',
            'function':{
                'Xmas_templ':'    def Start(targetParam, moduleParam):\n        return GetTemplate.DoStart(targetParam, moduleParam)\n @@container@@',
                'param':{
                    'param_1':'1',
                    'param_2':'2'
                }
            }
        }
    }]

    targetParam = ReplaceTemplate.Start(targetParam, {
        'mod_templateKey':'Xmas_templ'
    })

    print(targetParam)