package com.digitaldan.harmony.messages;

public class ErrorResponseMessage extends ResponseMessage {

    public ErrorResponseMessage(int code, String id, String msg) {
        super(code, id, msg);

    }

}
