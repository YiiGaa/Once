=begin
 param = {
	 "targetPath" => "*",                   	//文件目标
	 "sourcePath" => "*",						//文件来源
	 "isCover" => false,                 		//是否覆盖原文件
	 "except"=> ""								//文件筛选 用,隔开
 }
=end

# encoding=UTF-8
#require 'rubygems'
require 'fileutils'

class CWriteCopyFile
	def initialize()
		
	end

	def filter_Except(filename)
		if @filter != ""
			tempArr = @filter.split(',')
			tempArr.each do|list|
				if filename.include?list
					return false
				end
			end
		end
		return true
	end

	def filter_Contain(filename)
		if @contain != "" and @contain
			if filename.include?@contain
				return true
			else
				return false
			end
		end
		return true
	end

	def createDir(param)

		dir = ""
		dirArray = param.split("/")
		dirArray.each do |list|
			if list == "." or list == ".."  or list.include?':'
				dir += list
				next	
			end 
			dir += "/"+list
			if File.exist?(dir)
				next
			else	
				Dir.mkdir(dir)
			end
		end
	end

	def copyFile(sourcePath,targetPath)
		if File.file?(sourcePath)
			if filter_Except(sourcePath) and filter_Contain(sourcePath)
				FileUtils.cp(sourcePath,targetPath)
			end
			return;
		end

		Dir.foreach(sourcePath) do |file|
			if file !="." and file !=".."
				if File.directory?(sourcePath+"/"+file)
					if filter_Contain(sourcePath+"/"+file)
						createDir(targetPath+"/"+file)
					end
				end
                copyFile(sourcePath+"/"+file,targetPath+"/"+file)
            end
        end
	end

	def start(param)
		@filter = ""
		@contain = ""
		if param['except']
			@filter = param['except']
		end
		if param['contain']
			@contain = param['contain']
		end

		if !param['isCover']
			if File.exist?(param['targetPath'])
				today = Time.new; 
				File.rename( param['targetPath'], param['targetPath']+"_bak_"+today.strftime("%Y_%m_%d_%H_%M_%S") )
			end
		end

		createDir(param['targetPath'])
		copyFile(param['sourcePath'],param['targetPath'])
	end
end