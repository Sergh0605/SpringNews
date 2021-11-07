# SpringNews

This is the SpringNews application. This application developed for education purposes. This app allows you to upload and
view articles.

## Prerequisites

Developed with Java 17 and Spring Boot. Database is embedded H2. It stores data in file. 
Database file and log catalog will create automatically in folder of application jar file.

## Getting Started

### For Desktop Windows

1. Install Java from this link  https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe
2. Copy SpringNews-0.1.0.jar file to any catalog in your PC. (For example C:\SpringNews)
3. Start application by double-click on SpringNews.jar.
4. Open your browser and copy application URL (http://localhost:8085/) into address line.
5. To turn off this application you need to open Windows built in TaskManager application and remove Java(TM) Platform
   SE binary task.

### For Linux Server (Debian example)
1. Install Java.

   Add the Linux Uprising Oracle Java PPA repository (and its key) and update the software sources. Use Terminal:

   su

   echo "deb http://ppa.launchpad.net/linuxuprising/java/ubuntu focal main" | tee /etc/apt/sources.list.d/linuxuprising-java.list

   apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 73C3DB2A

   (probably you have to install gnupg2 before - apt-get install gnupg2)

   apt-get update

   exit

   Install and make Oracle JDK 17 the default JDK version:

   sudo apt install oracle-java17-installer --install-recommends

   (probably you have to install sudo before - apt-get install sudo)

   To check which Java version is set as default on your system, you could run java -version

2. Download application jar file.

   