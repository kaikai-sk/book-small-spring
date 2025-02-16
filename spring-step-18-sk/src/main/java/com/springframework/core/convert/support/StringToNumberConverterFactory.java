package com.springframework.core.convert.support;

import com.springframework.util.NumberUtils;
import com.springframework.core.convert.converter.Converter;
import com.springframework.core.convert.converter.ConverterFactory;
import com.sun.istack.internal.Nullable;

/**
 * 作者：DerekYRC https://github.com/DerekYRC/mini-spring
 *
 * @description Converts from a String any JDK-standard Number implementation.
 * @date 2022/3/16
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumber<>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            if (source.isEmpty()) {
                return null;
            }
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }

}
