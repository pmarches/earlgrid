GUI library
----
- SWT
	+ Known model
	+ Very responsive
	- Needs different packages for different OS? Multi-platform jar possible.
- Eclipse RCP
	- we do not quite fit the RCP model. the UI model is very different than a RCP application.
- JavaFX
	- Java 8+
	- Open jdk requires a separate package (openjfx) to be installed
	- Is laggy, compared to SWT at least
- Swing
	- Old tech
	
Deployment strategy
---
- Self contained
- Include GUI libs, SSH, protobuf, 
- Maybe separate ditrbution per OS, but ideally one big multi os distribution

Google protobuf
----
Avro
Thrift
Protobuf

OSGI
---

SSH
---
+ SSH is pretty ubiquitous
+ No daemon to run, to port to open
+ Like magic!

Terminal
---
A lot of work went into the terminal. Tried:
- Huge Scrollable panel (laggy, height limit on some OS)
- Multiple NATTable (prevents selection across widgets)
- Single Spanning Nebula gid (Laggy, causes funky selection behavior)
- GDI (lots of work)
- One label per line
- Single NatTable? the cells are probably customizable?
- Super imposed selection layer?

What to keep in mind for the TaskWidget
- Able to select multiple widgets (Ex: all screen)
- Will be of different height
- Maybe varying row height? (Display images and such)
- Will be changing in the background
- Can be added/removed
- Multi cell selection (not only row/column selection)
- Does it have a scrollbar?
- Can pop-out the command into a child window
- How to display stdout, err, log? Ideally, you want to link rows between each stream: these output rows are linked to these err row, and also linked to these log rows
-  

Table widget
---
- Nebula
  - requires JFace, eclipse runtime, etc..
  + already have working code
- ControlFX
  - Require JavaFX
  - Laggy
- SWT table widget
  - Multi cell selection


Message propagation stack:

- UI client
- Client API (LoopBack impl)                | - Client API (SSH implementation)
- IOAccumulator (message promises : Not task promises)
- Client-side SessionModel (Changed by IOThread, owned by UI Client)
- Client IOThread (async read/write)
------------------Client/Server boundary---------------------------
- Server IOThread (async read/write)
- Server main
- ServerSideShellSession
- Server-side SessionModel (changed by executor)
- Executor
- Single command





- Create msg to start task
- Msg eventually gets to the server side
- taskId generated
- task is started
- task id is returned with originating message id
- Synchronous client can observe the sessionModel change
- 

RMI
---
Can we simply start a RMI server on a random port, and forward the RMI traffic with SSH? Yes we can, but that is not what we are after.

Ammonite review
---
- Scala as the language is intriguing
- Uses the old terms
- Nice sub-process handling
- Annoying bang at end of commands; ls! cd!
- Confusing cd! up command
- Cool scala string single quote prefix
