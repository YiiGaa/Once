#!/usr/bin/env python3
# -*- coding: UTF-8 -*-

from Kernel.Common.Logger.Logger import Logger
import Kernel.Common.Lib.json5 as json5
import json
import re
def GetSettings(path, fileName):
    try:
        with open(path, 'r', encoding='utf-8') as file:
            content = json5.load(file)
            for key,value in content.items():
                if re.search(key, fileName):
                    settings = value
                    if isinstance(settings, str):
                        try:
                            with open(settings, 'r', encoding='utf-8') as file:
                                settings = json5.load(file)
                        except Exception as e:
                            Logger.Info(f'Error: {e}')
                            Logger.Error(f'Load {settings} fail')
                    if isinstance(settings, dict):
                        return settings
                    else:
                        Logger.Error(f'Illegal \'{fileName}\' setting')
    except Exception as e:
        Logger.Info(f'Error: {e}')
        Logger.Error(f'Load {path} fail')
    
    Logger.Error(f'Can not find \'{fileName}\' setting in {path}')

Xmas_template = {}
Xmas_tab = 4
Xmas_sign = '<<'
Xmas_setting = []
Xmas_node = None
All_node = {}
def DealSetting(setting):
    #STEP::Check necessary setting
    if 'Xmas_template' not in setting or 'Xmas_setting' not in setting:
        Logger.Error(f'Missing necessary setting: Xmas_template, Xmas_setting')
    if not isinstance(setting['Xmas_setting'], dict):
        Logger.Error(f'Illegal Xmas_setting')

    #STEP::Get base setting
    global Xmas_sign, Xmas_template, Xmas_setting, Xmas_tab, Xmas_node
    Xmas_sign = setting.get('Xmas_sign') if isinstance(setting.get('Xmas_sign'), str) else '<<'
    Xmas_template = setting.get('Xmas_template', {})
    Xmas_setting = setting.get('Xmas_setting', {})
    Xmas_setting = Xmas_setting if isinstance(Xmas_setting, dict) else {}
    Xmas_tab = setting.get('Xmas_tab', 4)
    Xmas_tab = Xmas_tab if isinstance(Xmas_tab, int) and not isinstance(Xmas_tab, bool) else 4
    
    #STEP::Convert Xmas_setting
    nodeList = []
    nodeConfig = []
    global All_node
    for key, value in Xmas_setting.items():
        if len(key) > 1 or not isinstance(value, dict):
            continue

        #STEP-IN::Init item
        nodeList.append(key)
        nodeItem = {
            'key': key,
            'node': 'node',
            'node-set': None,
            'check': [],
            'template': None
        }
        nodeConfig.append(nodeItem)

        #STEP-IN::Convert node
        templCheck = value.get('Xmas_node', 'node')
        if isinstance(templCheck, str):
            if templCheck == 'node' or templCheck == 'leaf' or templCheck == 'root':
                nodeItem['node'] = templCheck
        elif isinstance(templCheck, list):
            nodeItem['node'] = 'node'
            for checkItem in templCheck:
                if not isinstance(checkItem, dict):
                    continue
                if isinstance(checkItem.get('line'), str) and isinstance(checkItem.get('match'), str):
                    if nodeItem['node-set'] == None:
                        nodeItem['node-set'] = []
                    nodeItem['node-set'].append({
                        'line': re.compile(checkItem.get('line')),
                        'match': re.compile(checkItem.get('match'))
                    })
        elif isinstance(templCheck, dict):
            nodeItem['node'] = 'root'
            nodeItem['node-set'] = {}
            if isinstance(templCheck.get('repeat'), str):
                nodeItem['node-set']['repeat'] = re.compile(templCheck.get('repeat'))
            if isinstance(templCheck.get('define'), str):
                nodeItem['node-set']['define'] = re.compile(templCheck.get('define'))
        if nodeItem['node'] == 'root':
            All_node[nodeItem['key']] = []

        #STEP-IN::Convert check
        templCheck = value.get('Xmas_check', None)
        if isinstance(templCheck, list):
            for checkItem in templCheck:
                if not isinstance(checkItem, dict):
                    continue
                node = checkItem.get('node', '')
                line = checkItem.get('line', '')
                isEmptyPass = checkItem.get('isEmptyPass', False)
                isEmptyPass = isEmptyPass if isEmptyPass==True else False
                appendix = checkItem.get('appendix', [])
                check = checkItem.get('check', [])
                if not isinstance(node, str) or not isinstance(line, str) or not isinstance(check, list):
                    continue
                checkList = []
                checkParamList = []
                for item in check:
                    if isinstance(item, str):
                        checkList.append(re.compile(item))
                    elif isinstance(item, list) and len(item) == 3 and isinstance(item[0], str) and isinstance(item[1], str) and isinstance(item[2], str):
                        checkParamList.append([item[0], item[1], re.compile(item[2])])
                appendixList = []
                if isinstance(appendix, list):
                    for item in appendix:
                        if not isinstance(item, dict):
                            continue
                        listSet = item.get('list', [])
                        if not isinstance(listSet, list):
                            continue
                        tempList = []
                        for listItem in listSet:
                            if isinstance(listItem, str):
                                tempList.append(re.compile(listItem))
                        matchKey = item.get('match', '')
                        matchKey = matchKey if isinstance(matchKey, str) else ''
                        appendixList.append({  
                            'match': re.compile(matchKey),
                            'list': tempList,
                        })
                nodeItem['check'].append({
                    'node': re.compile(node),
                    'line': re.compile(line),
                    'check': checkList,
                    'check-param': checkParamList,
                    'isEmptyPass': isEmptyPass,
                    'appendix': appendixList
                })
        
        #STEP-IN::Convert template
        templCheck = value.get('Xmas_template', None)
        if isinstance(templCheck, list):
            for item in templCheck:
                if not isinstance(item, dict):
                    continue
                node = item.get('node', '')
                if not isinstance(node, str) or not 'template' in item:
                    continue
                if nodeItem['template'] == None:
                    nodeItem['template'] = []
                nodeItem['template'].append({
                    'node': re.compile(node),
                    'template': item['template']
                })
        if nodeItem['template'] == None:
            Logger.Error(f'The node({key}) lacks Xmas_template')
    Xmas_setting = nodeConfig

    #STEP::Convert nodeList to regex
    if len(nodeList) > 0:
        pattern = "|".join(re.escape(element) for element in nodeList)
        Xmas_node = re.compile(f"^\\s*({pattern})")
    else:
        Logger.Error(f'Illegal Xmas_setting, no valid node')
    if len(All_node) == 0:
        Logger.Error(f'Illegal Xmas_setting, no valid \'root\' node')

