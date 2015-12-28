package com.github.p4535992.util.database.jooq.spring.common.service;

import org.joda.time.LocalDateTime;

import java.sql.Timestamp;

/**
 * @author Petri Kainulainen
 */
public interface DateTimeService {

    public LocalDateTime getCurrentDateTime();

    public Timestamp getCurrentTimestamp();
}
