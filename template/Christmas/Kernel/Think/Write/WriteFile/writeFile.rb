=begin
 param = {
	 "path" => "*",                   	//文件路劲
	 "isCover" => false                 //是否覆盖原文件
 }
=end

# encoding=UTF-8
#require 'rubygems'
require 'fileutils'

class CWriteFile
	def initialize()
		
	end

	def createDir(param)
		dir = ""
		dirArray = param.split("/")
		dirArray.each do |list|
			if list == "." or list == ".."  or list.include?':'
				dir += list
				next	
			end 
			if dirArray.last == list
				break
			end
			dir += "/"+list
			if File.exist?(dir)
				next
			else		
				Dir.mkdir(dir)
			end
		end
	end

	def check(param)
		if File.exist?(param['path'])
			if !param['isCover']
				today = Time.new; 
				File.rename( param['path'], param['path']+"_bak_"+today.strftime("%Y_%m_%d_%H_%M_%S") )
			else
				FileUtils.rm_r(param['path'])
			end
		end
	end

	def writeFile(param)
		aFile = File.new(param['path'], "a+")
		if aFile
			aFile.syswrite(param['text'])
			aFile.close
		else
			puts "Unable to open file!"
		end
	end

	def start(param)
		if !param['isCover']
			param['isCover'] = false
		end

		createDir(param['path'])
		check(param)
		writeFile(param)
	end
end