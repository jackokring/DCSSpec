package uk.co.kring.android.dcs.jndsk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//============================ A BLANK TRANSFORM
public class FileProcessor {

    //======================== PUBLIC INTERFACE
    public String newName(String oldName, String ext) {
        return UUID.randomUUID().toString() + ext;
    }

    public void background(InputStream is, OutputStream os)
            throws IOException {
        int i;
        while((i = is.read()) != -1) {
            os.write(i);//copy
        }
    }

    public void headers(InputStream is, OutputStream os, String oldName)
        /* throws IOException */ {

    }
}
