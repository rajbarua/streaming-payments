package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class FinInstnId {
    @JacksonXmlProperty(localName = "BIC")
    private String bic;
    
    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    ///// Metadata Attributes start /////
    String metadataBicAlias;
    public void setMetadataBicAlias(Object bicAlias) {
        this.metadataBicAlias = bicAlias == null ? "" : bicAlias.toString();
    }
    public String getMetadataBicAlias() {
        return metadataBicAlias;
    }
    String metadataAccNum;
    public void setMetadataAccNum(Object accNum) {
        this.metadataAccNum = accNum == null ? "" : accNum.toString();
    }
    public String getMetadataAccNum() {
        return metadataAccNum;
    }
    ///// Metadata Attributes end /////

}
