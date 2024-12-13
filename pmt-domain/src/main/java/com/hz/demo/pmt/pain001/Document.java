package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Document")
public class Document {
    
    private CstmrCdtTrfInitn cstmrCdtTrfInitn;

    @JacksonXmlProperty(localName = "CstmrCdtTrfInitn")
    public CstmrCdtTrfInitn getCstmrCdtTrfInitn() {
        return cstmrCdtTrfInitn;
    }

    public void setCstmrCdtTrfInitn(CstmrCdtTrfInitn cstmrCdtTrfInitn) {
        this.cstmrCdtTrfInitn = cstmrCdtTrfInitn;
    }
    ///////// Metadata attributes start /////////
    @JsonIgnore
    String parseError = null;
    @JsonIgnore
    String key = null;
    public String getParseError() {
        return parseError;
    }
    public void setParseError(String message) {
        parseError = message;
    }
    @JsonIgnore
    public boolean isErrored() {
        return parseError != null;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
    ///////// Metadata attributes end /////////
}
