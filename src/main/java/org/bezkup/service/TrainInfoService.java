package org.bezkup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.bezkup.application.TrainInfo;
import org.bezkup.ns.RouteStation;
import org.bezkup.ns.TrainDeparture;
import org.bezkup.webtarget.WebTargetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainInfoService {

    private final Logger logger = LoggerFactory.getLogger(TrainInfoService.class);
    private final String baseUrl;
    private final String subscriptionKey;
    private final String stationUicCode;
    private final String uriPath;
    private final WebTargetProvider webTargetProvider;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public TrainInfoService(@Value("${baseUrl}") String baseUrl,
                            @Value("${subscriptionKey}") String subscriptionKey,
                            @Value("${stationUicCode}") String stationUicCode,
                            WebTargetProvider webTargetProvider,
                            @Value("${uriPath}") String uriPath
    ) {
        this.baseUrl = baseUrl;
        this.subscriptionKey = subscriptionKey;
        this.webTargetProvider = webTargetProvider;
        this.stationUicCode = stationUicCode;
        this.uriPath = uriPath;
    }

    public List<TrainInfo> getTrainInfo() {
        try (Response response = webTargetProvider.getWebTarget(getUri(baseUrl)).request()
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .buildGet()
                .invoke()) {
            if (response.getStatus() != 200) {
                return new ArrayList<>();
            }

            try {
                JsonNode rootNode = objectMapper.readTree(response.readEntity(String.class));
                JsonNode departuresNode = rootNode.path("payload").path("departures");
                List<TrainDeparture> trainDepartures = objectMapper.readValue(departuresNode.toString(), new TypeReference<>() {
                });
                return trainDeparturToTrainInfo(trainDepartures);
            } catch (JsonProcessingException e) {
                logger.error("Error while processing response", e);
                return new ArrayList<>();
            }
        }
    }

    private List<TrainInfo> trainDeparturToTrainInfo(List<TrainDeparture> trainDepartures) {
        return trainDepartures.stream()
                .map(trainDeparture -> new TrainInfo(
                        trainDeparture.direction(),
                        trainDeparture.actualTrack(),
                        getFormattedRouteStations(trainDeparture.routeStations()),
                        getFormattedDate(trainDeparture),
                        (int) trainDeparture.plannedDateTime().until(trainDeparture.actualDateTime(), ChronoUnit.MINUTES),
                        trainDeparture.cancelled(),
                        trainDeparture.departureStatus()
                ))
                .collect(Collectors.toList());
    }

    private URI getUri(String baseUrl) {
        return UriBuilder
                .fromUri(baseUrl)
                .path(uriPath)
                .queryParam("uicCode", stationUicCode)
                .build();
    }

    private String getFormattedRouteStations(List<RouteStation> routeStations) {
        return routeStations.stream()
                .map(RouteStation::mediumName)
                .collect(Collectors.joining(", "));
    }

    public String getFormattedDate(TrainDeparture trainDeparture) {
        var plannedDateTime = trainDeparture.plannedDateTime();
        var hour = plannedDateTime.getHour() + "";
        var minutes = plannedDateTime.getMinute() + "";
        if (plannedDateTime.getHour() < 10) {
            hour = "0" + plannedDateTime.getHour();
        }
        if (plannedDateTime.getMinute() < 10) {
            minutes = "0" + plannedDateTime.getMinute();
        }

        return hour + ":" + minutes;
    }


}