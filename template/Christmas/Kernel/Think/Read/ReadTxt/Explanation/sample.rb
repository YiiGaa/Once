# encoding=UTF-8
require_relative '../readTxt.rb'

ObjSample = CReadTxt.new
tmpList = ObjSample.start({"action" => "ReadTxt","path" => "./document/text.txt"})
print tmpList
