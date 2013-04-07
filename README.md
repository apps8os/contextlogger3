contextlogger3
==============

Folder structure:
* analysis -- analysis scipts (eg. identifying higher level context and mode of transport detection)
* clientframework -- Client-side sensing framework for android devices. This framework is built on top of Funf Open Sensing Framework.
* demoserver -- example java based http server for uploading the data from the client
* loggerui -- UI for self logging your daily activities and contexts
* traveldiary -- an app for logging trips incl. transport modes that were used
* database -- code for integrating data into database and processing it


Note after clone the project:
* update submodules (logger and travlediary)

``` 
  git submodule init
  git submodule sync
  git submodule update
``` 
* update the android-suport-v4.jar, if Eclipse complain about the library mismatching.
  
