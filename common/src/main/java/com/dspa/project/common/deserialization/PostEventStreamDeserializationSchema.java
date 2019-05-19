package com.dspa.project.common.deserialization;

import com.dspa.project.model.PostEventStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class PostEventStreamDeserializationSchema implements DeserializationSchema<PostEventStream> {

    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Override
    public PostEventStream deserialize(byte[] bytes) throws IOException {
        return objectMapper.readValue(bytes, PostEventStream.class);
    }

    @Override
    public boolean isEndOfStream(PostEventStream comment) {
        return false;
    }

    @Override
    public TypeInformation<PostEventStream> getProducedType() {
        return TypeInformation.of(PostEventStream.class);
    }
}