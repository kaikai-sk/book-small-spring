package com.springframework.core.convert.converter;

public interface Converter<S, T> {
    /**
     * convert the source object of type {@code S} to target type {@code T}
     *
     * @param source
     * @return
     */
    T convert(S source);
}
