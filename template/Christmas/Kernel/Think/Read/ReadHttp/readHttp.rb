=begin
 param = {
     "url" => "*"                         //http文件路径
 }
=end

# encoding=UTF-8
#require 'rubygems'
require 'open-uri'

class CReadHttp
	def initialize()
		
	end

	def start(param)
		download = open(param['url'])
		return download.read
	end
end