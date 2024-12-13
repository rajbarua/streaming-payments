package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class GrpHdr {
    @JacksonXmlProperty(localName = "MsgId")
    private String msgId;

    @JacksonXmlProperty(localName = "CreDtTm")
    private String creDtTm;

    @JacksonXmlProperty(localName = "NbOfTxs")
    private int nbOfTxs;

    @JacksonXmlProperty(localName = "CtrlSum")
    private double ctrlSum;

    @JacksonXmlProperty(localName = "InitgPty")
    private InitgPty initgPty;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getCreDtTm() {
        return creDtTm;
    }

    public void setCreDtTm(String creDtTm) {
        this.creDtTm = creDtTm;
    }

    public int getNbOfTxs() {
        return nbOfTxs;
    }

    public void setNbOfTxs(int nbOfTxs) {
        this.nbOfTxs = nbOfTxs;
    }

    public double getCtrlSum() {
        return ctrlSum;
    }

    public void setCtrlSum(double ctrlSum) {
        this.ctrlSum = ctrlSum;
    }

    public InitgPty getInitgPty() {
        return initgPty;
    }

    public void setInitgPty(InitgPty initgPty) {
        this.initgPty = initgPty;
    }
}