# Gatekeeper

## Introduction

Gatekeeper is an IoT system that can be part of your smart home. Today, safety is a word that is often heard so securing your home is of crucial importance and being able to do it with your mobile app sounds great. Equipped with *Ultrasonic Ranging Module HC - SR04*, ESP32 microcontroller is capable of detecting every nearby movement and sending corresponding information to your Android application.

## Technologies

We use cloud based AWS IoT solution based on AWS IoT core publish/subscribe system and AWS Dynamo DB database. AWS IoT core already comes with preinstalled message broker and gateway so installing system into production is extremely simple. Whenever sensor recognizes movement, alarm is triggered and information is sent to user through mobile app who can then turn off the alarm. User can also set time interval in which sensor and alarm will be active .
You can monitor state of your sensors through Android based mobile app but also through React web app. Its purpose is to provide you with different statistics about device's last activities.


## Device installation

With Arduino IDE open AWS_IOT folder and run .ino file. All header files will be automatically pulled with it.


## Mobile app installation

Install Android Studio and any device(it is tested on Google Pixel 5).

    git clone https://github.com/as51340/Gatekeeper.git
    cd GatekeeperApp
    Open with Android Studio and run on real device or any emulator .

## Web app
    git clone https://github.com/as51340/Gatekeeper.git
    cd webapp
    npm install
    npm start

Open your browser at localhost:3000. <br/>
Voila!


Sincerely, <br/>
Gatekeeper team: **Leonarda Gjenero, Neda Kusurin, Maksimilijan Marosevic, Marija Papic, Dora Pavelic, Andi Skrgat**
