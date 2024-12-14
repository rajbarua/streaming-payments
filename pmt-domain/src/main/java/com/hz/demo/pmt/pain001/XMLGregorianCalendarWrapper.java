package com.hz.demo.pmt.pain001;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

public class XMLGregorianCalendarWrapper extends XMLGregorianCalendar implements Serializable{
    private XMLGregorianCalendar xmlGregorianCalendarInner = null;
    public XMLGregorianCalendarWrapper(XMLGregorianCalendar xmlGregorianCalendar) {
        super();
        this.xmlGregorianCalendarInner = xmlGregorianCalendar;
    }
    @Override
    public void clear() {
        xmlGregorianCalendarInner.clear();
    }
    @Override
    public void reset() {
        xmlGregorianCalendarInner.reset();
    }
    @Override
    public void setYear(BigInteger year) {
        xmlGregorianCalendarInner.setYear(year);
    }
    @Override
    public void setYear(int year) {
        xmlGregorianCalendarInner.setYear(year);
    }
    @Override
    public void setMonth(int month) {
        xmlGregorianCalendarInner.setMonth(month);
    }
    @Override
    public void setDay(int day) {
        xmlGregorianCalendarInner.setDay(day);
    }
    @Override
    public void setTimezone(int offset) {
        xmlGregorianCalendarInner.setTimezone(offset);
    }
    @Override
    public void setHour(int hour) {
        xmlGregorianCalendarInner.setHour(hour);
    }
    @Override
    public void setMinute(int minute) {
        xmlGregorianCalendarInner.setMinute(minute);
    }
    @Override
    public void setSecond(int second) {
        xmlGregorianCalendarInner.setSecond(second);
    }
    @Override
    public void setMillisecond(int millisecond) {
        xmlGregorianCalendarInner.setMillisecond(millisecond);
    }
    @Override
    public void setFractionalSecond(BigDecimal fractional) {
        xmlGregorianCalendarInner.setFractionalSecond(fractional);
    }
    @Override
    public BigInteger getEon() {
        return xmlGregorianCalendarInner.getEon();
    }
    @Override
    public int getYear() {
        return xmlGregorianCalendarInner.getYear();
    }
    @Override
    public BigInteger getEonAndYear() {
        return xmlGregorianCalendarInner.getEonAndYear();
    }
    @Override
    public int getMonth() {
        return xmlGregorianCalendarInner.getMonth();
    }
    @Override
    public int getDay() {
        return xmlGregorianCalendarInner.getDay();
    }
    @Override
    public int getTimezone() {
        return xmlGregorianCalendarInner.getTimezone();
    }
    @Override
    public int getHour() {
        return xmlGregorianCalendarInner.getHour();
    }
    @Override
    public int getMinute() {
        return xmlGregorianCalendarInner.getMinute();
    }
    @Override
    public int getSecond() {
        return xmlGregorianCalendarInner.getSecond();
    }
    @Override
    public BigDecimal getFractionalSecond() {
        return xmlGregorianCalendarInner.getFractionalSecond();
    }
    @Override
    public int compare(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendarInner.compare(xmlGregorianCalendar);
    }
    @Override
    public XMLGregorianCalendar normalize() {
        return xmlGregorianCalendarInner.normalize();
    }
    @Override
    public String toXMLFormat() {
        return xmlGregorianCalendarInner.toXMLFormat();
    }
    @Override
    public QName getXMLSchemaType() {
        return xmlGregorianCalendarInner.getXMLSchemaType();
    }
    @Override
    public boolean isValid() {
        return xmlGregorianCalendarInner.isValid();
    }
    @Override
    public void add(Duration duration) {
        xmlGregorianCalendarInner.add(duration);
    }
    @Override
    public GregorianCalendar toGregorianCalendar() {
        return xmlGregorianCalendarInner.toGregorianCalendar();
    }
    @Override
    public GregorianCalendar toGregorianCalendar(TimeZone timezone, Locale aLocale, XMLGregorianCalendar defaults) {
        return xmlGregorianCalendarInner.toGregorianCalendar(timezone, aLocale, defaults);
    }
    @Override
    public TimeZone getTimeZone(int defaultZoneoffset) {
        return xmlGregorianCalendarInner.getTimeZone(defaultZoneoffset);
    }
    @Override
    public Object clone() {
        return xmlGregorianCalendarInner.clone();
    }
    

}
