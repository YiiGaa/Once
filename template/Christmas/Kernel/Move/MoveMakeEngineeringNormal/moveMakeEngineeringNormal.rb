# encoding=UTF-8
require_relative '../../Think/think.rb'

class CMoveMakeEngineeringNormal
	def initialize()
		@ObjThink = CThink.new
	end

	def init(param)
		today = Time.new;
		tempArr = param.split('@')
		@inPutPath = "#{$InPutPath}/MakeEngineeringNormal/#{param}/target.json"
		@modelPath = "#{$ModelPath}/MakeEngineeringNormal/#{tempArr[0]}/Basis.json"
		@configPath = "#{$ModelPath}/MakeEngineeringNormal/#{tempArr[0]}/config.json"
		@outPutPath = "#{$OutPutPath}/#{param}/#{today.strftime("%Y-%m-%d")}"
	end
	
	def step1(param)
		@input = @ObjThink.start({"action" => "ReadJson","path" => @inPutPath})
		@Model = @ObjThink.start({"action" => "ReadJson","path" => @modelPath})
		@Config = @ObjThink.start({"action" => "ReadJson","path" => @configPath})
		@configure = @input['configure']
		@input = @input['control']
		step1_checkIsNeedconfigure()
		step1_checkIsNeedReplace(param)

		if @Config['isTargetCover'] != true
			@Config['isTargetCover'] = false
		end
	end

	def step1_checkIsNeedReplace(param)
		if @Config['waitInputParam']
			if !param      #user input param
				errorLog("Please input param!")
			end
			step1_replaceInputParam(@input, param)
		end
	end

	def step1_replaceInputParam(param, replace)
		if param.class == Array
			param.each do |list|
				step1_replaceInputParam(list, replace)
			end
		elsif param.class == Hash
			param.each do |key,value|
				if value.class == String
					param[key] = param[key].gsub(/#{@Config['waitInputParam']}/,replace)
				else
					step1_replaceInputParam(value, replace)
				end	
			end
		end
	end

	def step1_checkIsNeedconfigure()
		if @configure
			if @configure.class == Hash
				@configure.each do |key,value|
					step1_replaceConfigure(@input, key, value)
				end
			end
		end
	end

	def step1_replaceConfigure(param, target, replace)
		if param.class == Array
			param.each do |list|
				step1_replaceConfigure(list, target, replace)
			end
		elsif param.class == Hash
			param.each do |key,value|
				if value.class == String
					param[key] = param[key].gsub(/#{target}/,replace)
				else
					step1_replaceConfigure(value, target, replace)
				end	
			end
		end
	end

	def step2()
		puts @input['generatePath']
		puts File.exist?(@input['generatePath'])
		if !File.exist?(@input['generatePath'])
			@ObjThink.start({"action" => "WriteCreateFile","path"=>@input['generatePath'] })
		end

		isEmpty = true
		Find.find(@input['generatePath']) do |filename|
			if filename != @input['generatePath']
				isEmpty = false
				break
			end
		end


		if @Config['isCleanGeneratePath'] == true and !isEmpty
			@ObjThink.start({"action" => "WriteCleanFile","path"=>@input['generatePath'] })
		elsif @Config['isNotEmptyGeneratePathError'] == true and !isEmpty
			errorLog(" #{@input['generatePath']}: document is not empty!!")
		elsif @Config['isBackupWhenNotEmpty'] == true and !isEmpty
			today = Time.new; 
			File.rename(@input['generatePath'], @input['generatePath']+"_bak_"+today.strftime("%Y_%m_%d_%H_%M_%S") )
		end
	end
	
	def errorLog(param)
		puts ""
		puts "error!#{param}"
		exit
	end


	def step3_ergodicTempl(param, model, path)
		if param.class == Array
			param.each do |list|
				step3_ergodicTempl(list, model)
			end
		elsif param.class == Hash
			if model.class == Hash
				model.each do |key,value|
					if value.class == String 
						if  param[key].class == String and value == "*"
							puts "  #{key}: #{param[key]}"
						elsif param[key].class == String
							puts "  #{key}: #{param[key]}"
						else
							errorLog("  #{key}: value is not a String")
						end
					elsif value.class == Hash
						if param[key].class == Hash or param[key].class == Array
							puts "  #{key}: Ok"
							step3_ergodicTempl(param[key], value)
						else
							errorLog("  #{key}: value is not a Hash or Array")
						end
					elsif value.class == Array
						if param[key].class == Hash or param[key].class == Array
							puts "  #{key}: Ok"
							value.each do |list_1|
								step3_ergodicTempl(param[key], list_1)
							end
						else
							errorLog("  #{key}: value is not a Hash or Array")
						end
					end
				end
			end
		end
	end

	def step3_directCopy(param)
		if param==nil
			return
		end

		param.each do |list|
			puts "	#{@Config['enginneringPath']+list['sourcePath']} => #{@Config['generatePath']+list['targetPath']}"
			contain = ""
			if list['contain'] and list['contain']!=""
				contain = list['contain']
			end
			if list['except'] and list['except']!=""
				@ObjThink.start({"action" => "WriteCopyFile","isCover"=>@Config['isTargetCover'], "sourcePath" => @Config['enginneringPath']+list['sourcePath'],"targetPath"=>@Config['generatePath']+list['targetPath'],"except"=>list['except'],"contain"=>contain})
			else
				@ObjThink.start({"action" => "WriteCopyFile","isCover"=>@Config['isTargetCover'], "sourcePath" => @Config['enginneringPath']+list['sourcePath'],"targetPath"=>@Config['generatePath']+list['targetPath'],"contain"=>contain})
			end
		end
	end

	def step3_directMerger(param)
		if param==nil
			return
		end
		param.each do |list|
			puts "	"+@Config['generatePath']+list['targetPath']
			targetText = ""
			list['sourcePath'].each do |list_1|
				#get file list
				tempList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+list_1['rootPath'],"contain"=>list_1['contain'],"except"=>list_1['except']})

				#get file content
				tempList.each do |list_2|
					if list_1['filter'] and list_1['filter']!=""
						temp = @ObjThink.start({"action" => "ReadTxt","path" => list_2,"startStr"=>@Config[list_1['filter']]['startStr'],"endStr"=>@Config[list_1['filter']]['endStr']})
					else
						temp = @ObjThink.start({"action" => "ReadTxt","path" => list_2})
					end
					targetText = targetText + temp
				end
			end
			@ObjThink.start({"action" => "WriteFile","path" => @Config['generatePath']+list['targetPath'],"isCover"=>@Config['isTargetCover'],"text"=>targetText})
		end

	end

	def step3_selectiveMerger(param)
		if param==nil
			return
		end
		param.each do |list|
			#get selector list
			selectorList = []
			selectorFileList = []
			slector = list['sourcePath']['selector']
			tempContain = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>slector['contain'],"startStr"=>'',"endStr"=>'',"substitutionParameter"=>{@Config["targetFileName"]=>""}})
			selectorFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+slector['rootPath'],"contain"=>tempContain})
			selectorFileList.each do |list_1|
				tempArr = list_1.split('/')
				tempArrsContain = tempContain.split('/')
				selectorList << tempArr[tempArr.length - tempArrsContain.length]
			end
			
			i = 0
			selectorFileList.each do |list_1|
				#get control list
				selectorSourceList = @ObjThink.start({"action" => "ReadTemplInLine","path" => list_1,"startStr"=>@Config[slector['filter']]['startStr'],"endStr"=>@Config[slector['filter']]['endStr']})
				
				sourceList = list['sourcePath']['source']
				sourceList.each do |key,value|
					controlText = ""
					#get srouce control code
					value.each do |list_2|
						tempControlFileList = []
						if list_2['contain'].include?@Config["selectName"]
							#foreach sourceList
							selectorSourceList.each do |list_3|
								tempContainForControl = list_2['contain']
								tempContainForControl = tempContainForControl.sub(@Config["selectName"],list_3)
								tempControlFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+list_2['rootPath'],"contain"=>tempContainForControl,"except"=>list_2['except']})
							
								#get control file text
								tempControlFileList.each do |list_4|
									if list_2['filter']
										controlText = controlText + @ObjThink.start({"action" => "ReadTxt","path" => list_4,"startStr"=>@Config[list_2['filter']]['startStr'],"endStr"=>@Config[list_2['filter']]['endStr']})
									else 
										controlText = controlText + @ObjThink.start({"action" => "ReadTxt","path" => list_4})
									end
								end

								if list_2['replace']
									tempReplaceTarget = list_2['replace'].clone
									tempReplaceTarget.each do |tempKey, tempValue|
										tempValue = tempValue.sub(@Config["selectName"],list_3)
										tempReplaceTarget[tempKey] = tempValue
									end
									controlText = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>controlText,"startStr"=>"","endStr"=>"","substitutionParameter"=>tempReplaceTarget})
								end
							end
							next
						elsif list_2['contain'].include?@Config["targetFileName"]
							tempContainForControl = list_2['contain']
							tempContainForControl = tempContainForControl.sub(@Config["targetFileName"],selectorList[i])
							tempControlFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+list_2['rootPath'],"contain"=>tempContainForControl,"except"=>list_2['except']})
						else
							tempContainForControl = list_2['contain']
							tempControlFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+list_2['rootPath'],"contain"=>tempContainForControl,"except"=>list_2['except']})
						end

						#get control file text
						tempControlFileList.each do |list_4|
							if list_2['filter']
								controlText = controlText + @ObjThink.start({"action" => "ReadTxt","path" => list_4,"startStr"=>@Config[list_2['filter']]['startStr'],"endStr"=>@Config[list_2['filter']]['endStr']})
							else 
								controlText = controlText + @ObjThink.start({"action" => "ReadTxt","path" => list_4})
							end
						end

						if list_2['replace']
							tempReplaceTarget = list_2['replace'].clone
							tempReplaceTarget.each do |tempKey, tempValue|
								tempValue = tempValue.sub(@Config["targetFileName"],selectorList[i])
								tempReplaceTarget[tempKey] = tempValue
							end
							controlText = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>controlText,"startStr"=>"","endStr"=>"","substitutionParameter"=>tempReplaceTarget})
						end
					end

					#write targetFile
					targetFile = list['targetPath'][key]
					targetFile = targetFile.sub(@Config["targetFileName"],selectorList[i])
					puts "	"+@Config['generatePath']+targetFile
					@ObjThink.start({"action" => "WriteFile","path" => @Config['generatePath']+targetFile,"isCover"=>@Config['isTargetCover'],"text"=>controlText})

				end
				i = i+1
			end

		end
	end

	def step3_replaceMerger(param)
		if param==nil
			return
		end
		param.each do |list|
			#get selector list
			selectorList = []
			selectorFileList = []
			slector = list['sourcePath']['selector']
			tempContain = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>slector['contain'],"startStr"=>'',"endStr"=>'',"substitutionParameter"=>{@Config["targetFileName"]=>""}})
			selectorFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+slector['rootPath'],"contain"=>tempContain})
			selectorFileList.each do |list_1|
				tempArr = list_1.split('/')
				tempArrsContain = tempContain.split('/')
				selectorList << tempArr[tempArr.length - tempArrsContain.length]
			end

			i = 0;
			selectorFileList.each do |list_1|
				controlText = {}
				controlLibText = {}
				#get control list
				selectorSourceList = @ObjThink.start({"action" => "ReadTemplInLine","path" => list_1,"startStr"=>@Config[slector['filter']]['startStr'],"endStr"=>@Config[slector['filter']]['endStr']})
				
				#get srouce control code
				sourceList = list['sourcePath']['source']
				sourceList.each do |list_2|
					#foreach sourceList
					selectorSourceList.each do |list_3|
						tempContainForControl = list_2['contain']
						tempContainForControl = tempContainForControl.sub(@Config["selectName"],list_3)
						tempControlFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['enginneringPath']+list_2['rootPath'],"contain"=>tempContainForControl,"except"=>list_2['except']})
						
						#get control file text
						tempControlFileList.each do |list_4|
							if list_2['filter']
								if list_2['target']
									tempText = @ObjThink.start({"action" => "ReadTxt","path" => list_4,"startStr"=>@Config[list_2['filter']]['startStr'],"endStr"=>@Config[list_2['filter']]['endStr']})
									if list_2['replace']
										tempReplaceTarget = list_2['replace'].clone
										tempReplaceTarget.each do |tempKey, tempValue|
											tempValue = tempValue.sub(@Config["selectName"],list_3)
											tempReplaceTarget[tempKey] = tempValue
										end
										tempText = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>tempText,"startStr"=>"","endStr"=>"","substitutionParameter"=>tempReplaceTarget})
									end
									controlLibText[list_2['target']] = (controlLibText[list_2['target']]? controlLibText[list_2['target']]: "") + tempText
								else
									controlText[list_3] = @ObjThink.start({"action" => "ReadTxt","path" => list_4,"startStr"=>@Config[list_2['filter']]['startStr'],"endStr"=>@Config[list_2['filter']]['endStr']})
								end
							else 
								if list_2['target']
									tempText = @ObjThink.start({"action" => "ReadTxt","path" => list_4})
									if list_2['replace']
										tempReplaceTarget = list_2['replace'].clone
										tempReplaceTarget.each do |tempKey, tempValue|
											tempValue = tempValue.sub(@Config["selectName"],list_3)
											tempReplaceTarget[tempKey] = tempValue
										end
										tempText = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>tempText,"startStr"=>"","endStr"=>"","substitutionParameter"=>tempReplaceTarget})
									end
									controlLibText[list_2['target']] = (controlLibText[list_2['target']]? controlLibText[list_2['target']]: "") + tempText
								else
									controlText[list_3] = @ObjThink.start({"action" => "ReadTxt","path" => list_4})
								end
							end
						end
					end
				end

				#replace target
				replaceTarget = @ObjThink.start({"action" => "ReadTxt","path" => list_1})
				replaceTarget = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>replaceTarget,"startStr"=>@Config[slector['filter']]['startStr'],"endStr"=>@Config[slector['filter']]['endStr'],"substitutionParameter"=>controlText})
				replaceTarget = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>replaceTarget,"startStr"=>"","endStr"=>"","substitutionParameter"=>controlLibText})

				#write target file
				targetFile = list['targetPath']
				targetFile = targetFile.sub(@Config["targetFileName"],selectorList[i])
				puts "	"+@Config['generatePath']+targetFile
				@ObjThink.start({"action" => "WriteFile","path" => @Config['generatePath']+targetFile,"isCover"=>@Config['isTargetCover'],"text"=>replaceTarget})
				i=i+1
			end

		end
	end
	
	def step3_targetReplace(param)
		if param==nil
			return
		end
		param.each do |list|
			#get code list
			codeFine = list['codeFine']
			if codeFine
				codeFine.each do |key,value|
					codeList = @ObjThink.start({"action" => "ReadTemplInLine","path" => @Config['enginneringPath']+value['sourcePath'],"readStart"=>@Config[value['filter']]['startStr'],"readEnd"=>@Config[value['filter']]['endStr'],"startStr"=>@Config[value['listEx']]['startStr'],"endStr"=>@Config[value['listEx']]['endStr']})
					tempStr = ""
					codeList.each do |list_1|
						if list_1.include?value['listContain']
							if value['strHeader']
								list_1 = value['strHeader']+list_1
							end
							if value['strFooter']
								list_1 = list_1+value['strFooter']
							end
							tempStr = tempStr+list_1+value['listSpliceDelimiter']
						end
					end
					codeFine[key] = tempStr.chop
				end
				
				#replace codeList to replaceStr
				list['replaceStr'].each do |key,value|
					codeFine.each do |key_1,value_1|
						value = value.sub("{"+key_1+"}",value_1)
					end
					list['replaceStr'][key] = value
				end
			end

			tempContain = ""
			if list['targetPath']['contain'] and list['targetPath']['contain']!=""
				tempContain = list['targetPath']['contain'].sub(@Config["targetFileName"],"")
			end
			targetFileList = @ObjThink.start({"action" => "ReadFileList","path" => @Config['generatePath']+list['targetPath']['rootPath'],"contain"=>tempContain})

			
			
			targetFileList.each do |list_1|
				replaceTarget = @ObjThink.start({"action" => "ReadTxt","path" => list_1})
				
				#get page name
				tempArr = list_1.split('/')
				tempArrs = tempContain.split('/')
				if(tempArrs.length == 0)
					targetFileName = tempArr[tempArr.length - 1]
				else
					targetFileName = tempArr[tempArr.length - tempArrs.length]
				end
				tempArrs = targetFileName.split('.')
				targetFileName = tempArrs[0]

				tempReplaceStr = {}
				list['replaceStr'].each do |key_2,value_2|
					tempReplaceStr[key_2] = value_2.sub(@Config["targetFileName"],targetFileName)
				end

				replaceTarget = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>replaceTarget,"startStr"=>"","endStr"=>"","substitutionParameter"=>tempReplaceStr})
				@ObjThink.start({"action" => "WriteFile","path" => list_1,"isCover"=>true,"text"=>replaceTarget})
			end
		end
	end

	def step3()
		@Deal = []
		
		@Model["control"].each do |key,value|
			case key
			when "directCopy"
				step3_directCopy(@input[key])
			when "directMerger"
				step3_directMerger(@input[key])
			when "selectiveMerger"
				step3_selectiveMerger(@input[key])
			when "replaceMerger"
				step3_replaceMerger(@input[key])
			when "targetReplace"
				step3_targetReplace(@input[key])
			else
				@Config[key]=@input[key];
			end
		end
	end
	
	def step4()
		puts "	outPutPath:"
		@Deal.each do |list|
			@ObjThink.start({"action" => "WriteCreateFile","path"=>@outPutPath+list["pathAdd"] })
			tmpStr = @outPutPath+list["pathAdd"]+"/"+list['fileName']
			puts "	"+tmpStr
			@ObjThink.start({"action" => "WriteFile","path" =>tmpStr,"text"=>list["text"]})
		end
		
	end

	def start(param)
		init(param[1])
		puts ""
		puts 'Step 1: Read textBook.'
		step1(param[2])
		puts 'Step 2: Clean target Dir'
		step2()
		puts 'Step 3: Generating code.'
		step3()
		# puts 'Step 3: Write code.'
		# step4()
		puts 'Success!'
	end
end
