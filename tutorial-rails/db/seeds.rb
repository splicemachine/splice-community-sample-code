# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)
############################# Script to insert Records (Sequential)  ##################
#Company.delete_all
#1.upto(100000) do |i|
#   Company.create(:name => "User #{i}")
#end
############################# Script to insert Records (Multi thread) #################
def createArrayAndPopulate(var)
     values=[]
     var=var*10000
     1.upto(10000) do |j|
       values.push(name:"User #{j+var}")
     end
     puts "Size of Array is #{values.size} and batch is #{var}"
     Company.create!(values)
#    sleep(1)
end

@threads = []
Company.delete_all
1.upto(10) do |i|
  @threads <<  Thread.start{
     puts "Initiated threads #{i}"
     createArrayAndPopulate(i)
   }
end
@threads.each{|t| t.join}
