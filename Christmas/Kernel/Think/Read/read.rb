=begin
 param = {
     "action" => "*",                   //需要调用的动作
     "path" => "*"                      //读取文件路劲
 }
=end
# encoding=UTF-8
#require_relative './ReadExcel/readExcel.rb'
require_relative './ReadJson/readJson.rb'
require_relative './ReadTempl/readTempl.rb'
require_relative './ReadTemplInLine/readTemplInLine.rb'
require_relative './ReadTxt/readTxt.rb'
require_relative './ReadDirectoryList/readDirectoryList.rb'
require_relative './ReadFileList/readFileList.rb'
require_relative './ReadHttp/readHttp.rb'

class CRead
    def initialize()
        @ReadMap =  {
                    #"ReadExcel" => CReadExcel.new,
                    "ReadJson" => CReadJson.new,
                    "ReadTempl" => CReadTempl.new,
                    "ReadTemplInLine" => CReadTemplInLine.new,
                    "ReadTxt" => CReadTxt.new,
                    "ReadDirectoryList" => CReadDirectoryList.new,
                    "ReadFileList" => CReadFileList.new,
                    "ReadHttp" => CReadHttp.new
                }
	end
	def start(param)
		return @ReadMap[param['action']].start(param)
	end
end