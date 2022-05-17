=begin
 param = {
	 "temlp" => "*",                   				//需要代入的模板，string
	 "startStr"=>"",								//默认为@@
	 "endStr"=>"",                                 	//默认为@@
     "substitutionParameter" => "",                 //代入参数，json
 }
 retrunParam = {
	 "result"=>""
 }
=end

# encoding=UTF-8
class CDealRegExReplace
	def initialize()
		@Modification = {
			"startStr" => "@@",
			"endStr" => "@@",
		}
	end

	def deal(param)
		if param['startStr']!=nil
			@Modification['startStr'] = param['startStr']
		end
		if param['endStr']!=nil
			@Modification['endStr'] = param['endStr']
		end
		param['substitutionParameter'].each do |key,value|
			if value.class == String
				regEx = @Modification['startStr'] + key + @Modification['endStr']
            	param['temlp'] = param['temlp'].gsub(/#{regEx}/,value)
			end
        end
		return param['temlp']
	end

	def start(param)
		# if !param['IsReadAll']
		# 	param['IsReadAll'] = true
		# end

		deal(param)
	end
end