package com.snapable.api;

import com.google.gson.Gson;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class SnapConverter extends GsonConverter {

    public SnapConverter(Gson gson) {
        super(gson);
    }

    public SnapConverter(Gson gson, String encoding) {
        super(gson, encoding);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        if (type.getClass().isInstance(SnapImage.class)) {
            // if the output type is SnapImage, read the data and build the image object
            try {
                ByteArrayOutputStream result = new ByteArrayOutputStream(2048);
                InputStream in = body.in();
                int b;
                while ((b = in.read()) >= 0 ) {
                    result.write(b);
                }

                return new SnapImage(result.toByteArray());
            } catch (IOException e) {
                throw new ConversionException("Conversion failed");
            }
        } else {

            return super.fromBody(body, type);
        }
    }
}
