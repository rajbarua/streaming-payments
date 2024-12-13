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

public class XMLGregorianCalendarCS implements CompactSerializer<XMLGregorianCalendar> {

    @Override
    public Class<XMLGregorianCalendar> getCompactClass() {
        return XMLGregorianCalendar.class;
    }

    @Override
    public String getTypeName() {
        return "XMLGregorianCalendar";
    }

    @Override
    public XMLGregorianCalendar read(CompactReader reader) {
        System.out.println("Reading XMLGregorianCalendar");
        OffsetDateTime date = reader.readTimestampWithTimezone(getTypeName());
        // create XMLGregorianCalendar from OffsetDateTime
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(date.toZonedDateTime());

        XMLGregorianCalendar xmlGregorianCalendar = null;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error creating XMLGregorianCalendar", e);
        }
        return xmlGregorianCalendar;
    }

    @Override
    public void write(CompactWriter writer, XMLGregorianCalendar xmlGregorianCalendar) {
        System.out.println("Writing XMLGregorianCalendar");
        Instant instant = xmlGregorianCalendar.toGregorianCalendar().toInstant();
        // Create OffsetDateTime with the desired offset (e.g., UTC)
        OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.UTC);
        writer.writeTimestampWithTimezone(getTypeName(), offsetDateTime);
    }

}
