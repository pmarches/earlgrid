package com.earlgrid.remoting.serverside;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.earlgrid.core.serverside.EarlGridPb.PbTopLevel;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

public class IOEndPoint {
  protected CodedOutputStream protobufOut;
  protected CodedInputStream protobufIn;
  protected InputStream protobufErr;
  private OutputStream out;

  public static IOEndPoint[] createEndPointConnectedByPipes() throws IOException {
    PipedOutputStream os1=new PipedOutputStream();
    PipedInputStream is1=new PipedInputStream(os1);

    PipedOutputStream os2=new PipedOutputStream();
    PipedInputStream is2=new PipedInputStream(os2);

    IOEndPoint leftEndPoint=new IOEndPoint(os2, is1, null);
    IOEndPoint rightEndPoint=new IOEndPoint(os1, is2, null);
    return new IOEndPoint[]{leftEndPoint, rightEndPoint};
  }

  public IOEndPoint(OutputStream out, InputStream in, InputStream inputStream) {
    this.out=out;
    this.protobufOut=CodedOutputStream.newInstance(out);
    this.protobufIn=CodedInputStream.newInstance(in);
    this.protobufErr=inputStream;
  }

  public PbTopLevel readTopLevel() throws IOException {
    PbTopLevel.Builder topMsg=PbTopLevel.newBuilder();
    protobufIn.readMessage(topMsg, null);
    return topMsg.build();
  }

  public void writeTopLevel(PbTopLevel topMsg) throws IOException {
    protobufOut.writeMessageNoTag(topMsg);
    protobufOut.flush();
    out.flush(); //FIXME Why doesn't the protobuf flush out?
  }

}
