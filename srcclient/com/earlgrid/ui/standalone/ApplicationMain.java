package com.earlgrid.ui.standalone;

import java.io.IOException;

import com.earlgrid.core.sessionmodel.SessionModel;
import com.earlgrid.remoting.InteractiveFormHandler;
import com.earlgrid.remoting.LoopbackRemotingClient;
import com.earlgrid.remoting.RemoteHostConfiguration;
import com.earlgrid.remoting.RemotingClient;
import com.earlgrid.remoting.SSHRemotingClient;
import com.earlgrid.remoting.serverside.ServerSideHasShutDownException;
import com.earlgrid.ui.model.HostManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class ApplicationMain {
  static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ApplicationMain.class);
  Display display;
  public ApplicationMainWindow mainWindow;
  public ApplicationKeyListener keyListener;

  ApplicationCmdLineOptions args;
  HostManager hostManager;
  public RemotingClient client;
  InteractiveFormHandler interactiveFormHandler=new InteractiveFormHandler(this);

  public ApplicationMain() {
    hostManager = new HostManager();
    keyListener = new ApplicationKeyListener(this);
  }

  public void acceptUserCommand(String userEnteredCommandText) {
    try {
      log.debug("accepted " + userEnteredCommandText);
      client.executeCommand(userEnteredCommandText);
    } catch(ServerSideHasShutDownException e){
      //We shutdown differently if the server-side has shutdown first. No need to send a shutdown request..
      try {
        client.shutdownLocalEnd();
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      display.dispose();
    } catch (Exception e) {
      display.asyncExec(new Runnable(){
        @Override
        public void run() {
          MessageBox messageBox = new MessageBox(mainWindow.getShell(), SWT.ICON_ERROR | SWT.ABORT );
          messageBox.setText("Error");
          messageBox.setMessage("The command '"+userEnteredCommandText+"' caused the exception "+e.getMessage());
          messageBox.open();
        }
      });
    }
  }

  private void execute(ApplicationCmdLineOptions args) {
    this.args = args;
    display = new Display();

    if (args.sessionName!=null) {
      new SelectHostToConnectWindow(this);
    } else {
      try {
        client=new LoopbackRemotingClient(interactiveFormHandler);
      } catch (IOException e) {
        log.error("Exception caught in "+getClass().getName(), e);
      }
      mainWindow = new ApplicationMainWindow(this);
      mainWindow.open();
      //////////////////// FIXME TESTING STUFF HERE /////////////////////
      if(false){
        try {
          client.executeCommand("mock output 11 5");
          client.executeCommand("mock who|delay");
          client.executeCommand("mock output 3 200");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      //////////////////// FIXME TESTING STUFF HERE /////////////////////
    }


    // run the event loop as long as the window is open
    while (!display.isDisposed()) {
      // read the next OS event queue and transfer it to a SWT event
      if (!display.readAndDispatch()) {
        // if there are currently no other OS event to process
        // sleep until the next OS event is available
        display.sleep();
      }
    }
    exitApplication();
  }

  public void exitApplication() {
    try {
      if (client != null) {
        client.shutdown();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    display.dispose();
  }

  public void openRemoteTerminalWindow(RemoteHostConfiguration remoteHostConfiguration) throws Exception {
    client = new SSHRemotingClient(remoteHostConfiguration, interactiveFormHandler);
    mainWindow = new ApplicationMainWindow(this);
    mainWindow.open();
  }

  public static void main(String[] args) {
    ApplicationMain app = new ApplicationMain();
    app.execute(ApplicationCmdLineOptions.createFromStrings(args));
  }

  public SessionModel getSessionModel(){
    return client.getSessionModel();
  }


  public void showCommandLineHistoryWindow() {
    CommandHistoryWindow historyWindow = new CommandHistoryWindow(this);
    historyWindow.open();
  }

  public void showTaskOutputWindow() {
    TaskOutputWindow taskOutputWindow=new TaskOutputWindow(this);
    taskOutputWindow.open();
  }

  public void showInteractiveInputWindow() {
    InteractiveInputWindow interactiveInputWindow=new InteractiveInputWindow(this);
    interactiveInputWindow.open();
  }

  public void setFocusToTaskOutput(int taskIdToFocus) {
    mainWindow.historyArea.setFocusOnTask(taskIdToFocus);
  }

}
