# Getting Started

## Requirements
* IntelliJ IDEA ( with Scala and Play plugin enabled )
* Java 8
* Enough Coffee 
* RabbitMQ 
* PostgreSQL

## Database

1. create Login-Role 'helin' with superuser permissions
2. Create local database named 'helin'
3. Create Login-Role 'test' with superuser permissions
4. Create local database named 'test' (this is used for integrationtests)
5. Apply this script on both databases: CREATE EXTENSION postgis;CREATE EXTENSION "uuid-ossp";

## Server Setup
Tested with IntelliJ IDEA 15.0.3.

1. Watch Play Tutorial https://www.youtube.com/watch?v=bLrmnjPQsZc
2. Checkout https://github.com/Project-Helin/server
3. Import server Project in IntelliJ: 
    * File -> Open -> select 'server' folder
    * 'Import Project from SBT' Dialog appears, setup as follows:
        * ☑ Use auto-import
        * ☐ Create directories for empty content roots automatically
        * Download: ☑ Sources ☑ Javadocs ☑ Sources for SBT plugins
        * Project SDK: Select Java 1.8
        * Project format: .idea (directory based )
        * Click Ok
    * 'SBT Project Data To Import' Dialog appears
        * ☑ root
        * ☑ root-build
        * Click Ok
4. After few minutes, IntelliJ should have a project.
5. CTRL+N -> Application -> Enter 
6. Right Click on Application -> Run Play 2 App

## RabbitMQ with vagrant
1. Install Virtualbox ( tested with 5.0.14 )
1. Install Vagrant ( tested with 1.8.1 )
2. Run following command to start new virutal machine
 ```
 cd <your-folder>/server
 vagrant up
 ```
3. Go to http://localhost:15672 to verify if server has started correctly


