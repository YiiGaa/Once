# encoding=UTF-8
require_relative '../writeDownload.rb'

ObjSample = CWriteDownload.new
ObjSample.start({"accton" => "WriteDownload","isCover" => false, "url" => "http://192.168.1.100/open/trick-moudle/-/raw/main/WallDeep/Load.json", "path"=> "Load.json"})