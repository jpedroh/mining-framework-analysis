/*
 * Copyright (c) 2013-2016.  Urban Airship and Contributors
 */

package com.urbanairship.api.push.parse.notification.ios;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.urbanairship.api.push.model.notification.ios.IOSBadgeData;

import java.io.IOException;

public class IOSBadgeDataSerializer extends JsonSerializer<IOSBadgeData> {
    @Override
    public void serialize(IOSBadgeData badge, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        switch (badge.getType()) {
          case VALUE:
              jgen.writeNumber(badge.getValue().get());
              break;
          case AUTO:
              jgen.writeString("auto");
              break;
          case INCREMENT:
              jgen.writeString("+" + badge.getValue().get().toString());
              break;
          case DECREMENT:
              jgen.writeString("-" + badge.getValue().get().toString());
              break;
        }
    }
}
