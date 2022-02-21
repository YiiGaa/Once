=begin
 param = {
     "path" => "*",                   	//文件路劲
 }
=end

#encoding:UTF-8
#require 'rubygems'
require 'json'

class CReadJson
	def initialize()
	end

	def read(tFile) 
		json = File.read(tFile)
		obj = JSON.parse(json)
		return obj
	end

	def start(param)
		read(param['path'])
	end
end