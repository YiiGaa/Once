#!/usr/bin/env python3
# -*- coding: UTF-8 -*-
from Kernel.Module.CleanDirFile.CleanDirFile import CleanDirFile
from Kernel.Module.GenFile.GenFile import GenFile
from Kernel.Module.GetFileList.GetFileList import GetFileList
from Kernel.Module.GetJsonFile.GetJsonFile import GetJsonFile
from Kernel.Module.GetTemplate.GetTemplate import GetTemplate
from Kernel.Module.LoadFile.LoadFile import LoadFile
from Kernel.Module.LoadTarget.LoadTarget import LoadTarget
from Kernel.Module.ReplaceConfig.ReplaceConfig import ReplaceConfig
from Kernel.Module.ReplaceTemplate.ReplaceTemplate import ReplaceTemplate
from Kernel.Module.ShellExcute.ShellExcute import ShellExcute
from Kernel.Module.WriteFile.WriteFile import WriteFile

class Module:
    map = {
        'CleanDirFile':CleanDirFile,
        'GenFile':GenFile,
        'GetFileList':GetFileList,
        'GetJsonFile': GetJsonFile,
        'GetTemplate': GetTemplate,
        'LoadFile':LoadFile,
        'LoadTarget':LoadTarget,
        'ReplaceConfig':ReplaceConfig,
        'ReplaceTemplate': ReplaceTemplate,
        'ShellExcute': ShellExcute,
        'WriteFile': WriteFile, 
    }

    def Start(action, dataPool, moduleParam):
        if action in Module.map:
            return Module.map[action].Start(dataPool, moduleParam)