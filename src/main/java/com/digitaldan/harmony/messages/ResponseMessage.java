package com.digitaldan.harmony.messages;

public abstract class ResponseMessage extends Message {
    private int code;
    private String id;
    private String msg;

    public ResponseMessage(int code, String id, String msg) {
        super();
        this.code = code;
        this.id = id;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

}
