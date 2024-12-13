package com.hz.demo.pmt.pain001;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Pain001Reader {
    XmlMapper xmlMapper = null;
    public Pain001Reader(){
        xmlMapper = new XmlMapper();
    }
    public static void main(String[] args) {
        String xml = """
        <Document><CstmrCdtTrfInitn><GrpHdr><MsgId>MSGID12345</MsgId><CreDtTm>2024-10-16T10:36:45.074522</CreDtTm><NbOfTxs>1</NbOfTxs><CtrlSum>1000.0</CtrlSum><InitgPty><Nm>Initiating Party Name</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>PMTINFID12345-1</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>1</NbOfTxs><CtrlSum>1000.0</CtrlSum><ReqdExctnDt>2023-10-02</ReqdExctnDt><Dbtr><Nm>Debtor Name</Nm></Dbtr><DbtrAcct><Id><IBAN>DE89370400440532013000</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>DEUTDEFF</BIC></FinInstnId></DbtrAgt><CdtTrfTxInf><PmtId><InstrId>INSTRID12345-1</InstrId><EndToEndId>ETOEID12345-1</EndToEndId></PmtId><Amt><InstdAmt><value>1000.0</value><Ccy>EUR</Ccy></InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>DEUTDEFF</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Creditor Name</Nm></Cdtr><CdtrAcct><Id><IBAN>DE89370400440532013001</IBAN></Id></CdtrAcct><RmtInf><Ustrd>Invoice 12345-1</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>
        """;
        Pain001Reader pain001Reader = new Pain001Reader();
        try {
            Document doc = pain001Reader.readXML(xml);
            System.out.println(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Document readXML(String xml){
        Document doc = null;
        try{
            doc = xmlMapper.readValue(xml, Document.class);
        }catch(Exception e){
            e.printStackTrace();
            doc = new Document();
            doc.setParseError(e.getMessage());
        }
        return doc;
    }

}
