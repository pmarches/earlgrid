package com.earlgrid.remoting.serverside;


public class RemotingClassLoader extends ClassLoader {
  private IOEndPoint endPoint;

  public RemotingClassLoader(IOEndPoint endPoint) {
    this.endPoint=endPoint;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return super.findClass(name);
  }
}
