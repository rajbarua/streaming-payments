package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PmtId {
    @JacksonXmlProperty(localName = "InstrId")
    private String instrId;

    @JacksonXmlProperty(localName = "EndToEndId")
    private String endToEndId;
    
    public String getInstrId() {
        return instrId;
    }

    public void setInstrId(String instrId) {
        this.instrId = instrId;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }
}
