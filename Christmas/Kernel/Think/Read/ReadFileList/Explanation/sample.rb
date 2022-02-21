# encoding=UTF-8
require_relative '../readFileList.rb'

ObjSample = CReadFileList.new
tmpList = ObjSample.start({"action" => "ReadFileList","path" => "./document","contain"=>"js","except"=>""})
print tmpList