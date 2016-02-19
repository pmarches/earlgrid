TODO
====
- Create nice demo screenshots
	- Show read from excel file and pipe to filters
	- Show bogus ERM commands
	- Show clipboard
	- Show system commands
	- 
- setup maven
- investigate if we can use apache metamodel to connect to datasources (http://metamodel.apache.org/)
	- Read/Write excel files
	- Connect to SQL databases
	- mm connect file://path/to/file.csv | session set csv1
	- mm query csv1 "select count(*) from users"
	- 
- diff/patch

- How can we use lambdas to manipulate our grids?
- Add Mime types to output
- Process management
- Handle hiearchical data (JSON,XML)
- Commands that interacts with the client should be packaged with the serverside Command. This will be a problem if going to OSGi
- API for thrid party to execute commands
- OSGI/Glassfish classloader to isolate third party libraries
- Update Site/Market place (Included with OSGI?)
- interactive help
- Command schema
- Auto completion, context help
- Implement string manipulations commands:
	* uniq
	* wc
	* nl
- Implement ted (sed/awk replacement) tjoin?
- Define scripting language (Groovy? Ammonite? Clojure? Kotlin ?)
- Execute commands in the background (see progress bar!)
- Scroll NAT grid to the end of the outputs, add marker to indicate data at top of page?)
- Add command that manipulates the behaviour of the tabo? (tabular output)
- A describe command that shows column headers
- Process management (Ctrl-C, bg, fg, jobs)
- File open dialog (I start Typing a long command that works on a filename, but I forget (or do not know) the filename. So i press Ctrl-O and a nice dialog allows me to search for the file server-side. Indexed search, browse, recent searches, ...
- Allow column names to contain spaces (the Write command does not expect spaces in table names) 
- Propagate server-side exceptions back to the client
- Add error stream. Each error should be able to know what output it is linked to? Same goes for logs?
- Advanced clipboard, specialised for shell operations: 
- Protect against copy/paste attacks See: https://thejh.net/misc/website-terminal-copy-paste
- 