All_rootDefine = {}
All_rootRepeat = {}
All_nodeMap = []
def Convert_CheckNode(lineText, lineNo):
    global All_rootDefine, All_rootRepeat, All_nodeMap
    global Xmas_setting, Xmas_sign, Xmas_tab

    #STEP::Check \t(illegal)
    if re.match(r'^\s*\t\s*', lineText):
        Logger.Error(f'Can not use tab(\\t), indentation units(spaces): {Xmas_tab}\nlineNo: {lineNo}\n↪{lineText}')
    
    #STEP::Get intend
    match = re.match(r'^ *', lineText)
    intent = len(match.group(0)) if match else 0
    if len(All_nodeMap) > 0:
        for item in reversed(All_nodeMap):
            if item['intent'] < intent:
                if item['template'] == None or item['node'] == None:
                    return {
                        'key': '',
                        'intent': intent,
                        'line': lineText,
                        'template': None,
                        'node': None,
                        'check': [],
                        'param': None,
                        'lineNo': lineNo,
                        'match': False,
                        'pair':[]
                    }
                break
    if intent % Xmas_tab != 0:
        Logger.Error(f'Please unify the indentation units, units(spaces): {Xmas_tab}\nlineNo: {lineNo}\n↪{lineText}')
    lineText = re.sub(r'^ *', '', lineText)

    #STEP::Match node type
    nodeSetting = None
    for item in Xmas_setting:
        if not lineText.startswith(item['key']):
            continue
        nodeSetting = item
    if nodeSetting == None:
        Logger.Error(f'Can not find this node setting\nlineNo: {lineNo}\n↪{lineText}')

    #STEP::Init node
    template = nodeSetting['template']
    targetTemplate = None
    isAllowParam = False
    isAllowNode = False
    isAllowPair = False
    if template !=None:
        for item in template:
            if re.search(item['node'], lineText):
                targetTemplate = item['template']
                templateText = json.dumps(targetTemplate)
                if templateText.find(f'{Xmas_sign}param') != -1:
                    isAllowParam = True
                if templateText.find(f'{Xmas_sign}node') != -1:
                    isAllowNode = True
                if templateText.find(f'{Xmas_sign}pair') != -1:
                    isAllowPair = True
                break
    node = {
        'key': nodeSetting['key'],
        'intent': intent,
        'line': lineText,
        'template': targetTemplate,
        'node': [] if isAllowNode else None,
        'check': nodeSetting['check'],
        'param': '' if isAllowParam else None,
        'lineNo': lineNo,
        'match': False,
        'pair': [] if isAllowPair else None,
    }
    
    #WHEN::"root" type
    if nodeSetting['node'] == 'root':
        if intent != 0:
            Logger.Error(f'The root node needs to be aligned at the top\nlineNo: {lineNo}\n↪{lineText}')
        if nodeSetting['node-set'] != None and 'repeat' in nodeSetting['node-set']:
            match = re.search(nodeSetting['node-set']['repeat'], lineText)
            if match:
                global All_rootRepeat
                if nodeSetting['key'] not in All_rootRepeat:
                    All_rootRepeat[nodeSetting['key']] = [match.group(0)]
                elif match.group(0) not in All_rootRepeat[nodeSetting['key']]:
                    All_rootRepeat[nodeSetting['key']].append(match.group(0))
                else:
                    Logger.Error(f'Root node repeated definition\nlineNo: {lineNo}\n↪{lineText}')
        if nodeSetting['node-set'] != None and 'define' in nodeSetting['node-set']:
            match = re.search(nodeSetting['node-set']['define'], lineText)
            if match:
                global All_rootDefine
                if nodeSetting['key'] not in All_rootDefine:
                    All_rootDefine[nodeSetting['key']] = [match.group(0)]
                elif match.group(0) not in All_rootDefine[nodeSetting['key']]:
                    All_rootDefine[nodeSetting['key']].append(match.group(0))
        return node
    
    #STEP::Check node  
    if intent == 0:
        Logger.Error(f'This node must be under the \'root\' node\nlineNo: {lineNo}\n↪{lineText}')

    #STEP::Check pair "node"
    pairNode = None
    if nodeSetting['node'] == 'node' and nodeSetting['node-set']:
        for item in nodeSetting['node-set']:
            if re.search(item['line'], lineText):
                pairNode = item['match']
                break
    
    #WHEN::Need to check pairNode
    if pairNode != None:
        isPass = False
        for item in reversed(All_nodeMap):
            if item['intent'] < intent:
                break
            if item['intent'] == intent:
                if re.search(pairNode, item['line']):
                    isPass = True
                    if item['template'] == None or item['pair'] == None:
                        node['template'] = None
                        node['node'] = None
                        node['param'] = None
                        node['pair'] = None
                    else:
                        node['match'] = True
                    break
        if not isPass:
            Logger.Error(f'This node needs to match other node\nlineNo: {lineNo}\n↪{lineText}')
        return node
    
    #WHEN::Normal node
    for item in reversed(All_nodeMap):
        if item['intent'] < intent:
            if item['node'] == 'leaf':
                node['template'] = None
                node['node'] = None
                node['param'] = None
                node['pair'] = None
                return node
            break
    return node

