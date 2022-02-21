=begin
 param = {
     "action" => "*",                   //需要调用的动作
     "path" => "*"                      //读取文件路劲
 }
=end
# encoding=UTF-8
require_relative './Deal/deal.rb'
require_relative './Read/read.rb'
require_relative './Write/write.rb'

class CThink
    def initialize()
        @ThinkMap =  {"Deal" => CDeal.new,
                       "Read" => CRead.new,
                        "Write" => CWrite.new
                    }
	end
	def start(param)
        @ThinkMap.each do |key,value|
            if param['action'].include?key
                return value.start(param)
                break;
            end
        end	
	end
end