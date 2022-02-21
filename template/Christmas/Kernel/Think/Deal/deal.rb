=begin
 param = {
     "action" => "*",                   //需要调用的动作
 }
=end
# encoding=UTF-8
require_relative './DealAnalyseJson/dealAnalyseJson.rb'
require_relative './DealRegExReplace/dealRegExReplace.rb'

class CDeal
    def initialize()
        @ReadMap =  {
                    "DealAnalyseJson" => CDealAnalyseJson.new,
                    "DealRegExReplace" => CDealRegExReplace.new
                }
	end
	def start(param)
		return @ReadMap[param['action']].start(param)
	end
end