All_recheckLine = []
def Convert_CheckLine(node, line, isNode, lineNo):
    isPass = True
    isNeedReCheck = False
    for checkItem in node['check']:
        if not re.search(checkItem['node'], node['line']):
            continue
        if not re.search(checkItem['line'], line):
            continue
        isPass = False
        if checkItem['isEmptyPass'] == True and len(checkItem['check']) == 0 and len(checkItem['check-param']) == 0:
            isPass = True
        for item in checkItem['check']:
            if re.search(item, line):
                isPass = True
                break
        if len(checkItem['check-param']) > 0:
            isNeedReCheck = True
        if not isPass:
            break
        for item in checkItem['appendix']:
            if len(item["list"]) == 0:
                continue
            matches = re.finditer(item['match'], line)
            results = [match.group(0) for match in matches]  
            for item_match in results:
                isPass = False
                for listItem in item['list']:
                    if re.search(listItem, item_match):
                        isPass = True
                        break
                if not isPass:
                    Logger.Error(f'Illegal setting(appendix)\nlineNo: {lineNo}\n↪{line}')
        break
    if not isPass and isNeedReCheck:
        global All_recheckLine
        All_recheckLine.append({
            'line': line,
            'node': node,
            'isNode': isNode,
            'lineNo': lineNo
        })
    elif not isPass:
        Logger.Error(f'Illegal setting\nlineNo: {lineNo}\n↪{line}')

