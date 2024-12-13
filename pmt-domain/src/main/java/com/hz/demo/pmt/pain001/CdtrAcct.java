package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrAcct {
    @JacksonXmlProperty(localName = "Id")
    private Id id;
    
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }
}
