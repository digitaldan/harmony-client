package com.digitaldan.harmony.messages;

import java.lang.reflect.Type;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitaldan.harmony.messages.GetCurrentActivityMessage.GetCurrentActivityResponseMessage;
import com.digitaldan.harmony.messages.StartActivityMessage.StartActivityResponseMethod;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MessageDeserializer implements JsonDeserializer<Message> {

    private final Logger logger = LoggerFactory.getLogger(MessageDeserializer.class);

    private static HashMap<String, Class<?>> responseHandlers = new HashMap<>();

    static {
        responseHandlers.put(ConfigMessage.MIME_TYPE, ConfigMessage.ConfigResponseMessage.class);
        responseHandlers.put(GetCurrentActivityMessage.MIME_TYPE, GetCurrentActivityResponseMessage.class);
        responseHandlers.put(PingMessage.MIME_TYPE, PingMessage.PingResponseMessage.class);
        responseHandlers.put(StartActivityMessage.MIME_TYPE, StartActivityResponseMethod.class);
        responseHandlers.put(DigestMessage.MIME_TYPE, DigestMessage.class);
        responseHandlers.put(ActivityFinishedMessage.MIME_TYPE, ActivityFinishedMessage.class);
    }

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement mime = jsonObject.get("cmd");

        if (mime == null) {
            mime = jsonObject.get("type");
        }

        if (mime != null) {
            Class<?> clazz = responseHandlers.get(mime.getAsString());
            if (clazz != null) {
                logger.trace("Calling fromJsonElement");
                return context.deserialize(jsonObject, clazz);
            } else {
                logger.debug("Unknown message type {}", mime);
                return null;
            }
        }

        JsonElement code = jsonObject.get("code");
        if (code != null && code.getAsInt() >= 400) {
            return context.deserialize(jsonObject, ErrorResponseMessage.class);
        }
        return null;
    }
}