def Convert_DefineCheck():
    global All_recheckLine, All_rootDefine, Xmas_sign
    for item in All_recheckLine:
        line = item['line']
        node = item['node']
        lineNo = item['lineNo']
        isPass = False
        for checkItem in node['check']:
            if not re.search(checkItem['node'], node['line']):
                continue
            if not re.search(checkItem['line'], line):
                continue
            for paramCheck in checkItem['check-param']:
                if paramCheck[1] not in All_rootDefine:
                    Logger.Error(f'Illegal setting(root({paramCheck[1]}) not found)\nlineNo: {lineNo}\n↪{line}')
                for defineItem in All_rootDefine[paramCheck[1]]:
                    match = re.search(paramCheck[2], defineItem)
                    if match:
                        targetCheck = paramCheck[0].replace(f'{Xmas_sign}0', match.group(0))
                        allGroups = match.groups()
                        for i in range(len(allGroups)):
                            targetCheck = targetCheck.replace(f'{Xmas_sign}{i+1}', match.group(i+1))
                        if re.search(rf'{targetCheck}', line):
                            isPass = True
                            break
                if isPass:
                    break
            if isPass:
                break
        if not isPass:
            Logger.Error(f'Illegal setting(root define)\nlineNo: {lineNo}\n↪{line}')

