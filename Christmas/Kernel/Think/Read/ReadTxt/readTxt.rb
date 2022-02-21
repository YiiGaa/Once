=begin
 param = {
     "path" => "*",                   	//文件路劲
	 "isReadAll" => "",                 //是否全读出来
	 "startStr" => ""					//开始字符
	 "endStr" => ""						//结束字符
 }
=end

#encoding:UTF-8
class CReadTxt
	def initialize()
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
		isMarkDown = false

		if !param['startStr'] or param['startStr']==""
			isMarkDown = true
		end

		while line = read()
			if !isMarkDown
				if !param['startStr'] or param['startStr']==""
					break;
				elsif line.include?param['startStr']
					isMarkDown = true
					next
				end
			elsif 
				if !param['endStr'] or param['endStr']==""

				elsif  line.include?param['endStr']
					isMarkDown = false
				end
			end

			if isMarkDown
				tReadTxt = tReadTxt+line
			end
		end

		tReadTxt = tReadTxt.chomp

		return tReadTxt+"\r\n"
	end

	def start(param)
		return readAll(param)
	end
end