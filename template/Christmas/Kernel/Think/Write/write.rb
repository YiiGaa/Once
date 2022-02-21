=begin
 param = {
     "action" => "*",                   //需要调用的动作
     "path" => "*"                      //文件路劲
 }
=end
# encoding=UTF-8
require_relative './WriteCreateFile/writeCreateFile.rb'
require_relative './WriteCleanDir/writeCleanDir.rb'
require_relative './WriteFile/writeFile.rb'
require_relative './WriteCleanFile/writeCleanFile.rb'
require_relative './WriteCopyFile/writeCopyFile.rb'

class CWrite
    def initialize()
        @WriteMap =  {
                        "WriteCreateFile" => CWriteCreateFile.new,
                        "WriteCleanDir" => CWriteCleanDir.new,
                        "WriteFile" => CWriteFile.new,
                        "WriteCleanFile" => CWriteCleanFile.new,
                        "WriteCopyFile" => CWriteCopyFile.new
                    }
	end
    def start(param)
		@WriteMap[param['action']].start(param)
	end
end