=begin
 param = {
     "path" => "*",                   	//文件路劲
 }
=end

# encoding=UTF-8
#require 'rubygems'

class CWriteCleanDir
	def initialize()
		
	end

	def clean(param)
		directory = param['path']

		Dir.foreach(directory) do |item|
			if item != '.' and item != '..'
				u_dir = directory + File::Separator + item
				if File.stat(u_dir).directory?
					Dir.foreach(u_dir) do |f|
						if f == '.' or f == '..'
							next
						end
						if File.stat(u_dir + File::Separator + f).file?
							File.delete(u_dir + File::Separator + f)
						else
							clean({"path"=>(u_dir + File::Separator + f)})
							Dir.rmdir(u_dir + File::Separator + f)
						end
					end
					Dir.rmdir(u_dir)
				elsif File.stat(u_dir).file?
					File.delete(u_dir)
				end	
			end
		end
		
		Dir.rmdir(directory)
	end

	def start(param)
		clean(param)
	end
end