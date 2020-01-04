package org.qpython.qpy.main.server.model;

/**
 * Created by Hmei
 * 2/11/18.
 */

public class PayStatusModel {


    /**
     * errorno : 0
     * payed : 1
     * msg :
     */

    private int errorno;
    private int    payed; // >0 -> 已参与众筹/购买
    private String msg;

    public int getErrorno() {
        return errorno;
    }

    public void setErrorno(int errorno) {
        this.errorno = errorno;
    }

    public int getPayed() {
        return payed;
    }

    public void setPayed(int payed) {
        this.payed = payed;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
