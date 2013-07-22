package com.googlecode.utterlyidle.jobs.schedule;

import com.googlecode.totallylazy.Value;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.Days;

import java.util.Calendar;
import java.util.Date;

import static com.googlecode.totallylazy.time.Dates.stripTime;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class NextTime implements Value<Date> {
    private Clock clock;
    private Date nextTime;

    private NextTime(String time, Clock clock) {
        this.clock = clock;
        this.nextTime = calcualteNext(time);
    }

    public static NextTime nextTime(String time, Clock clock) { return new NextTime(time, clock); }

    @Override
    public Date value() { return nextTime; }

    private Date calcualteNext(String time) {
        int hours = parseInt(time.substring(0, 2));
        int minutes = parseInt(time.substring(2, 4));
        Date now = clock.now();
        Calendar calendar = Dates.calendar(stripTime(now));
        calendar.set(HOUR_OF_DAY, hours);
        calendar.set(MINUTE, minutes);
        Date todayTime = calendar.getTime();
        return todayTime.before(now) ? Days.add(todayTime, 1) : todayTime;
    }
}
