package com.hz.demo.pmt.pain001.cs;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;
import com.hz.demo.pmt.pain001.XMLGregorianCalendarWrapper;

public class XMLGregorianCalendarWrapperCS implements CompactSerializer<XMLGregorianCalendarWrapper> {

    @Override
    public Class<XMLGregorianCalendarWrapper> getCompactClass() {
        return XMLGregorianCalendarWrapper.class;
    }

    @Override
    public String getTypeName() {
        return "XMLGregorianCalendarWrapper";
    }

    @Override
    public XMLGregorianCalendarWrapper read(CompactReader reader) {
        System.out.println("Reading XMLGregorianCalendar");
        OffsetDateTime date = reader.readTimestampWithTimezone(getTypeName());
        // create XMLGregorianCalendar from OffsetDateTime
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(date.toZonedDateTime());

        XMLGregorianCalendarWrapper xmlGregorianCalendarWrpr;
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            xmlGregorianCalendarWrpr = new XMLGregorianCalendarWrapper(xmlGregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error creating XMLGregorianCalendar", e);
        }
        return xmlGregorianCalendarWrpr;
    }

    @Override
    public void write(CompactWriter writer, XMLGregorianCalendarWrapper xmlGregorianCalendar) {
        System.out.println("Writing XMLGregorianCalendar");
        Instant instant = xmlGregorianCalendar.toGregorianCalendar().toInstant();
        // Create OffsetDateTime with the desired offset (e.g., UTC)
        OffsetDateTime offsetDateTime = instant.atZone(ZoneId.of("Asia/Kolkata")).toOffsetDateTime();
        writer.writeTimestampWithTimezone(getTypeName(), offsetDateTime);
    }

}
