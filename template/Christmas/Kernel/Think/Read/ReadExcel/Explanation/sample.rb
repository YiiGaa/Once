# encoding=UTF-8
require_relative '../readExcel.rb'

=begin
get range excel value
@read params
	1.excel range
@read return
	value
=end
ObjSample = CReadExcel.new
ObjSample.openFile("C:/Christmas/textBook/test.xlsx",1)
tmpList = ObjSample.readRange('a1:b2')
ObjSample.closeFile
puts tmpList



=begin
get all excel value

@readAll params
	1.excel path
	2.excel Sheet
	3.startRow
	4.startColumn
@readAll return
	value list
=end
=begin
ObjSample = CReadExcel.new
tmpList = ObjSample.read("C:/Christmas/textBook/test.xlsx",1,1,1)
puts tmpList
=end
