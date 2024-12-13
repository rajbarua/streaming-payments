package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class InstdAmt {
    @JacksonXmlProperty(localName = "value")
    private double value;

    @JacksonXmlProperty(localName = "Ccy")
    private String ccy;

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
