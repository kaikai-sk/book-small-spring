package com.springframework.core.convert.support;

import com.springframework.core.convert.converter.ConverterRegistry;

public class DefaultConversionService extends GenericConversionService {
    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
    }

    public DefaultConversionService() {
        addDefaultConverters(this);
    }


}
