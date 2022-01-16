package com.mobzheng.trace.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ToLogFile implements DataProcess<DataProcess.Operate> {


    private FileWriter fileWriter;

    {
        String pattern = System.getProperty("data.path", System.getProperty("user.dir") + "/traceData/");
        File file = new File(pattern);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fileWriter = new FileWriter(new File(file.getPath(),"agent.log"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Operate accept(Object o) {
        try {
            fileWriter.write(o + "\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Operate.OVER;
    }
}
