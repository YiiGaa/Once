=begin
 param = {
     "path" => "*",                   	//文件路劲
 }
=end

# encoding=UTF-8
#require 'rubygems'

class CWriteCleanFile
	def initialize()
		
	end

	def clean(param)
		directory = param['path']
		if File.stat(directory).file?
			File.delete(directory)
			return
		end	

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
	end

	def start(param)
		clean(param)
	end
end