def Convert_TemplateTraverse(template, param, lineContent, node, lineNo, pair=None):
    global Xmas_sign
    #WHEN::dict{}
    if isinstance(template, dict):
        result = {}
        for key, value in template.items():
            value = Convert_TemplateTraverse(value, param, lineContent, node, lineNo, pair)
            keyArr = key.split(Xmas_sign, 3)
            if value == None:
                if keyArr[0] == "opt":
                    continue   
                if len(keyArr) == 2:
                    try:
                        result[keyArr[1]] = json5.loads(keyArr[0])
                    except:
                        result[keyArr[1]] = keyArr[0]
                else:
                    Logger.Error(f'Missing necessary parameter settings({keyArr[0]})\nlineNo: {lineNo}\n↪{lineContent}')
                continue
            else:
                if keyArr[0] == "opt":
                    del keyArr[0]
                if len(keyArr) == 1:
                    result[keyArr[0]] = value
                else:
                    try:
                        defaultParam = json5.loads(keyArr[0])
                    except:
                        defaultParam = keyArr[0]
                    if not isinstance(defaultParam, str) and isinstance(value, str):
                        try:
                            value = json5.loads(value)
                        except:
                            pass
                    if type(defaultParam) is type(value):
                        result[keyArr[1]] = value
                    else:
                        result[keyArr[1]] = defaultParam
        return result
    #WHEN::list[]
    elif isinstance(template, list):
        for i, item in enumerate(template):
            value = Convert_TemplateTraverse(item, param, lineContent, node, lineNo, pair)
            template[i] = value
    #WHEN:Processing insertion expression
    elif isinstance(template, str) and template.startswith(Xmas_sign):
        content = template.removeprefix(Xmas_sign)
        if content == 'node':
            return node if isinstance(node, list) else None
        elif content.startswith(f'node{Xmas_sign}'):
            key = content.removeprefix(f'node{Xmas_sign}')
            return node[key] if isinstance(node, dict) else None
        elif content == 'param':
            return param
        elif content.startswith(f'param{Xmas_sign}'):
            key = content.removeprefix(f'param{Xmas_sign}')
            return param[key] if param!=None and key in param else None
        elif content.startswith(f'pair{Xmas_sign}'):
            pairKey = content.removeprefix(f'pair{Xmas_sign}')
            if pair == None and not isinstance(pair, list):
                return None
            pairList = []
            for item in pair:
                if re.search(rf'{pairKey}', item['line']):
                    pairList.append(item['node'])
            return pairList if len(pairList)>0 else None
        elif lineContent !=None and content.startswith(f'list{Xmas_sign}'):
            key = content.removeprefix(f'list{Xmas_sign}')
            matches = re.findall(rf'{key}', lineContent)
            result = None
            for i, match in enumerate(matches, 1):
                if result == None:
                    result = []
                result.append(match)
            return result
        elif lineContent !=None:
            pattern = rf'^(\d+){re.escape(Xmas_sign)}(.+)$'
            match = re.match(pattern, content)
            if match:
                number = int(match.group(1))
                text = match.group(2)  
                match = re.search(rf'{text}', lineContent)
                if match:
                    allGroups = match.groups()
                    if len(allGroups) <= number:
                        return match.group(number)
                return None
            else:
                match = re.search(rf'{content}', lineContent)
                if match:
                    return match.group(0)
                else:
                    return None
    return template

import Kernel.Common.Lib.yaml as yaml
def Convert_Template(node):
    global All_nodeMap

    #STEP::Clean last node
    result = None
    isPair = False
    if len(All_nodeMap) > 0:
        for item in reversed(All_nodeMap):
            if result != None and item['node'] != None and not isPair:
                item['node'].append(result)   
            elif result != None and item['pair'] != None and isPair:
                item['pair'].append(result)
            if node == None or item['intent'] >= node['intent']:
                endNode = None
                if node != None and node['match'] == True and item['intent'] == node['intent'] and item['pair'] != None:
                    break
                else:
                    endNode = All_nodeMap.pop()
                param = endNode['param'] if endNode['param'] != "" else None
                if isinstance(param, str):
                    try:
                        endNode['param'] = endNode['param'].rstrip()
                        param = yaml.safe_load(endNode['param'])
                    except yaml.YAMLError as e:
                        print(e)
                        textArr = endNode['param'].split('\n')
                        if hasattr(e, 'problem_mark'):
                            mark = e.problem_mark
                            Logger.Error(f'YAML syntax error\nlineNo: {endNode["paramIndex"]+mark.line}\n↪{textArr[mark.line]}')
                        else:
                            Logger.Error(f'YAML syntax error\nlineNo: {endNode["paramIndex"]}-{endNode["paramIndex"]+len(textArr)}\n↪{endNode["param"]}')
                result = None
                isPair = False
                if endNode['template'] != None:
                    result = Convert_TemplateTraverse(endNode['template'], param, endNode['line'], item['node'], endNode['lineNo'], endNode['pair'])
                    if result == None:
                        return None
                    if endNode['match'] == True:
                        isPair = True
                        result = {
                            "line": endNode["line"],
                            "node": result
                        }
                if item['intent'] == 0 and result != None:
                    global All_node
                    All_node[item['key']].append(result)      
            else:
                if item['template'] == None or item['node'] == None:
                    node['template'] = None
                    node['param'] = None
                    node['node'] = None
                break
    
    #STEP::Push node
    if node != None:
        All_nodeMap.append(node) 

