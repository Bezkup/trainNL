package org.bezkup.ns;

import java.time.OffsetDateTime;
import java.util.List;

public record TrainDeparture(String direction,
                             String name,
                             OffsetDateTime plannedDateTime,
                             Integer plannedTimeZoneOffset,
                             OffsetDateTime actualDateTime,
                             String plannedTrack,
                             String actualTrack,
                             List<RouteStation> routeStations,
                             String departureStatus,
                             boolean cancelled)
{

}

