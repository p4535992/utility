package com.github.p4535992.util.database.jooq.spring.config;

import org.jooq.Transaction;
import org.springframework.transaction.TransactionStatus;

/**
 * Created by 4535992 on 01/01/2016.
 */
class SpringTransaction implements Transaction {
    final TransactionStatus tx;

    SpringTransaction(TransactionStatus tx) {
        this.tx = tx;
    }
}