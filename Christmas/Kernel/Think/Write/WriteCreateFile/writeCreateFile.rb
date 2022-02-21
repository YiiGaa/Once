=begin
 param = {
     "path" => "*",                   	//文件路劲
 }
=end

# encoding=UTF-8
#require 'rubygems'

class CWriteCreateFile
	def initialize()
		
	end

	def check(param)
		dir = ""
		dirArray = param['path'].split("/")
		dirArray.each do |list|
			if list == "." or list == ".."  or list.include?':'
				dir += list
				next	
			end 
			dir += "/"+list
			if File.exist?(dir)
			elsif
				Dir.mkdir(dir)
			end
		end
	end

	def start(param)
		check(param)
	end
end