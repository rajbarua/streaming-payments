package com.hz.demo.pmt.pain001;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PmtInf {
    @JacksonXmlProperty(localName = "PmtInfId")
    private String pmtInfId;

    @JacksonXmlProperty(localName = "PmtMtd")
    private String pmtMtd;

    @JacksonXmlProperty(localName = "BtchBookg")
    private boolean btchBookg;

    @JacksonXmlProperty(localName = "NbOfTxs")
    private int nbOfTxs;

    @JacksonXmlProperty(localName = "CtrlSum")
    private double ctrlSum;

    @JacksonXmlProperty(localName = "ReqdExctnDt")
    private String reqdExctnDt;

    @JacksonXmlProperty(localName = "Dbtr")
    private Dbtr dbtr;

    @JacksonXmlProperty(localName = "DbtrAcct")
    private DbtrAcct dbtrAcct;

    @JacksonXmlProperty(localName = "DbtrAgt")
    private DbtrAgt dbtrAgt;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CdtTrfTxInf")
    private List<CdtTrfTxInf> cdtTrfTxInf = new ArrayList<>();

    public String getPmtInfId() {
        return pmtInfId;
    }

    public void setPmtInfId(String pmtInfId) {
        this.pmtInfId = pmtInfId;
    }

    public String getPmtMtd() {
        return pmtMtd;
    }

    public void setPmtMtd(String pmtMtd) {
        this.pmtMtd = pmtMtd;
    }

    public boolean isBtchBookg() {
        return btchBookg;
    }

    public void setBtchBookg(boolean btchBookg) {
        this.btchBookg = btchBookg;
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

    public String getReqdExctnDt() {
        return reqdExctnDt;
    }

    public void setReqdExctnDt(String reqdExctnDt) {
        this.reqdExctnDt = reqdExctnDt;
    }

    public Dbtr getDbtr() {
        return dbtr;
    }

    public void setDbtr(Dbtr dbtr) {
        this.dbtr = dbtr;
    }

    public DbtrAcct getDbtrAcct() {
        return dbtrAcct;
    }

    public void setDbtrAcct(DbtrAcct dbtrAcct) {
        this.dbtrAcct = dbtrAcct;
    }

    public DbtrAgt getDbtrAgt() {
        return dbtrAgt;
    }

    public void setDbtrAgt(DbtrAgt dbtrAgt) {
        this.dbtrAgt = dbtrAgt;
    }

    public List<CdtTrfTxInf> getCdtTrfTxInf() {
        return cdtTrfTxInf;
    }

    public void setCdtTrfTxInf(List<CdtTrfTxInf> cdtTrfTxInf) {
        this.cdtTrfTxInf = cdtTrfTxInf;
    }
    public void addCdtTrfTxInf(CdtTrfTxInf cdtTrfTxInf) {
        this.cdtTrfTxInf.add(cdtTrfTxInf);
    }
    ////// Metadata attributes start //////
    private Integer metadataCounter=0;
    public Integer getMetadataCounter() {
        return metadataCounter;
    }
    public void setMetadataCounter(Integer metadataCounter) {
        this.metadataCounter = metadataCounter;
    }
    Map<String, CdtTrfTxInf> metadataCreditorDetail = new HashMap<>();
    public void addMetadataCreditorDetail(CdtTrfTxInf cdtTrfTxInf) {
        String bic = cdtTrfTxInf.getCdtrAgt().getFinInstnId().getBic();
        metadataCreditorDetail.merge(bic, cdtTrfTxInf, (oldValue, newValue) -> {
            oldValue.getAmt().getInstdAmt().setValue(oldValue.getAmt().getInstdAmt().getValue() + newValue.getAmt().getInstdAmt().getValue());
            return oldValue;
        });
    }
    public Map<String, CdtTrfTxInf> getMetadataCreditorDetail() {
        return metadataCreditorDetail;
    }

    ////// Metadata attributes end //////

}