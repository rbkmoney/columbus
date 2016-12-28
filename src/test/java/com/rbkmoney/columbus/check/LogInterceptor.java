package com.rbkmoney.columbus.check;

import com.palantir.docker.compose.execution.DockerCompose;
import com.palantir.docker.compose.logging.LogCollector;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jeckep on 27.12.16.
 */
public class LogInterceptor implements LogCollector {
    private static final long STOP_TIMEOUT_IN_MILLIS = 50;
    private ExecutorService executor = null;
    private PipedInputStream in;
    private PipedOutputStream out;

    public LogInterceptor(){
        in = new PipedInputStream();
        try {
            out = new PipedOutputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startCollecting(DockerCompose dockerCompose) throws IOException, InterruptedException {
        if (executor != null) {
            throw new RuntimeException("Cannot start collecting the same logs twice");
        }

        List<String> serviceNames = dockerCompose.services();
        if (serviceNames.size() == 0) {
            return;
        }
        executor = Executors.newFixedThreadPool(serviceNames.size());
        serviceNames.stream().forEachOrdered(service -> this.collectLogs(service, dockerCompose));

    }
    private void collectLogs(String container, DockerCompose dockerCompose)  {
        executor.submit(() -> {
            try {
                dockerCompose.writeLogs(container, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stopCollecting() throws InterruptedException {
        if (executor == null) {
            return;
        }
        if (!executor.awaitTermination(STOP_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }

    public PipedInputStream getIn() {
        return in;
    }
}
