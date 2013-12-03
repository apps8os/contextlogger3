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
  
### Referring from scientific articles

Mannonen, Petri, Kimmo Karhu, and Mikko Heiskala. “An Approach for Understanding Personal Mobile Ecosystem in Everyday Context.” In Effective, Agile and Trusted eServices Co-Creation – Proceedings of the 15th International Conference on Electronic Commerce ICEC 2013, 19:135–146. TUCS Lecture Notes. Turku, Finland: Turku Centre for Computer Science, 2013.http://www.doria.fi/bitstream/handle/10024/91642/LN19.digi.pdf.
