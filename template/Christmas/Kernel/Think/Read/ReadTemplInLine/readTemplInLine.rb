=begin
 param = {
	 "path" => "*",                   	//文件路劲
	 "startStr" => "*", 				//匹配模板，默认为######
	 "endStr" => "*", 					//匹配模板，默认为######
	 "readStart" => "*", 			    //开始read关键字，默认none
	 "readEnd" => "*", 			    	//结束read关键字，默认none
 }
=end

#encoding:UTF-8
#require 'rubygems'

class CReadTemplInLine
	def initialize()
		@mKey = {
			"startStr"=>"######",
			"endStr"=>"######"
		}
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
		if param['startStr']!=nil
			@mKey['startStr'] = param['startStr']
		end
		if param['endStr']!=nil
			@mKey['endStr'] = param['endStr']
		end
		openFile(param['path'])
		tReadTxt = []
		tKey = @mKey['startStr']+'(.+?)'+@mKey['endStr']
		regEx = /#{tKey}/

		isMarkDown = false
		if !param['readStart'] or param['readStart']==""
			isMarkDown = true
		end

		while line = read()
			if !isMarkDown
				if !param['readStart'] or param['readStart']==""
					break;
				elsif line.include?param['readStart']
					isMarkDown = true
				end
			elsif 
				if !param['readEnd'] or param['readEnd']==""

				elsif  line.include?param['readEnd']
					isMarkDown = false
				end
			end

			if isMarkDown
				#puts line
				if line =~ regEx
					regEx.match(line) 
					tReadTxt << $1
				end
			end
		end
		return tReadTxt
	end

	def start(param)
		readAll(param)
	end
end