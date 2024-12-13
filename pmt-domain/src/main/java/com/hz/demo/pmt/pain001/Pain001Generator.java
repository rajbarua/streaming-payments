package com.hz.demo.pmt.pain001;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Pain001Generator implements Serializable{

    private static final int NUM_PMTINF = Integer.parseInt(System.getenv().getOrDefault("NUM_PMTINF", "1"));
    private static final int NUM_CDTTRFTXINF = Integer.parseInt(System.getenv().getOrDefault("NUM_CDTTRFTXINF", "1"));

    public static void main(String[] args) throws Exception {
        Pain001Generator gen = new Pain001Generator();
        gen.outXML(System.out);
    }

    public Document outXML(OutputStream os) {
        // mvn exec:java -Dexec.mainClass="com.hz.demos.pain001.Pain001Generator" -pl
        // att-transaction-producer-iso20022
        // Create the Document object
        Document document = new Document();
        CstmrCdtTrfInitn cstmrCdtTrfInitn = new CstmrCdtTrfInitn();

        // Set Group Header
        GrpHdr grpHdr = new GrpHdr();
        grpHdr.setMsgId("MSGID12345");
        // Get current date and time
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        grpHdr.setCreDtTm(currentDateTime);

        // Set Payment Information
        // Generate random PmtInf elements
        Random random = new Random();
        int numberOfPmtInf = random.nextInt(NUM_PMTINF) + 1;
        List<PmtInf> pmtInfList = new ArrayList<>();
        int totalTransactions = 0;
        for (int i = 0; i < numberOfPmtInf; i++) {
            PmtInf pmtInf = getPmtInf(i);
            pmtInfList.add(pmtInf);
            totalTransactions += pmtInf.getCdtTrfTxInf().size();
        }
        cstmrCdtTrfInitn.setPmtInf(pmtInfList);

        // Set NbOfTxs based on total number of transactions
        grpHdr.setNbOfTxs(totalTransactions);
        grpHdr.setCtrlSum(1000.00);
        InitgPty initgPty = new InitgPty();
        initgPty.setNm("Initiating Party Name");
        grpHdr.setInitgPty(initgPty);
        cstmrCdtTrfInitn.setGrpHdr(grpHdr);

        document.setCstmrCdtTrfInitn(cstmrCdtTrfInitn);

        // Marshal the Document object to XML
        XmlMapper xmlMapper = new XmlMapper();
        // Serialize the document object to XML and write to OutputStream
        try {
            xmlMapper.writeValue(os, document);
        } catch (Exception e) {
            throw new RuntimeException("Error marshalling the document object to XML", e);
        }

        // JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
        // Marshaller marshaller = jaxbContext.createMarshaller();
        // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // marshaller.marshal(document, os);
        return document;
    }

    private PmtInf getPmtInf(int numPmt) {
        PmtInf pmtInf = new PmtInf();
        pmtInf.setPmtInfId("PMTINFID12345-" + (numPmt + 1));
        pmtInf.setPmtMtd("TRF");
        pmtInf.setBtchBookg(false);
        pmtInf.setNbOfTxs(1);
        pmtInf.setCtrlSum(1000.00);
        pmtInf.setReqdExctnDt("2023-10-02");

        Dbtr dbtr = new Dbtr();
        dbtr.setNm("Debtor Name");
        pmtInf.setDbtr(dbtr);

        DbtrAcct dbtrAcct = new DbtrAcct();
        Id dbtrAcctId = new Id();
        dbtrAcctId.setIban("DE89370400440532013000");
        dbtrAcct.setId(dbtrAcctId);
        pmtInf.setDbtrAcct(dbtrAcct);

        DbtrAgt dbtrAgt = new DbtrAgt();
        FinInstnId dbtrAgtFinInstnId = new FinInstnId();
        dbtrAgtFinInstnId.setBic("DEUTDEFF");
        dbtrAgt.setFinInstnId(dbtrAgtFinInstnId);
        pmtInf.setDbtrAgt(dbtrAgt);

        Random random = new Random();
        int numberOfPmtInf = random.nextInt(NUM_CDTTRFTXINF) + 1;
        List<CdtTrfTxInf> cdtTrfTxInfList = new ArrayList<>();
        for (int i = 0; i < numberOfPmtInf; i++) {
            CdtTrfTxInf cdtTrfTxInf = createCreditTransferTransactionInformation(i);
            cdtTrfTxInfList.add(cdtTrfTxInf);
        }
        pmtInf.setCdtTrfTxInf(cdtTrfTxInfList);
        return pmtInf;
    }

    private CdtTrfTxInf createCreditTransferTransactionInformation(int i) {
        // Set Credit Transfer Transaction Information
        CdtTrfTxInf cdtTrfTxInf = new CdtTrfTxInf();
        PmtId pmtId = new PmtId();
        pmtId.setInstrId("INSTRID12345-" + (i + 1));
        pmtId.setEndToEndId("ETOEID12345-" + (i + 1));
        cdtTrfTxInf.setPmtId(pmtId);

        Amt amt = new Amt();
        InstdAmt instdAmt = new InstdAmt();
        instdAmt.setCcy("EUR");
        instdAmt.setValue(1000.00);
        amt.setInstdAmt(instdAmt);
        cdtTrfTxInf.setAmt(amt);

        CdtrAgt cdtrAgt = new CdtrAgt();
        FinInstnId cdtrAgtFinInstnId = new FinInstnId();
        cdtrAgtFinInstnId.setBic("DEUTDEFF");
        cdtrAgt.setFinInstnId(cdtrAgtFinInstnId);
        cdtTrfTxInf.setCdtrAgt(cdtrAgt);

        Cdtr cdtr = new Cdtr();
        cdtr.setNm("Creditor Name");
        cdtTrfTxInf.setCdtr(cdtr);

        CdtrAcct cdtrAcct = new CdtrAcct();
        Id cdtrAcctId = new Id();
        cdtrAcctId.setIban("DE89370400440532013001");
        cdtrAcct.setId(cdtrAcctId);
        cdtTrfTxInf.setCdtrAcct(cdtrAcct);

        RmtInf rmtInf = new RmtInf();
        rmtInf.setUstrd("Invoice 12345-" + (i + 1));
        cdtTrfTxInf.setRmtInf(rmtInf);
        return cdtTrfTxInf;
    }
}