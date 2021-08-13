package util;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamRequestBody extends RequestBody {

    private final InputStream inputStream;
    private final MediaType contentType;

    private InputStreamRequestBody(InputStream inputStream, MediaType contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    public static RequestBody create(InputStream inputStream, MediaType contentType) {
        return new InputStreamRequestBody(inputStream, contentType);
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public boolean isOneShot() {
        return true;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try (Source source = Okio.source(inputStream)) {
            sink.writeAll(source);
        }
    }
}
