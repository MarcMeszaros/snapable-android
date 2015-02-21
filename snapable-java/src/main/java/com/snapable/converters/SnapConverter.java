package com.snapable.converters;

import com.snapable.utils.SnapImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class SnapConverter implements Converter {

    Converter mWrappedConverter;

    public SnapConverter(Converter converter) {
        mWrappedConverter = converter;
    }

    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        if (body.mimeType().contains("image/jpeg") && type.getClass().isInstance(SnapImage.class)) {
            // if the output type is SnapImage, read the data and build the image object
            try {
                ByteArrayOutputStream result = new ByteArrayOutputStream(2048);
                InputStream in = body.in();
                int b;
                while ((b = in.read()) >= 0) {
                    result.write(b);
                }

                return new SnapImage(result.toByteArray());
            } catch (IOException e) {
                throw new ConversionException("Conversion failed");
            }
        } else {
            return mWrappedConverter.fromBody(body, type);
        }
    }

    @Override
    public TypedOutput toBody(Object object) {
        return mWrappedConverter.toBody(object);
    }
}
