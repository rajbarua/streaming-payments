package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RmtInf {
    @JacksonXmlProperty(localName = "Ustrd")
    private String ustrd;
    
    public String getUstrd() {
        return ustrd;
    }

    public void setUstrd(String ustrd) {
        this.ustrd = ustrd;
    }
}
