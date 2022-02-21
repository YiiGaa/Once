# encoding=UTF-8
require_relative '../readJson.rb'

ObjSample = CReadJson.new
tmpList = ObjSample.start({"accton" => "ReadJson","path" => "./document/aa.json"})
puts tmpList['India'][1]