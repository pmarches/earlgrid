//syntax = "proto3";
package com.earlgrid.core.serverside;

message PbTopLevel {
  enum MessageType {
    NOTIFICATION=0;
    REQUEST=1;
    RESPONSE=2;
  };
  optional MessageType messageType=1;
  optional uint32 requestId=2; //This is set wheter it is a request or response
  optional PbExceptionSpecification exceptionOccured=5;
  
  optional PbRemoting remoting=10;
  optional PbCommandLineUserAction userAction=20;
  optional PbMultiPartData multipartData=30;
  optional PbFileSystemManagement fileSystemManagement=40;
  optional PbSessionModelChange sessionModelChange=50;
  optional PbInteractiveForm clientSideInteractiveForm=60;
  optional PbClientClipboard clientClipboard=61;
}

message PbExceptionSpecification {
  optional string exceptionClass=1;
  optional string message=2;
  optional string stackTrace=3;
}

message PbExecutableCode {
  optional string language=1;
  optional bytes executableBytes=2;
}

message PbClientClipboard {
  enum ClipboardOperation {
    READ_CLIPBOARD=0;
    WRITE_CLIPBOARD=1;
  };
  optional ClipboardOperation operation=1;
  optional string clipboardContent=2;
}

message PbFormField {
  optional string fieldLabel=1;
  optional string fieldValue=2;
  optional bool concealUserInput=4;
}

message PbFormButton {
  optional string buttonLabel=1;
}

message PbInteractiveForm {
  optional string formTitle=1;
  repeated PbFormField fields=2;
  repeated PbFormButton buttons=3;
}

message PbPathSpecification {
  repeated string pathElement=1;
}

message PbPermissionSpecification {
}

message PbFileSystemObjectSpecification {
  optional PbPathSpecification path=1;
  optional string permission=2;
  optional int64 sizeInBytes=3;
  enum FilesystemObjectType {
    FILE=0;
    DIRECTORY=1;
  };
  optional FilesystemObjectType type=4;
}

message PbFileSystemObjectAttributesRequested {
  optional bool includePath=1;
  optional bool includeSizeInBytes=2;
  optional bool includeSHA512Hash=3;
  optional bool includePermissions=4;
}

message PbFileSystemManagement {
  enum PbOperation {
    GET=1;
    PUT=2;
    LIST=3;
    FIND=4;
    CWD=5;
    MONITOR_FOR_CHANGE=6;
  }
  optional PbOperation operation=1;
  
  optional PbPathSpecification path=10;
  optional string permissions=11;
  optional PbFileSystemObjectAttributesRequested attributesRequested=31;

  optional PbExecutableCode findPredicate=20;
  repeated PbFileSystemObjectSpecification fileListing=30;
}

message PbFileSystemManagementTransfertFileSpecification {
  optional PbPathSpecification path=10;
  optional string permissions=11;
}

message PbFileSystemManagementFindSpecification {
  optional PbPathSpecification startPath=1;
  optional PbFileSystemObjectAttributesRequested attributesRequested=2;
  optional PbExecutableCode findPredicate=3;
}

message PbMultiPartData {
  optional int64 payloadOffSet=1;
  optional bytes payload=2;
  optional bool endOfDataTransferReached=3;
}

message PbRemoting {
  optional bool shutdown=1;
  optional bool ping=2;
  optional bool pong=3;
  optional string loadTestClass=100;
}

message PbCommandLineUserAction {
  optional string commandLine=1;
  optional uint32 caretPosition=2;
  enum UserRequestedActionKind {
    EXECUTE=0;
    AUTO_COMPLETE=1;
  };
  optional UserRequestedActionKind actionKind=3;
//  optional uint32 clientSideCookie=4; 
}

message PbRemoveTaskFromHistory {
}

message PbSessionModelChange {
  optional uint32 taskId=1;
  optional string commandString=2;
  
  enum PbTaskState { 
    RUNNING=1;
    FINISHED=2;
    SUSPENDED=3;
  }
  optional PbTaskState taskState=3;
  
//  optional ExceptionSpecification exception=5;
  optional string outputName=6;
  optional PbTabularColumnHeaders header=7;
  repeated PbTabularRow row=8;
  optional PbPathSpecification newWorkingDirectory=9;
  optional PbRemoveTaskFromHistory removeTask=10;
  optional PbOutputStreamChange streamChange=11;
}

message PbProgress {
}


message PbTabularColumnHeaders {
  repeated string columnName=1;
}

message PbTabularRow {
  repeated string cells=1;
}

////////////////////
message PbOutputStreamValueNumber {
  optional int64 intNumber=1;
}

message PbOutputStreamValueObjectAttribute {
  optional string attributeName=1;
  optional PbOutputStreamValue value=2;
}

message PbOutputStreamValueObject {
  repeated PbOutputStreamValueObjectAttribute attributes=1;
}

message PbOutputStreamValueBytes {
  optional uint64 offset=1;
  optional bytes payload=2;
  optional uint64 totalSize=3;
  optional bool utf8Encoding=4;
}

message PbOutputStreamValueArray {
  optional uint32 nbElements=1;
}

message PbOutputStreamValue {
  optional PbOutputStreamValueNumber number=1;
  optional PbOutputStreamValueObject object=2;
  optional PbOutputStreamValueBytes bytes=3;
  optional PbOutputStreamValueArray array=4;
}

message PbOutputStreamTabularSchema {
  repeated string columnName=1;
//  repeated enum COLUMN_TYPE columnType=2;
  repeated uint32 estimatedNumberOfRows=3;
}

message PbOutputStreamHiearchySchema {
//  optional bool isStrictSchema=1; 
  repeated uint32 estimatedNumberOfAttributes=3;
}


message PbOutputStreamSchema {
  optional string mimeType=1;
  optional PbOutputStreamTabularSchema tabularSchema=2;
  optional PbOutputStreamHiearchySchema hiearchySchema=3;
}

message PbOutputStreamByteChunk {
  optional uint64 offset=1;
  optional bytes chunk=2;
}

message PbOutputStreamChange {
  optional uint32 streamId=1;
  enum PbStreamDisposition {
    ALTERNATE=0;
    INLINE=1;
    ATTACHMENT=2;
  };
  optional PbStreamDisposition disposition=2;
  optional PbOutputStreamSchema schema=3;
  optional PbOutputStreamByteChunk chunk=4;
}

