To Deploy war file on heroku server.
***********************************

 heroku plugins:install https://github.com/heroku/heroku-deploy

 heroku deploy:war --war <path_to_war_file> --app <app_name>


mvn package 
mvn clean

temprary
********
git init

 echo target > .gitignore

 git add .

 git commit -m init
