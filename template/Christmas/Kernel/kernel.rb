# encoding=UTF-8

require_relative './config.rb'
require_relative './Move/move.rb'

class CKernel
	def initialize()
            @KernelMap = {"Move" => CMove.new}
	end

	def start(moduleParam)
	    moduleParam.each do |list|
			@KernelMap["Move"].start(list)
	    end
	end
	
end