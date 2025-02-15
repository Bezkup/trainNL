package org.bezkup.application;

public record TrainInfo (String direction, String track, String routeStations, String dateDeparture, Integer delay, boolean cancelled, String departureStatus) {
}
