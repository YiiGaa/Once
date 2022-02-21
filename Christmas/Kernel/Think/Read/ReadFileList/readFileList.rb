=begin
 param = {
	 "path" => "*",                   	//文件路劲
	 "contain" => "",					//包含xx
	 "except"=> ""						//除去xx，用,隔开
 }
=end

# encoding=UTF-8
#require 'rubygems'
require  'find'

class CReadFileList
	def initialize()
	end

	def filter_Except(param,filename)
		if param['except'] != ""
			tempArr = param['except'].split(',')
			tempArr.each do|list|
				if filename.include?list
					return false
				end
			end
		end
		return true
	end

	def filter(param,filename)
		if param['contain']!="" and filename.include?param['contain']
			return filter_Except(param,filename)
		elsif param['contain']==""
			return filter_Except(param,filename)
		end
		return false
	end

	def check(param)
		path = param['path']
		tempDirectoryList = []
		Find.find(path) do |filename|
			if File.file?filename and filename != path
				if filter(param,filename)
					tempDirectoryList << filename
				end
			end
		end
		return tempDirectoryList
	end

	def start(param)
		if !param['contain']
			param['contain'] = ""
		end
		if !param['except']
			param['except'] = ""
		end
		return check(param)
	end
end