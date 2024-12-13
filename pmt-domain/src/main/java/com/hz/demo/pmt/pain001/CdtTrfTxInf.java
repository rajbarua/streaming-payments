package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtTrfTxInf {
    @JacksonXmlProperty(localName = "PmtId")
    private PmtId pmtId;

    @JacksonXmlProperty(localName = "Amt")
    private Amt amt;

    @JacksonXmlProperty(localName = "CdtrAgt")
    private CdtrAgt cdtrAgt;

    @JacksonXmlProperty(localName = "Cdtr")
    private Cdtr cdtr;

    @JacksonXmlProperty(localName = "CdtrAcct")
    private CdtrAcct cdtrAcct;

    @JacksonXmlProperty(localName = "RmtInf")
    private RmtInf rmtInf;

    
    public PmtId getPmtId() {
        return pmtId;
    }

    public void setPmtId(PmtId pmtId) {
        this.pmtId = pmtId;
    }

    public Amt getAmt() {
        return amt;
    }

    public void setAmt(Amt amt) {
        this.amt = amt;
    }

    public CdtrAgt getCdtrAgt() {
        return cdtrAgt;
    }

    public void setCdtrAgt(CdtrAgt cdtrAgt) {
        this.cdtrAgt = cdtrAgt;
    }

    public Cdtr getCdtr() {
        return cdtr;
    }

    public void setCdtr(Cdtr cdtr) {
        this.cdtr = cdtr;
    }

    public CdtrAcct getCdtrAcct() {
        return cdtrAcct;
    }

    public void setCdtrAcct(CdtrAcct cdtrAcct) {
        this.cdtrAcct = cdtrAcct;
    }

    public RmtInf getRmtInf() {
        return rmtInf;
    }

    public void setRmtInf(RmtInf rmtInf) {
        this.rmtInf = rmtInf;
    }
    ///////// Payment Information attributes start /////////
    private String metadataPmtInfId;
    public String getMetadataPmtInfId() {
        return metadataPmtInfId;
    }

    public void setMetadataPmtInfId(String metadataPmtInfId) {
        this.metadataPmtInfId = metadataPmtInfId;
    }

    private int metadataCdtTrfTxInfCnt;

    public int getMetadataCdtTrfTxInfCnt() {
        return metadataCdtTrfTxInfCnt;
    }

    public void setMetadataCdtTrfTxInfCnt(int metadataCdtTrfTxInfCnt) {
        this.metadataCdtTrfTxInfCnt = metadataCdtTrfTxInfCnt;
    }
    DbtrAgt metadataDbtrAgt;
    public DbtrAgt getMetadataDbtrAgt() {
        return metadataDbtrAgt;
    }

    public void setMetadataDbtrAgt(DbtrAgt dbtrAgt) {
        this.metadataDbtrAgt = dbtrAgt;
    }
    
    private boolean metedataIsDuplicate;
    public boolean isMetedataIsDuplicate() {
        return metedataIsDuplicate;
    }
    public void setMetedataIsDuplicate(boolean metedataIsDuplicate) {
        this.metedataIsDuplicate = metedataIsDuplicate;
    }
    ///////// Payment Information attributes end /////////

}
