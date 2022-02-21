# encoding=UTF-8
require_relative '../readTempl.rb'

ObjSample = CReadTempl.new
tmpList = ObjSample.start({"accton" => "ReadTempl","path" => "./document/aa.templ","templ" => "controller"})
print tmpList