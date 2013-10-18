contextlogger3
==============

### Folder structure:
* analysis -- analysis scipts (eg. identifying higher level context and mode of transport detection)
* clientframework -- Client-side sensing framework for android devices. This framework is built on top of Funf Open Sensing Framework.
                     clientframework/src/org/apps8os has the latest code for client framework (without any additional application specific code) with new packaging structure. A jar of client framework can be taken from this packaging structure and used with host android application.
* demoserver -- example java based http server for uploading the data from the client
* loggerui -- UI for self logging your daily activities and contexts
* traveldiary -- an app for logging trips incl. transport modes that were used
* database -- code for integrating data into database and processing it


### How to start?
* You should clone git repo, init and update submodules

``` 
  git clone git@github.com:apps8os/contextlogger3.git
  cd contextlogger3
  git submodule update --init --recursive
``` 
* import the project and its submodules as Android project (File -> Import -> Android -> Existing Android Code Into Workspace).
* update the android-suport-v4.jar, if Eclipse complain about the library mismatching.
  
