package com.wecloud.erp.json;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ErpObjectMapper extends ObjectMapper
{

    public ErpObjectMapper()
    {
        setVisibility(PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY).setVisibility(PropertyAccessor.CREATOR, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY).setVisibility(PropertyAccessor.SETTER, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE).setVisibility(PropertyAccessor.GETTER, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE).setVisibility(PropertyAccessor.IS_GETTER, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private static final long serialVersionUID = 0x8f39c4332d8b73b5L;
}
