package eu.luminis.debugging.report.model;

import java.time.LocalDateTime;
import java.util.List;

public record ObservationEvent(
        List<Observation> observations,
        LocalDateTime date
) {
}