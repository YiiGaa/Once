# encoding=UTF-8
require_relative '../../Think/think.rb'

class CMoveMakeMenu
	def initialize()
		@ObjThink = CThink.new
		@menuParam = {
			"MakeCode"=>{
					"path"=>"#{$InPutPath}/MakeCodeNormal"
				},
			"CleanFile"=>{
					"path"=>"#{$InPutPath}/CleanFile"
				},
			"DownloadCode"=>{
					"path"=>"#{$InPutPath}/DownloadCode"
				},
			"MakeEngineering"=>{
					"path"=>"#{$InPutPath}/MakeEngineeringNormal"
				}
		}
	end
	
	def step1()
		@ObjThink.start({"action" => "WriteCleanFile","path"=>$MenuPath })
	end

	def step2(param)
		param.each do |list|
			isNext = false
			@menuParam.each do |key,value|
				if list.include?key
					if value['fileList'] == nil
						value['fileList'] = @ObjThink.start({"action" => "ReadDirectoryList","path"=>value['path'] })
					end
					value['fileList'].each do |list_1|
						@ObjThink.start({"action" => "WriteCreateFile","path"=>$MenuPath+"/"+list+list_1 })
						@ObjThink.start({"action" => "WriteFile","path" => $MenuPath+"/"+list+list_1+"/.gitkeep","isCover"=>true,"text"=>""})
					end
					isNext = true
					break
				end
			end
			if !isNext
				@ObjThink.start({"action" => "WriteCreateFile","path"=>$MenuPath+"/"+list })
				@ObjThink.start({"action" => "WriteFile","path" => $MenuPath+"/"+list+"/.gitkeep","isCover"=>true,"text"=>""})
			end
		end
	end
	
	def start(param)
		puts 'Step 1: clean Menu.'
		step1()
		puts 'Step 2: create Menu.'
		step2(param)
		puts 'Success!'
	end
end
