package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CdtrAgt {
    @JacksonXmlProperty(localName = "FinInstnId")
    private FinInstnId finInstnId;
    
    public FinInstnId getFinInstnId() {
        return finInstnId;
    }

    public void setFinInstnId(FinInstnId finInstnId) {
        this.finInstnId = finInstnId;
    }
}
