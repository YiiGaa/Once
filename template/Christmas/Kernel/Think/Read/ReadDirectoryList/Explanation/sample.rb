# encoding=UTF-8
require_relative '../readDirectoryList.rb'

ObjSample = CReadDirectoryList.new
tmpList = ObjSample.start({"action" => "ReadDirectoryList","path" => "./document","except"=>"cc"})
print tmpList