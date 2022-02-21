# encoding=UTF-8
require_relative '../dealRegExReplace.rb'

=begin
get line txt value	
=end
ObjSample = CDealRegExReplace.new
tmpList = ObjSample.start({"temlp"=>"C:/On@@c@@e/o@@c@@utPut/code.txt","substitutionParameter"=>{"c"=>"replace"}})
puts tmpList['result']


=begin
get all txt value
=end
=begin
ObjSample = CReadTxt.new
tmpList = ObjSample.read("C:/Christmas/outPut/code.txt")
puts tmpList
=end
