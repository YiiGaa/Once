# encoding=UTF-8
require_relative '../../Think/think.rb'
require 'json'

class CMoveDownloadCode
	def initialize()
		@ObjThink = CThink.new
	end

    def init(param)
		today = Time.new;
		tempArr = param.split('@')
		@inPutPath = "#{$InPutPath}/DownloadCode/#{param}/target.json"
		@modelPath = "#{$ModelPath}/DownloadCode/#{tempArr[0]}/Basis.json"
		@configPath = "#{$ModelPath}/DownloadCode/#{tempArr[0]}/config.json"
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

	def step2_IsExists(param)
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

    def step2()
        puts "loading #{@input['url']}/#{@input['module']}/#{@input['index']}"
        indexString = @ObjThink.start({"action" => "ReadHttp", "url" => "#{@input['url']}/#{@input['module']}/#{@input['index']}"})
        indexString = JSON.parse(indexString)

        @outPutPath = @Config['targetDir']
		targetAdd = ""
		if @input['targetAdd']!=nil
			targetAdd = @input['targetAdd']
		end
		step2_IsExists(@outPutPath+targetAdd+"/"+@input['module'])
        indexString.each do |list|
			httpUrl = "#{@input['url']}/#{@input['module']}/#{list}"
			puts "Download file: #{@outPutPath}/#{@input['module']}/#{list}"
			downloadFile = @outPutPath+"/"+targetAdd+@input['module']+"/"+list
			puts downloadFile
            @ObjThink.start({"action" => "WriteDownload", "url"=>httpUrl , "isCover"=>false, "path"=>downloadFile })

			if @input['replace']!=nil
				replaceTarget = @ObjThink.start({"action" => "ReadTxt","path" => downloadFile})
				replaceTarget = @ObjThink.start({"action" => "DealRegExReplace","temlp"=>replaceTarget,"startStr"=>"","endStr"=>"","substitutionParameter"=>@input['replace']})
				@ObjThink.start({"action" => "WriteFile","path" => downloadFile,"isCover"=>true,"text"=>replaceTarget})
			end
        end
    end
	
	def errorLog(param)
		puts ""
		puts "error!#{param}"
		exit
	end

    def start(param)
		init(param[1])
		puts ""
		puts 'Step 1: Read textBook.'
		step1(param[2])
		puts 'Step 2: Read download code.'
		step2()
		puts 'Success!'
	end
end