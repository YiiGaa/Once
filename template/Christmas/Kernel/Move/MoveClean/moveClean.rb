# encoding=UTF-8
require_relative '../../Think/think.rb'

class CMoveClean
	def initialize()
		@ObjThink = CThink.new
	end

	def init(param)
		today = Time.new;
		tempArr = param.split('@')
		@inPutPath = "#{$InPutPath}/CleanFile/#{param}/target.json"
		@modelPath = "#{$ModelPath}/CleanFile/#{tempArr[0]}/Basis.json"
		@configPath = "#{$ModelPath}/CleanFile/#{tempArr[0]}/config.json"
		@outPutPath = "#{$OutPutPath}/#{param}/#{today.strftime("%Y-%m-%d")}"
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

	def step2_Excute(param)
		fileList = @ObjThink.start({"action" => "ReadDirectoryList","path"=>@Config['targetDir']+param['targetRoot'],"contain"=>param['contain'],"except"=>param['except'] })

		lastClean = "!@#$%^&"
		fileList.each do |list_1|
			if list_1.include?lastClean
				next
			end
			puts "Delete Dir: #{@Config['targetDir']+param['targetRoot']+list_1}"
			@ObjThink.start({"action" => "WriteCleanDir","path"=>@Config['targetDir']+param['targetRoot']+list_1 })
			lastClean = list_1
		end

		fileList = @ObjThink.start({"action" => "ReadFileList","path"=>@Config['targetDir']+param['targetRoot'],"contain"=>param['contain'] })
		fileList.each do |list_1|
			puts "Delete File: #{list_1}"
			@ObjThink.start({"action" => "WriteCleanFile","path"=>list_1 })
		end

	end

	def step2()
		if @input.class == Array
			i = 1
			@input.each do |list|
				step2_Excute(list)
			end
		elsif @input.class == Hash
			step2_Excute(@input)
		end
	end
	
	def start(param)
		init(param[1])
		puts 'Step 1: getting Setting.'
		step1(param)
		puts 'Step 2: clean Files.'
		step2()
		puts 'Success!'
	end
end
