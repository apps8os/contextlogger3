contextlogger3
==============

### Folder structure:
* clientframework -- Client-side sensing framework for android devices. This framework is built on top of Funf Open Sensing Framework.
"clientframework/src/org/apps8os" has the latest code for client framework (without any additional application specific code) with new packaging structure. A jar of client framework can be taken from this packaging structure and used with host android application.
* httpserver -- example java based http server for uploading the data from the client
* loggerapp -- Android application that aimes to log your daily activities and contexts
* dataprocessing -- code for integrating data into database and processing it

### How to start?

* You should clone git repo, init and update submodules

- Clone the project by using valid SSH key
 
```
  $git clone git@github.com:apps8os/contextlogger3.git
```
  - Clone the project without SSH key
 
```
 $git clone https://github.com/apps8os/contextlogger3.git
```
- Enter the directory

```
$cd contextlogger3
```
- Update submodules
 
```
$git submodule update --init --recursive
```

* Open eclipse 

* Import the projects and its submodules as Android project (File -> Import -> Android -> Existing Android Code Into Workspace).

* Import the dependency project "google-play-services_lib"

* Import the dependency project "HoloEverywhere library" (HoloEverywhere -> library)

* Import the dependency project "HoloEverywhere Addon Preferences" (HoloEverywhere -> addons -> preferences) 

* Import the dependency project "funf" (only funf, no tests and FunfTestApp)
 
* Import the dependcy project "clientframework"

* Import main project "loggerApp"

### Referring from scientific articles

Mannonen, Petri, Kimmo Karhu, and Mikko Heiskala. “An Approach for Understanding Personal Mobile Ecosystem in Everyday Context.” In Effective, Agile and Trusted eServices Co-Creation – Proceedings of the 15th International Conference on Electronic Commerce ICEC 2013, 19:135–146. TUCS Lecture Notes. Turku, Finland: Turku Centre for Computer Science, 2013. [Download](http://www.doria.fi/bitstream/handle/10024/91642/LN19.digi.pdf)
