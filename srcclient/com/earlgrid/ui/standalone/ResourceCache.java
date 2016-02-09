package com.earlgrid.ui.standalone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ResourceCache {
  static ResourceCache instance;
  
  synchronized public static ResourceCache getInstance(){
    if(instance==null){
      instance=new ResourceCache();
    }
    return instance;
  }

  public final Image CLOSE_ICON;
  public final Image CLOSE_ICON_RED;
  public final Image COLLAPSE_ICON;
  public final Font monospaceFont;
  public final Font fontAwesomeFont;
  public final Image APPLICATION_ICON;
  public final Image TIMER_ICON;
  public final Image EDIT_ICON;
  public final Image COMMAND_ICON;

  public ResourceCache() {
    CLOSE_ICON_RED=new Image(Display.getDefault(), "resources/png/circle-x-2x-red.png");
    CLOSE_ICON=new Image(Display.getDefault(), "resources/png/circle-x-2x.png");
    COLLAPSE_ICON=new Image(Display.getDefault(), "resources/png/chevron-top-2x.png");
    TIMER_ICON=new Image(Display.getDefault(), "resources/png/timer-2x.png");
    EDIT_ICON=new Image(Display.getDefault(), "resources/png/pencil-2x.png");
    COMMAND_ICON=new Image(Display.getDefault(), "resources/png/command-2x.png");

    monospaceFont = new Font(Display.getDefault(), new FontData("Courier New", 12, SWT.NONE));
    Display.getCurrent().loadFont("resources/fontawesome-webfont.ttf"); //This will not work if the client is delivered in a jarfile, see http://www.patrikdufresne.com/en/fontawesome-swt/
    fontAwesomeFont = new Font(Display.getDefault(), new FontData("FontAwesome", 12, SWT.NONE));
    
    APPLICATION_ICON = new Image(Display.getDefault(), "resources/small-7086-10298825.png");
  }

}
