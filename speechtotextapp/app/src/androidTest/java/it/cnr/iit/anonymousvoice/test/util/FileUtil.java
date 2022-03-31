package it.cnr.iit.anonymousvoice.test.util;


import android.app.Instrumentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static InputStream getResourceStream(Instrumentation instrumentation, int resourceId){
        return instrumentation
                .getContext()
                .getResources()
                .openRawResource(resourceId);
    }

    public static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[512];
        int len;
        while ((len = from.read(buf)) != -1) {
            to.write(buf, 0, len);
        }
    }

    public static void removeFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }
}
