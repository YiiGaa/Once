# encoding=UTF-8
require_relative '../readTemplInLine.rb'

ObjSample = CReadTemplInLine.new
tmpList = ObjSample.start({"action" => "ReadTemplInLine","path" => "./document/aa.templ"})
print tmpList