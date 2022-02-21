# encoding=UTF-8
require_relative '../writeFile.rb'

ObjSample = CWriteFile.new
ObjSample.start({"action" => "WriteFile","path" => "./document/text.txt","isCover"=>true,"text"=>"123"})