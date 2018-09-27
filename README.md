<img width="928" alt="captura de pantalla 2017-10-07 a las 15 09 49" src="https://user-images.githubusercontent.com/8100363/31313078-665da9a6-abcf-11e7-9266-932880ea6ed2.png">

# Implementation akka-quartz-scheduler and Internet of things #
This is a multimodule project it include two modules that can live separated but I want to give you the idea 
of allways work in a multimodules environments.
* [akka-quartz-scheduler](https://github.com/enragedginger/akka-quartz-scheduler): We needed something similar to the linux cron service, execute several tasks spread in time, change the schedule of those tasks, change the tasks and all of this without stop the services.
* Full implementation of [the akka example of InternetOf Things](https://doc.akka.io/docs/akka/current/guide/tutorial_1.html)

## Implementation of akka-quartz-scheduler in a cron service problem ##

What is my problem?: I have several tasks that need to execute following an specific schedule and **with different parameters every time that be executed**. There are not periodical tasks but I know that for example every sunday or every week someone will publish when I have to execute our tasks. In our example we are going to inspect an url about films and tv series *http://mejorenvo.com/*. So we can review a fixed amount of page or schedule inspect page those days that we know that company is planning to add new film pages.

### What will you find in the Implementation of akka-quartz-scheduler ? ###

* How implement tasks that can be execute like in *a linux cron services* but without the security problems and at the same time with a variable schedule without stoped any process. I have implement 2 scheduler:
  - Internal configuration: In *application.conf* akka configuration file.
  - External configuration: In an external file, this file won't be in our compiled distribution. So while our daemons are working I can change this external configuration and the jobs will be rescheduled without any problem and without stop my akka application. 

### What you will not find here, but you should ###

* A proper ErrorHandler implementation. 
* Implementation of Supervisor strategy.
* Test module: This point we'll be interesting in the future. All code have been prepared for be tested in akka environment without mocking any value.

### Requirements, Installation, Launching ###

#### Requirements ####

* jdk 1.7+ -> how check java version: java -version
* scala 2.11.x -> how check scala version: scala -version
* sbt 0.13.11+ -> how check sbt version: sbt about

#### Installation and Launching ####

* clone repository
* go to root project
* type in your terminal:
    - **sbt "AkkaSchedulerPoc/runMain com.ldg.BootNotScheduled 1 5"** . Description: It will let you review from page 1 to page 5 and then ends all process.
    - **sbt "AkkaSchedulerPoc/runMain com.ldg.BootScheduled"** . Description: You have to configurate the pages to review and when it must be done. Every thing in an external configuration file (root/akka-quickstart-scala/scriptsdb/cronmoviepages.conf)

## Implementation of akka example of Internet of Things ##

This project implement [the akka example of InternetOf Things](https://doc.akka.io/docs/akka/current/guide/tutorial_1.html)
The idea is that **each house can has several sensors devices** and should be possible get/set values in each device. Each house must have a **Network Connection Component (NCC)** It must allow connect the sensor device to Internet.
**The integration with [Calimero](http://calimero-project.github.io) is under construction.** 

### Talking with sensors devices via KNX protocols ###

There are several protocols for talk with sensor device. In my case I going to use KNX protocols and for comunication 
between sensors devices and users I will use java libraries for let Akka libarries to talk with sensors. 


### The java libraries that implement KNX protocol and will let to talk with sensors device: ###

### how connect java via KNX protocols ###

### The are a lot of project that help to comunicate with house automation sensors device: ###


* [Calimero project](http://calimero-project.github.io)



![myimage-alt-tag](https://github.com/ldipotetjob/akka-quickstart-scala/blob/master/images/IoTImage.jpg)
*Copyright of My Children (Mariela and Samuel)*
