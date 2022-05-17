# encoding=UTF-8
require_relative '../readHttp.rb'

ObjSample = CReadHttp.new
tempString = ObjSample.start({"accton" => "ReadHttp", "url" => "http://192.168.1.100/open/trick-moudle/-/raw/main/WallDeep/Load.json"})
puts tempString