Thank you to e-j technologies for a [jProfiler licence](https://www.ej-technologies.com/products/jprofiler/overview.html)

Inspiration: 
 https://code.google.com/p/unix4j/
 https://csvkit.readthedocs.org
 http://stedolan.github.io/jq/manual/ 
 http://fishshell.com/
 http://lihaoyi.github.io/Ammonite

Demo:
 Show hackernews, reddit programming

What is it?
---
A modern data IDE. Do not call it a shell, or all the neckbeards will scream bloddy murder. Execution is performed via SSH or thru a local loopback. You can chain commands and transform data with unix-like utilities, but this is not a system shell. We think the command line can be a user-friendly environment not just for developers or sysadmins, but also for powerusers that have a lot of data to manage. This is a graphical environment, meaning raster graphics, dialogs, windows, widgets and interactive prompts can be used. It is meant to be extended by developers, giving powerful tools to users by writing simple commands that transform data grids. 

Excel being the ruler of adhoc office data, how does it compare? The worksheet in excel is excellent, but the programming is awful. We use a real programming language to transform data making complex problems much easier to tackle. Being written in Java, we can make use of a vast array of libraries, and a modern execution environment.

Unix-like shells have been around for a long time. At the time, the lack of computing resources dictated a very simple IO model. Today, we can do better. Of course, this means somewhat breaking backward compatibility 

Why?
---
Because it itched. This effort started out as writing some nice utilities that would run in a terminal, but I quickly realized that for what I had in mind, I needed a graphical interface. The world of dev tools is not a world where you make a lot of money. Most dev tools need to be free (as in money) to get adption. But the world of data manipulation seems a place where users are ready to pony up money for a good product. 

What problem does it solve?
---
Tedious handling of line oriented text. 
Never bother with an escape sequence again!
Commands can output complex objects on many streams
Commands can provide feedback to users without making parsing the output a nightmare
Client can provide resources to the server
Allows discovery of commands, options, files, ...
Help and syntax highligthing built in the command editor
Allows definition of a stream
Allows re-use of command outputs, without the need for temp files
Binary serialization of variables that are human readable inside terminal
Scripts and commands can be published to a repository for collaboration
Resuming session after disconnecting from server
Shell apperance can be customized by versioned packages (widgets and all)
OS agnostic (windows, mac, linux, aix, whatever)
Versioned packages are crypto-signed to prevent tampering 
Mouseless window interaction
Capable of decent multiline editing for large commands
API mode: 90% of generic command line flags are fine, but for the rest you need to power of an API
Selective screen cleanup; Make some output sticky, clear out to old cruft
Modern collections used as variables.
Debugging & introspective capabilities: Analyze each step of a chain of command
Standardize the way we specify options, 
Good integration with Spreadsheet software

Standardized option handling
---
The different meanings of short and long form of options.
?Short form are fine for toggles, but when they require an argument, they should be long form. -f

Command results
---
A command is not limited to a single output stream, but may emit many streams of data. The most common ones are
- output
- progress
- logging
Each stream begins with a well defined header to clearly identify the stream content

Output layout
--
Each output is spreadsheet-like. Each column may be addressed by the corresponding letter. Each command may defined some aliases that can be referenced as certain areas. For example, the ls command may define the aliases name,size,lastModification that map to each column. 

How does it work?
---

The local client connects thru ssh to a host. It uploads a small bootstrap jar file in a temp directory. It then starts a java process on this bootstrap, communicating with the client thru the SSH IO channel. The bootstrap does not contain the commands that will be executed. The bootstrap will receive versioned plugins that contains the command code.

It is easier to copy the code to be executed over to another machine than to copy the data over.

The idea is that the code runs on the host side. This making all of the host's resources available.

What are resources
----
Classes
Versioned modules
Scripts
Environment variables
Files?


Where ares resources stored?
----
General rule: All resources defined on the client side are to be made available on the server side. The server-side may provide resources, which may be used or ignored by the client.

Rational: Scripts developed on the client side, will depend on a certain set of versioned packages. The client should check the script's requirement against what is installed on the server and warn the user appropriately. At user's discretion, the client may provide the missing versioned packages to the server for this session. The server may cache these client-provided packages, but MUST not activate them without the client's explicit request. It shall be possible to examine what versioned packages are provided by the client. 

All resources are defined on the client side, but used on the server side. The server-side has a shared cache that the client may use or override. This keeps the user in control when software versions are installed because it is client-side. 

Installed Software (versioned packages): Client side, but lazy-copied to the serverside. And cached server-side.
Where should the command-line parsing occur? Tricky one. Server-side?
Where does variable expansion occur? Wherever the parsing occurs.
Where are environment variables defined? Client side but lazy-copied?
Is there shared configuration for all users of a server?
Should the shared objects live in both places and merged together? Conflicts handled by overwriting client or server settings?
Custom scripts: Stored on the client, same as a regular resource.

Shared repository
---
Shared repository can connect many clients and provide version management according to hostname, username, role, etc. The client is in control.

Restartable session
---
Sessions can be started and resumed, just like screen or tmux. How does that work? Screen uses some sort of daemon...
- Java process is started server side via exec call
- Client-server communication is negociated via the stdio, server process opens listening socket
- Client connects via ssh using direct-tcpip channel to the server socket
- 


Plugins
---
Plugins are signed pieces of code with configurable permissions. They may be cached on the host filesystem using the plugin's hash.



Decorator overview
---
Need a way for plugins to decorate the user interface. Let's start by decorating around the command line, but in the long run 
we should be able to decorate aything, just like eclipse. decorators need to be notified when certain things occur in the app. These
events can occur server or client side.
Use cases:
- CWD changes, we want to change the displayed prompt.
- A command has finished executing (ready to display an empty command input area)
- A command is currently executing
- Show progress (should this be displayed instead in the run history portion?)
- A command may interact directly with the decorator
- Keypress events (auto completion like fish)
Implementation options:
- OSGI
- JPF
- JSPF


 
