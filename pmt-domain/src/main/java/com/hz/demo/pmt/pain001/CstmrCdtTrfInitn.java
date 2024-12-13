package com.hz.demo.pmt.pain001;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

// @JsonPropertyOrder({"grpHdr", "pmtInf"})
public class CstmrCdtTrfInitn {
    
    @JacksonXmlProperty(localName = "GrpHdr")
    private GrpHdr grpHdr;

    public GrpHdr getGrpHdr() {
        return grpHdr;
    }

    
    public void setGrpHdr(GrpHdr grpHdr) {
        this.grpHdr = grpHdr;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "PmtInf")
    private List<PmtInf> pmtInf;

    public List<PmtInf> getPmtInf() {
        return pmtInf;
    }

    public void setPmtInf(List<PmtInf> pmtInf) {
        this.pmtInf = pmtInf;
    }
}