package com.rbkmoney.columbus.check;

import java.io.InputStream;

/**
 * Created by jeckep on 27.12.16.
 */
public class PostgresIsReadyCheck extends LogCheck {
    public static final String pattern = "database system is ready to accept connections";
    private  int count = 0;
    public PostgresIsReadyCheck(InputStream in) {
        super(in);
    }

    public PostgresIsReadyCheck(InputStream in, boolean printLogs) {
        super(in, printLogs);
    }

    @Override
    public void checkIsReady(String logLine) {
        if(logLine.contains(pattern)){
            count++;
            if(count >= 2){
                setReady(true);
            }
        }
    }
}
