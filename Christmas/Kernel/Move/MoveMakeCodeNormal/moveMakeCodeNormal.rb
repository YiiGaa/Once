# encoding=UTF-8
require_relative '../../Think/think.rb'

class CMoveMakeCodeNormal
	def initialize()
		@ObjThink = CThink.new
	end

	def init(param)
		today = Time.new;
		tempArr = param.split('@')
		@inPutPath = "#{$InPutPath}/MakeCodeNormal/#{param}/target.json"
		@modelPath = "#{$ModelPath}/MakeCodeNormal/#{tempArr[0]}/Basis.json"
		@configPath = "#{$ModelPath}/MakeCodeNormal/#{tempArr[0]}/config.json"
		@outPutPath = "#{$OutPutPath}/#{param}/#{today.strftime("%Y-%m-%d")}"
		@comfirmDir = []
	end
	
	def step1(param)
		@input = @ObjThink.start({"action" => "ReadJson","path" => @inPutPath})
		@Model = @ObjThink.start({"action" => "ReadJson","path" => @modelPath})
		@Config = @ObjThink.start({"action" => "ReadJson","path" => @configPath})
		step1_checkIsNeedReplace(param)
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
	
	def errorLog(param)
		puts ""
		puts "error!#{param}"
		exit
	end

	def step2_ergodicTempl_end(templ, param)
		tempStr = ""
		if param.class == Array
			param.each do |list|
				if list.class == String
					tempStr += @ObjThink.start({"action" => "DealRegExReplace","temlp"=>templ,"substitutionParameter"=>{"value"=>list}})
				elsif list.class == Hash
					list.each do |key_1,value_1|
						tempStr += @ObjThink.start({"action" => "DealRegExReplace","temlp"=>templ,"substitutionParameter"=>{"key"=>key_1,"value"=>value_1}})
					end
				end		
			end
		end
		return tempStr
	end

	def step2_ergodicTempl(param, model, path)
		isIncludeTemp = false
		if param.class == Array
			param.each do |list|
				step2_ergodicTempl(list, model, path)
			end
		elsif param.class == Hash
			if model.class == Hash
				model.each do |key,value|
					if value.class == String 
						if  (param[key].class != String or param[key] == nil) and value == "*" 
							errorLog("  #{key}: Lack of #{key}")
						end
						if key.include?@Config['templ'] and param[key]!=nil
							param[key] = @ObjThink.start({"action" => "ReadTempl","path" => @Config['documentPath'][path],"templ" => param[key]})
							isIncludeTemp = true
							next
						end
					elsif value.class == Hash
						if param[key].class != Hash and param[key].class != Array and param[key]!=nil
							errorLog("  #{key}: value is not a Hash or Array")
						end
						if param[key].class == Array
							if param[@Config['endTempl']] != nil
								param[key] = step2_ergodicTempl_end(param[@Config['endTempl']], param[key])
							else
								tmpStr = ""
								param[key].each do |list_1|
									tmpStr = tmpStr + step2_ergodicTempl(list_1, value, key)
								end
								param[key] = tmpStr
							end
						elsif
							param[key] = step2_ergodicTempl(param[key], value, key)
						end

					elsif value.class == Array
						if param[key].class != Hash and param[key].class != Array and param[key]!=nil
							errorLog("  #{key}: value is not a Hash or Array")
						end
						if param[key].class == Array
							if param[@Config['endTempl']] != nil
								param[key] = step2_ergodicTempl_end(param[@Config['endTempl']], param[key])
							else
								tmpStr = ""
								param[key].each do |list_1|
									tmpStr = tmpStr + step2_ergodicTempl(list_1, value[0], key)
								end
								param[key] = tmpStr
							end
						elsif
							param[key] = step2_ergodicTempl(param[key], value[0], key)
						end
					end
				end
				if isIncludeTemp
					return @ObjThink.start({"action" => "DealRegExReplace","temlp"=>param[@Config['templ']],"substitutionParameter"=>param})
				end
			end
		end
	end

	def step2()
		@Deal = []
		if @input.class == Array
			i = 1
			@input.each do |list|
				puts "	making #{list['fileName']}..."
				step2_ergodicTempl(list,@Model,"")
				tempStr = ""
				if list['path']!=nil
					tempStr = list['path']
				end
				@Deal << {"fileName"=>list['fileName'],"text"=>list['document'],"pathAdd"=>tempStr}
				i += 1
			end
		elsif @input.class == Hash
			puts "	making #{list['fileName']}..."
			step2_ergodicTempl(@input,@Model,"")
			tempStr = ""
			if @input['path']!=nil
				tempStr = @input['path']
			end
			@Deal << {"fileName"=>@input['fileName'],"text"=>@input['document'],"pathAdd"=>tempStr}
		end
	end

	def step3_IsExists(param)
		if !@Config['isJudgeExist']
			@Config['isJudgeExist'] = false
		end
		if @comfirmDir.include? param or @Config['isJudgeExist'] == false

		elsif File.exist?(param)
			puts ""
			puts "**warning: #{param} already exists, continue generating?(yes/no)"
			tempKeys = STDIN.gets.chomp()
			
			if tempKeys == "yes"
				@comfirmDir << param
			else
				puts "Generation has been interrupted"
				exit
			end
		else
			@comfirmDir << param
		end
	end
	
	def step3()
		puts "	outPutPath:"
		@Deal.each do |list|
			if @Config['targetDir']
				@outPutPath = @Config['targetDir']
			end

			step3_IsExists(@outPutPath+list["pathAdd"])

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
		puts 'Step 2: Generating code.'
		step2()
		puts 'Step 3: Write code.'
		step3()
		puts 'Success!'
	end
end
