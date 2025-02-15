package org.bezkup.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.bezkup.application.TrainInfo;
import org.bezkup.service.TrainInfoService;
import org.bezkup.ns.TrainDeparture;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SpringComponent
@UIScope
@PageTitle("Train list")
@Route("")
@Uses(Icon.class)
public class TrainView extends Composite<VerticalLayout> {

    private final TrainInfoService trainInfoService;

    public TrainView(TrainInfoService trainInfoService) {
        this.trainInfoService = trainInfoService;
        H1 header = new H1("Train list");
        getContent().setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getContent().add(header);
        Grid<TrainInfo> grid = new Grid<>();
        final var trainInfos = trainInfoService.getTrainInfo();
        grid.setItems(trainInfos);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(createStatusRenderer()).setHeader("STATUS");
        grid.addColumn(createDirectionRenderer()).setHeader("DIRECTION");
        grid.addColumn(createPlannedDateTimeRenderer()).setHeader("DATE TIME");
        grid.addColumn(createActualTrack()).setHeader("TRACK");
        getContent().add(grid);
    }

    private ComponentRenderer<Span, TrainInfo> createActualTrack() {
        return new ComponentRenderer<>(Span::new,
                (span, trainInfo) -> { span.setText(trainInfo.track());});
    }

    private ComponentRenderer<Span, TrainInfo> createStatusRenderer() {
        return new ComponentRenderer<>(Span::new,
                (span, trainInfo) -> { span.setText(trainInfo.departureStatus()); });
    }

    private Renderer<TrainInfo> createDirectionRenderer() {
        return new ComponentRenderer<>(trainInfo -> {
            var direction = new Span(trainInfo.direction());
            var routeStations = new Span(" via " + trainInfo.routeStations());

            var lines = new VerticalLayout(direction, routeStations);
            lines.setAlignItems(FlexComponent.Alignment.START);
            lines.setPadding(false);
            lines.setSpacing(false);

            return lines;
        });
    }


    private static ComponentRenderer<Span, TrainInfo> createPlannedDateTimeRenderer() {
        return new ComponentRenderer<>(Span::new,
                (span, trainInfo) -> {
            var timeDeparture = trainInfo.dateDeparture();
            if (trainInfo.delay() > 0) {
                timeDeparture += " +" + trainInfo.delay();
            }
            span.setText(timeDeparture);
        });
    }

}