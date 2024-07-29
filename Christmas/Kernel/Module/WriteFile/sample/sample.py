#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

import sys
sys.path.append("../../../../")
sys.dont_write_bytecode = True
from Kernel.Module.WriteFile.WriteFile import WriteFile

if __name__ == '__main__':
    targetParam = [{
        'Xmas_path':'./test/output.txt',
        'document':'#!/usr/bin/env python3\n# -*- coding: UTF-8 -*-\n\nclass GetTemplate:\n        def Start(targetParam, moduleParam):\n        return GetTemplate.DoStart(targetParam, moduleParam)\n\n'
    }]

    targetParam = WriteFile.Start(targetParam, {
        'mod_fileKey':'Xmas_path',
        "mod_isJudgeExist":True,
        "mod_isExistBackup":False
    })

    print(targetParam)