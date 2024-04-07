package eu.luminis.debugging.report.util;

import eu.luminis.debugging.report.model.Observation;
import eu.luminis.debugging.report.model.ObservationEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonTest {
    ObservationEvent sampleEvent = new ObservationEvent(
            List.of(
                    new Observation("Bla", 10, 50)
            ),
            LocalDateTime.of(2024, 4, 8, 13, 54, 32)
    );

    @Test
    void format() {

        String json = Json.format(sampleEvent);

        assertEquals("{\"observations\":[{\"station\":\"Bla\",\"temperature\":10.0,\"humidity\":50.0}],\"date\":[2024,4,8,13,54,32]}", json);
    }

    @Test
    void parse() {
        String json = "{\"observations\":[{\"station\":\"Bla\",\"temperature\":10.0,\"humidity\":50.0}],\"date\":[2024,4,8,13,54,32]}";

        ObservationEvent event = Json.parse(json, ObservationEvent.class);

        assertEquals(sampleEvent, event);
    }
}