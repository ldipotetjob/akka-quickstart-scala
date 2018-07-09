<img width="928" alt="captura de pantalla 2017-10-07 a las 15 09 49" src="https://user-images.githubusercontent.com/8100363/31313078-665da9a6-abcf-11e7-9266-932880ea6ed2.png">


# Full implementation of akka example of Internet of Thing #

This project implement [the akka example of InternetOf Things](https://doc.akka.io/docs/akka/current/guide/tutorial_1.html)
The idea is that **each house can has several sensors devices** and should be possible get/set values in each device. Each house must have a **Network Connection Component (NCC)** who must allow connect the sensor device to Internet.

# Talking with sensors devices via KNX protocols #

There are several protocols for talk with sensor device. In my case I going to use KNX protocols and for comunication 
between sensors devices and users I will use java libraries for let Akka libarries to talk with sensors. 


## The java libraries that implement KNX protocol and will let to talk with sensors device: ##

* [Calimero project](http://calimero-project.github.io)



![myimage-alt-tag](https://github.com/ldipotetjob/akka-quickstart-scala/blob/master/images/IoTImage.jpg)
