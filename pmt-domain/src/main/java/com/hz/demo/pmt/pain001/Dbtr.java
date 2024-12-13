package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Dbtr {
    @JacksonXmlProperty(localName = "Nm")
    private String nm;
    
    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }
}