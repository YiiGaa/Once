=begin
 param = {
     "path" => "*",                   	//文件路劲
 }
=end

#encoding:UTF-8
#require 'rubygems'

class CReadTempl
	def initialize()
		@mKey = "######"
	end

	def openFile(tFile)
		@mFile = File.new(tFile,"r",:encoding=>"utf-8")
	end
	
	def closeFile()
		@mFile.close
	end
	
	def read()
		tmpReadLine = @mFile.gets
		#for the end of file
		if !tmpReadLine then
			closeFile
			tmpReadLine = nil
		end
		return tmpReadLine
	end

	def readAll(param) 
		openFile(param['path'])
		tReadTxt = ""

		tKey = @mKey+param['templ']+@mKey
		tIsMark = false
		while line = read()
			if line.include?tKey
				if tIsMark
					closeFile()
					break
				else
					tIsMark = true
					next
				end
			end
			if tIsMark
				tReadTxt = tReadTxt+line
			end
		end
		return tReadTxt
	end

	def start(param)
		readAll(param)
	end
end