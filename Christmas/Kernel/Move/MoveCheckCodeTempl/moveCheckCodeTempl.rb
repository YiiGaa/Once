# encoding=UTF-8
require_relative '../../Think/think.rb'

class CMoveCheckCodeTempl
	def initialize()
		@ObjThink = CThink.new
	end

	def init(param)
		today = Time.new;
		tempArr = param.split('@')
		@inPutPath = "#{$InPutPath}/#{param}/target.json"
		@modelPath = "#{$ModelPath}/#{tempArr[0]}/Basis.json"
	end

	def step1()
		@input = @ObjThink.start({"action" => "ReadJson","path" => @inPutPath})
		@Model = @ObjThink.start({"action" => "ReadJson","path" => @modelPath})
	end
	
	def errorLog(param)
		puts ""
		puts "error!#{param}"
		exit
	end

	def step2_ergodicTempl(param,model)
		if param.class == Array
			param.each do |list|
				step2_ergodicTempl(list, model)
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
							step2_ergodicTempl(param[key], value)
						else
							errorLog("  #{key}: value is not a Hash or Array")
						end
					elsif value.class == Array
						if param[key].class == Hash or param[key].class == Array
							puts "  #{key}: Ok"
							value.each do |list_1|
								step2_ergodicTempl(param[key], list_1)
							end
						else
							errorLog("  #{key}: value is not a Hash or Array")
						end
					end
				end
			end
		end
	end

	def step2()
		if @input.class == Array
			i = 1
			@input.each do |list|
				puts "###########{i}##########"
				step2_ergodicTempl(list,@Model)
				tempStr = ""
				
				puts "###########{i}##########"
				puts ""
				i += 1
			end
		elsif @input.class == Hash
			puts "##########1##########"
			step2_ergodicTempl(@input,@Model)
			tempStr = ""
			if @input['path']!=nil
				tempStr = @input['path']
			end
			puts "##########1##########"
			puts ""
		end
	end
	
	
	def start(param)
		init(param[1])
		puts ""
		puts 'Step 1: Read textBook.'
		step1()
		puts 'Step 2: check textBook.'
		step2()
		puts 'Success!'
	end
end
