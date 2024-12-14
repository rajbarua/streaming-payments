package com.hz.demo.pmt.pain001.cs;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;
import com.hz.demo.pmt.pain001.GrpHdr;
import com.hz.demo.pmt.pain001.InitgPty;
import com.hz.demo.pmt.pain001.XMLGregorianCalendarWrapper;

public class GrpHdrCS implements CompactSerializer<GrpHdr>{

    @Override
    public Class<GrpHdr> getCompactClass() {
        return GrpHdr.class;
    }

    @Override
    public String getTypeName() {
        return "GrpHdr";
    }

    @Override
    public GrpHdr read(CompactReader reader) {
        var creDtTm = reader.readString("creDtTm");
        OffsetDateTime creDtTmDateTime = reader.readTimestampWithTimezone("creDtTmGC");
        var ctrlSum = reader.readFloat64("ctrlSum");
        InitgPty initgPty = reader.readCompact("initgPty");
        var msgId = reader.readString("msgId");
        int nbOfTxs = reader.readInt32("nbOfTxs");
        GrpHdr grpHdr = new GrpHdr();
        grpHdr.setCreDtTm(creDtTm);
        XMLGregorianCalendar creDtTmGC;
        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(creDtTmDateTime.toZonedDateTime());
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
             creDtTmGC = new XMLGregorianCalendarWrapper(xmlGregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error creating XMLGregorianCalendar", e);
        }
        grpHdr.setCreDtTmGC(creDtTmGC);
        grpHdr.setCtrlSum(ctrlSum);
        grpHdr.setInitgPty(initgPty);
        grpHdr.setMsgId(msgId);
        grpHdr.setNbOfTxs(nbOfTxs);
        return grpHdr;
    }

    @Override
    public void write(CompactWriter writer, GrpHdr grpHdr) {
        writer.writeString("creDtTm", grpHdr.getCreDtTm());
        Instant instant = grpHdr.getCreDtTmGC().toGregorianCalendar().toInstant();
        writer.writeTimestampWithTimezone("creDtTmGC", instant.atOffset(ZoneOffset.UTC));
        writer.writeFloat64("ctrlSum", grpHdr.getCtrlSum());
        writer.writeCompact("initgPty", grpHdr.getInitgPty());
        writer.writeString("msgId", grpHdr.getMsgId());
        writer.writeInt32("nbOfTxs", grpHdr.getNbOfTxs());
    }

}
