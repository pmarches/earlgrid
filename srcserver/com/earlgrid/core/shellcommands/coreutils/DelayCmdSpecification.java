package com.earlgrid.core.shellcommands.coreutils;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.shellcommands.BaseCmdSpecification;

public class DelayCmdSpecification extends BaseCmdSpecification<DelayCmdArguments> {
  long lastEmissionTimeStamp;
  
  @Override
  public void onUpstreamCommandOutputRow(TabularOutputRow outputElement) throws Exception {
    int nbMsToWait=0;
    if(lastEmissionTimeStamp!=0){
      long nbMsElapsedSinceLastEmission=System.currentTimeMillis()-lastEmissionTimeStamp;
      nbMsToWait=(int) (args.delayInMS-nbMsElapsedSinceLastEmission);
      if(nbMsToWait<0){
        nbMsToWait=0;
      }
    }
    Thread.sleep(nbMsToWait);
    lastEmissionTimeStamp=System.currentTimeMillis();
    
    emit(outputElement);
  }
}
