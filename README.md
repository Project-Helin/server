# Getting Started

## Requirements
* IntelliJ IDEA ( with Scala and Play plugin enabled )
* Java 8
* Enough Coffee 
* Virtualbox 
* Vagrant

## Server Setup with IntelliJ
Tested with IntelliJ IDEA 15.0.3.

1. Checkout https://github.com/Project-Helin/server
2. Import server Project in IntelliJ: 
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
3. After few minutes, IntelliJ should have indexed the project.
4. Now checkout https://github.com/Project-Helin/commons this contains all Classes which are shared between Server and Onboard-App
5. Import commons Module in IntelliJ: 
       * File -> New -> Module from Existing Sources -> select 'commons' folder
       * 'Import Project from Gradle' Dialog appears, setup as follows:
           * ☑ Use auto-import
           * Project SDK: Select Java 1.8
           * Project format: .idea (directory based )
           * Click Ok
       
6. Now you should see the commons module at top of the server project in the Project Tree View   
7. Open Gradle Projects in the right sidebar and start the following Task: commons -> Tasks -> publishing -> publishToMavenLocal
8. Start up Virtual machine with Database and RabbitMQ-Broker (see RabbitMQ and Postgresql with vagrant)
9. Right Click on ApplicationController in app -> controllers -> Run Play 2 App


## RabbitMQ and Postgresql with vagrant
1. Install Virtualbox ( tested with 5.0.14 )
2. Install Vagrant ( tested with 1.8.1 )
3. Run following command to start the VM with the preconfigured setup
 ```
 cd <your-folder>/server
 vagrant up
 ```
 (This can take up to 30 Minutes, because it has to compile some database extensions
 which are needed for calculating routes.)
5. Go to http://localhost:15672 to verify if Rabbit-MQ Broker has started correctly
