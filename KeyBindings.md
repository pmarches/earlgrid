#Key bindings

Key bindings inside a tabular output:
 Escape : Returns focus to command line text box
 XXXx   : Hide/Show column headers
 XXXX   : Wrap long cell values, make row taller
 XXX    : Process management (foreground/background/...)

Need to provide instance feedback when use changes edition mode. Ex; When going to selection mode, the selection should immediatly change as the user types in the addresses.

# Editor modes
## Shell mode
Executes shell commands on the host. This is the default.

* Up/Down arrows recall history
* Page up/Down 
* Ctrl-G : Prompts for the output number to set focus Go to
* Ctrl-H : Shows the command history as a list
* Ctrl-Z : Closes the last executed output
* Shift-Up/Down: Scrolls the execution outputs by one line
* Shift-PageUp/PageDown: Scrolls outputs by one page

##Meta mode
Allows execution of command that manipulate the earlgrid application. earlgrid should be controlled by keyboard as much as possible. What kind of commands do we need? Should this mode include the vi/emacs modes?
* Shows internal dialogs (preferences, settings, keys, ...)
* Window management (Create windows that can be moved here and there
* Exit the application 

##vi/emacs mode
Allows for moving around the text box?

##Interactive Select mode
 Escape : Returns to shell mode
 Arrows : Moves the cursor around
 Shift  : Enables selection
 +/-    : Selection arithmetics

* Popout a selection of tabular output(s)
* Select cells from certain output

##Interactive input Mode
Shows a grid to allow interactive input from the user. When the user is ready, he can press Ctrl-D to close the windows and let the computation proceed. This should work with any command invoked as the first command that reads undefined input.

##Tabularo editor
Popout a single tabular output for selection, copy.

