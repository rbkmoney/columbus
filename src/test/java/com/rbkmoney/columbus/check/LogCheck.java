package com.rbkmoney.columbus.check;

import com.palantir.docker.compose.connection.Cluster;
import com.palantir.docker.compose.connection.waiting.ClusterHealthCheck;
import com.palantir.docker.compose.connection.waiting.SuccessOrFailure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jeckep on 27.12.16.
 */
public abstract class LogCheck implements ClusterHealthCheck {
    private boolean printLogs = true;
    private boolean ready = false;

    public LogCheck(InputStream in){
        this(in, true);
    }

    public LogCheck(InputStream in, boolean printLogs) {
        this.printLogs = printLogs;
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                while ((line = reader.readLine()) != null){
                    checkIsReady(line);
                    if(this.printLogs) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public abstract void checkIsReady(String logLine);

    protected void setReady(boolean ready){
        this.ready = ready;
    }

    @Override
    public SuccessOrFailure isClusterHealthy(Cluster cluster) {
        return SuccessOrFailure.fromBoolean(ready, "Fail");
    }
}
