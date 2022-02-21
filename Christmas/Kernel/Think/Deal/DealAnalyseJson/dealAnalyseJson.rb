=begin
 param = {
     "check" => ["*"]                   	//抽取的参数
 }
 returnParam = {
	 "result"=>""
 }
=end

# encoding=UTF-8
class CDealAnalyseJson
	def initialize()
		@returnTempl = []
		@returnParam = {}
	end

	def ergodicHash(param)
		isModule = false
		returnparam = {}

		if param.class == Array
			param.each do |value|
				returnparam = []
				returnparam << ergodicHash(value)
			end
		elsif param.class == Hash
			param.each do |key,value|
				templKey = ""
				@returnTempl.each do |value_1|
					if key == value_1
						isModule = true
						templKey = value_1
						break
					end
				end

				if isModule
					if value.class == Array
						@returnParam[templKey] = []
						value.each do |value_2|
							@returnParam[templKey] << ergodicHash(value_2)
						end
					elsif value.class == Hash
						@returnParam[templKey] = ergodicHash(value)
					else
						@returnParam[key] = value
					end
				else
					returnparam[key] = value
				end
			end
		else
			returnparam = param
		end
		return returnparam
	end

	def deal(param)
		@returnTempl = param['check']
		returnParam = []

		if param['paramForAnalyse'].class == Array
			param['paramForAnalyse'].each do |value_2|
				ergodicHash(value_2)
				returnParam << @returnParam
				@returnParam = {}
			end
		elsif param['paramForAnalyse'].class == Hash
			ergodicHash(param['paramForAnalyse'])
			returnParam << @returnParam
			@returnParam = {}
		end

		return returnParam
	end

	def start(param)
		# if !param['IsReadAll']
		# 	param['IsReadAll'] = true
		# end

		return deal(param)
	end
end