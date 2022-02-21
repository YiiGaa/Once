=begin
 param = {
     "Path" => "*",                   	//文件路劲
     "Sheet" => "",                     //excel sheel
	 "StartRow" => "",					//开始行
	 "StartColumn" => ""				//开始列
 }
=end
# encoding=UTF-8
require 'pathname'
require 'win32ole'

class CReadExcel
	def initialize()
	end
	
	def openFile(tFileName, mSheet)
		mExcel = WIN32OLE::new('excel.Application')
		@mBook = mExcel.Workbooks.Open(tFileName)
		@mSheet = @mBook.Worksheets(mSheet)
		@mSheet.Select 
	end   
	   
	def closeFile
		@mBook.close
	end  
	
	def readRange(param)
		#return @mSheet.Range(param).value
		return @mSheet.Cells(1, 2).value
	end

	def read(tFile, tSheet, tStartRow, tStartColumn) 
		openFile(tFile, tSheet)
		tDataMap = []
		tDataRow = []
		tmpColumn = tStartColumn;
		while @mSheet.Cells(tStartRow,tmpColumn).value
			while @mSheet.Cells(tStartRow,tmpColumn).value
				tDataRow << @mSheet.Cells(tStartRow,tmpColumn).value
				tmpColumn+=1
			end
			tDataMap << tDataRow
			tDataRow = []
			tmpColumn = tStartColumn
			tStartRow+=1
		end
		closeFile()
		return tDataMap
	end

	def start(param)
		if !param['Sheet']
			param['Sheet'] = 1
		end
		if !param['StartRow']
			param['StartRow'] = 1
		end
		if !param['StartColumn']
			param['StartColumn'] = 1
		end

		read(param['Path'], param['Sheet'], param['StartRow'], param['StartColumn'])
	end
end
