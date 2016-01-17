package com.earlgrid.core.shellcommands.coreutils;

import com.earlgrid.core.serverside.EarlGridPb.PbFormField;
import com.earlgrid.core.serverside.EarlGridPb.PbInteractiveForm;
import com.earlgrid.core.sessionmodel.TabularOutputColumn;
import com.earlgrid.core.sessionmodel.TabularOutputColumnHeader;
import com.earlgrid.core.sessionmodel.TabularOutputRow;
import com.earlgrid.core.sessionmodel.TabularOutput.ColumnType;

public class PromptCmdSpecification extends CollectingCmdSpecification<PromptCmdArguments> {
  @Override
  protected void onThisCommandExecute() throws Exception {
    PbInteractiveForm.Builder requestedForm=PbInteractiveForm.newBuilder();
    if(args.title!=null){
      requestedForm.setFormTitle(args.title);
    }

    PbFormField.Builder requestedField=PbFormField.newBuilder();
    if(args.fieldName!=null){
      requestedField.setFieldLabel(args.fieldName);
    }
    if(args.passwordMode){
      if(args.fieldName==null){
        requestedField.setFieldLabel("Password");
      }
      requestedField.setConcealUserInput(true);
    }
    requestedForm.addFields(requestedField);
    
    PbInteractiveForm userResponse=session.requestUserInteraction(requestedForm);
    PbFormField responseField = userResponse.getFields(0);
    emit(new TabularOutputColumnHeader(taskId, new TabularOutputColumn(responseField.getFieldLabel(), ColumnType.STRING)));
    String userEnteredValue=responseField.getFieldValue();
    emit(new TabularOutputRow(taskId, userEnteredValue));
  }
}
