package com.jivesoftware.v3client.framework.type;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ed.venaglia on 3/1/14.
 */
public class CoreApiDateFormat extends DateFormat {

    public static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new CoreApiDateFormat();
        }
    };

    public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_STR_ISO8601_Z ="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_STR_ISO8601_NO_MS = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String DATE_FORMAT_STR_ISO8601_Z_NO_MS ="yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final static SimpleDateFormat DATE_FORMAT_ISO8601;
    private final static SimpleDateFormat DATE_FORMAT_ISO8601_Z;
    private final static SimpleDateFormat DATE_FORMAT_ISO8601_NO_MS;
    private final static SimpleDateFormat DATE_FORMAT_ISO8601_Z_NO_MS;

    private DateFormat[] allDateFormats;

    static {
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        DATE_FORMAT_ISO8601 = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601);
        DATE_FORMAT_ISO8601.setTimeZone(gmt);

        DATE_FORMAT_ISO8601_Z = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601_Z);
        DATE_FORMAT_ISO8601_Z.setTimeZone(gmt);

        DATE_FORMAT_ISO8601_NO_MS = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601_NO_MS);
        DATE_FORMAT_ISO8601_NO_MS.setTimeZone(gmt);

        DATE_FORMAT_ISO8601_Z_NO_MS = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601_Z_NO_MS);
        DATE_FORMAT_ISO8601_Z_NO_MS.setTimeZone(gmt);
    }

    /**
     * Must clone date formats, because SimpleDateFormat is not thread safe.
     */
    private CoreApiDateFormat() {
        super();
        //set default values (not to be used) so that clone, equals do not cause null pointer exceptions
        setCalendar(Calendar.getInstance());
        setNumberFormat(NumberFormat.getInstance());
        allDateFormats = new DateFormat[4];
        allDateFormats[0]=(DateFormat)DATE_FORMAT_ISO8601.clone();
        allDateFormats[1]=(DateFormat)DATE_FORMAT_ISO8601_Z.clone();
        allDateFormats[2]=(DateFormat)DATE_FORMAT_ISO8601_NO_MS.clone();
        allDateFormats[3]=(DateFormat)DATE_FORMAT_ISO8601_Z_NO_MS.clone();
    }

    /**
     * Change timezone of every internal date format.
     *
     * @param tz
     */
    @Override
    public void setTimeZone(TimeZone tz) {
        for (DateFormat df: allDateFormats) {
            df.setTimeZone(tz);
        }
    }

    @Override
    public StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        //When formatting, always return the standard ISO8601 format
        return allDateFormats[0].format(date, stringBuffer, fieldPosition);
    }

    /**
     * Parse a string by successively trying an array of date formats. By default, uses 4 variations
     * on the ISO8601 date format to allow some leniency.
     *
     * As specified by the Java API, returns null if parse fails. The parse position is left in the correct
     * position as specified by the Java API.
     */
    @Override
    public Date parse(String s, ParsePosition parsePosition) {
        for (DateFormat dateFormat: allDateFormats) {
            Date d = dateFormat.parse(s, parsePosition);
            if (d != null) {
                return d;
            }
        }
        return null;
    }

    @Override
    public Object clone() {
        CoreApiDateFormat clone = (CoreApiDateFormat)super.clone();
        clone.allDateFormats = new DateFormat[this.allDateFormats.length];
        for (int i = 0; i < clone.allDateFormats.length; i++) {
            clone.allDateFormats[i] = (DateFormat)this.allDateFormats[i].clone();
        }
        return clone;
    }
}