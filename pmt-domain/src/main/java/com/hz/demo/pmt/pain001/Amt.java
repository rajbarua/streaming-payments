package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Amt {
    @JacksonXmlProperty(localName = "InstdAmt")
    private InstdAmt instdAmt;
    
    public InstdAmt getInstdAmt() {
        return instdAmt;
    }

    public void setInstdAmt(InstdAmt instdAmt) {
        this.instdAmt = instdAmt;
    }
}