def Covert_RemoveCommond(line):
    in_single = False
    in_double = False
    i = 0
    while i < len(line):
        char = line[i]
        if char == '\\' and (in_single or in_double):
            i += 2
            continue
        if in_single:
            if char == "'":
                in_single = False
        elif in_double:
            if char == '"':
                in_double = False
        else:
            if char == "'":
                in_single = True
            elif char == '"':
                in_double = True
            elif char == '#' and (i==0 or line[i-1] == ' '):
                return line[:i].rstrip()
        i += 1
    return line

def Convert(filePath='test.xmas'):
    global Xmas_node, All_nodeMap, Xmas_template

    #STEP::Open xmas file
    try:
        targetFile = open(filePath, encoding='utf-8')
    except Exception as e:
        print(f'Error: {e}')
        Logger.Error(f'Load {filePath} fail')

    #STEP::Read line
    line = targetFile.readline()
    headContent = ''
    index = 0
    headIndex = -1
    headLine = ''
    while line:
        lineText = line.rstrip()
        line = targetFile.readline()
        index = index+1

        #WHEN-IN::Skip comment or empty line
        if re.match(r'^\s*#', lineText):
            continue
        if re.match(r'^\s*$', lineText):
            continue

        #STEP-IN::Clean line end comment
        lineText = Covert_RemoveCommond(lineText)

        #WHEN-IN::Node match
        if re.search(Xmas_node, lineText):
            #STEP-IN-IN::Check node
            node = Convert_CheckNode(lineText, index)
            if node['template'] != None:
                Convert_CheckLine(node, node['line'], True, index)
            Convert_Template(node)

        #WHEN-IN::Param match
        elif len(All_nodeMap)>0:
            if All_nodeMap[-1]['param'] != None:
                lineContent = re.sub(r'^ *', '', lineText)
                Convert_CheckLine(All_nodeMap[-1], lineContent, False, index)
                All_nodeMap[-1]['param'] = All_nodeMap[-1]['param'] + lineText + '\n'
                if 'paramIndex' not in  All_nodeMap[-1]:
                    All_nodeMap[-1]['paramIndex'] = index
        else:
            if headIndex == -1:
                headIndex = index
                headLine = lineText
            headContent = headContent + lineText + '\n'
    targetFile.close()

    #STEP::Recheck line(use root define)
    Convert_DefineCheck()

    #STEP::Convert Xmas_template
    Convert_Template(None)
    global All_node
    try:
        headContent = headContent.rstrip()
        param = yaml.safe_load(headContent)
    except yaml.YAMLError as e:
        print(e)
        textArr = headContent.split('\n')
        if hasattr(e, 'problem_mark'):
            mark = e.problem_mark
            Logger.Error(f'YAML syntax error\nlineNo: {headIndex+mark.line}\n↪{textArr[mark.line]}')
        else:
            Logger.Error(f'YAML syntax error\nlineNo: {headIndex}-{headIndex+len(textArr)}\n↪{headContent}')
    return Convert_TemplateTraverse(Xmas_template, param, headLine, All_node, headIndex)
    
def Deal(pathSetting, pathXmas):
    #STEP::Get xmas setting
    setting = GetSettings(pathSetting, pathXmas)

    #STEP::Convert setting
    DealSetting(setting)

    #STEP::Get xmas
    result = Convert(pathXmas)

    return result