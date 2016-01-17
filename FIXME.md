Stuff that needs fixing
====

- When a command completes, it should check if a redraw is required. Would get rid of the lag for small rapid commands like pwd
- Re-order git checkins
- TaskId should be hidden in the emit() function
- Clipboard command does not handle multiple columns
- Refactor KeyBindings
- Figure out how Meta mode is supposed to work
- Figure out how Select mode is supposed to work
- Exceptions are not shown in the task widget
- PageUp/Down does not work
- try it out with SSH
- Add mock typical command that executes a typical set of command

Task Widget painting problems:
- Status icon for each task is too far to the right
- TaskWidget painting shows a thin line at the bottom
- More than 1638 rows cause problem: This is caused by the NATTable being larger than 32768 pixels, which windows is unable to handle. Looks like we need to handle the drawing explicitly. Man, this sucks.
- when left idle for a while, the widgets get resized at the top in a small bunch.
- 
