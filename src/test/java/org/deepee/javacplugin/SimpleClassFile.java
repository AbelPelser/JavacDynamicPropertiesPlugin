package org.deepee.javacplugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

public class SimpleClassFile extends SimpleJavaFileObject {

    private ByteArrayOutputStream out;

    public SimpleClassFile(URI uri) {
        super(uri, Kind.CLASS);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return out = new ByteArrayOutputStream();
    }

    public byte[] getCompiledBinaries() {
        return out.toByteArray();
    }
}