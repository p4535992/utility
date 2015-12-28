package com.github.p4535992.util.database.jooq.spring.common.json;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.LocalDateTime;

/**
 * @author Petri Kainulainen
 */
public class JodaModule extends SimpleModule {
    
    private static final long serialVersionUID = 123758346L;

    public JodaModule() {
        super(PackageVersion.VERSION);

        addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
    }
}
