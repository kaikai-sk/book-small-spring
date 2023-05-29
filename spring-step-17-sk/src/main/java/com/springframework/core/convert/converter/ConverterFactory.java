package com.springframework.core.convert.converter;

public interface ConverterFactory<S, R> {
    /**
     * get the converter to convert from S to T, where T is also an instance of R.
     *
     * @param targetType targetType the target type to convert to.
     * @return a converter from S to T.
     * @param <T> the target type.
     */
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
