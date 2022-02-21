=begin
 Christmas v1.0 for StopRefactoring framework
 More Detail: https://github.com/YiiGaa/Christmas
 official: https://stoprefactoring.com
 Designed by Daniel.Leung
=end
# encoding=UTF-8
require_relative 'Kernel/kernel.rb'


puts 'Hi, I am Christmas.I am a tool which would work for StopRefactoring framework.'
puts 'More Detail: https://github.com/YiiGaa/Christmas'
puts 'official: https://stoprefactoring.com'
puts ''
if(ARGV.length != 0) then
	strList = ''
	tMoudleList = []
	ARGV.each do |list|
		strList = strList + ", #{list}"
		tMoudleList << list
	end
	puts "I will make #{strList}"
	ObjKernel = CKernel.new
	ObjKernel.start(tMoudleList)
else
	puts 'Please input command parameters'
	puts 'like: ruby Christmas.rb ./Menu/MakeCodeNormal/xxx'
	puts './Menu/MakeCodeNormal/xxx is command parameters'
end
