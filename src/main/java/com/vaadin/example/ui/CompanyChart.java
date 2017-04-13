package com.vaadin.example.ui;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointSelectEvent;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.DataProviderSeries;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.theme.MyTheme;
import com.vaadin.shared.Registration;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Panel;

@SpringComponent
@ViewScope
public class CompanyChart extends Panel {

	private final Chart chart;
	private final UIEventBus eventBus;
	private List<CompanyData> items;
	private DataProviderSeries<CompanyData> series;
	private Optional<Registration> pointSelectListenerRegistration = Optional.empty();
	private Optional<CompanyData> selection = Optional.empty();

	public CompanyChart(UIEventBus eventBus) {
		this.eventBus = eventBus;
		setStyleName(MyTheme.PANEL_CAPTION_DARK);
		setCaption("Company Dashboard");

		chart = createChart();
		registerToSelectionEvent();
		chart.setSizeFull();
		setContent(chart);
	}

	public void setItems(List<CompanyData> items) {
		this.items = items;
		series = new DataProviderSeries<>(new ListDataProvider<CompanyData>(items));
		series.setY(CompanyData::getPrice);
		series.setPointName(CompanyData::getName);
		series.setProperty("dataLabels", this::createLabel);
		series.setProperty("selected", this::checkSelectStatus);
		chart.getConfiguration().setSeries(series);
	}

	private Boolean checkSelectStatus(CompanyData item) {
		return item.equals(selection.orElse(null));
	}

	public void refresh(CompanyData item) {
		if (item != null) {
			Collections.replaceAll(items, item, item);
			series.getDataProvider().refreshItem(item);
		}
	}

	public void select(CompanyData item) {
		selection.ifPresent(this::deselect);
		if (item != null) {
			selection = Optional.of(item);
			series.getDataProvider().refreshItem(item);
		}
	}

	private void deselect(CompanyData item) {
		selection = Optional.empty();
		series.getDataProvider().refreshItem(item);
	}

	private Chart createChart() {
		Chart chart = new Chart(ChartType.COLUMN);

		Configuration conf = chart.getConfiguration();
		conf.setTitle("");

		XAxis xaxis = new XAxis();
		Labels labels = new Labels();
		labels.setRotation(-45);
		labels.setAlign(HorizontalAlign.RIGHT);
		Style style = new Style();
		style.setFontSize("13px");
		style.setFontFamily("Verdana, sans-serif");
		labels.setStyle(style);
		xaxis.setLabels(labels);
		xaxis.setType(AxisType.CATEGORY);

		YAxis yaxs = new YAxis();
		conf.getLegend().setEnabled(false);
		conf.addxAxis(xaxis);
		conf.addyAxis(yaxs);
		conf.getTooltip().setHeaderFormat("");
		conf.getTooltip().setPointFormat("{point.name}: <b>${point.y}</b>");

		PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
		plotOptionsColumn.setTurboThreshold(0);
		plotOptionsColumn.setAllowPointSelect(true);
		conf.setPlotOptions(plotOptionsColumn);

		return chart;
	}

	private void onPointSelect(PointSelectEvent event) {
		selection = Optional.of(items.get(event.getPointIndex()));
		eventBus.publish(this, new Events.ChartSelectEvent(selection.get()));
	}

	private DataLabels createLabel(CompanyData data) {
		DataLabels dataLabels = new DataLabels();
		dataLabels.setEnabled(true);
		dataLabels.setRotation(-90);

		dataLabels.setAlign(HorizontalAlign.RIGHT);
		if (data.getPrice().compareTo(BigDecimal.valueOf(45.0)) < 0) {
			dataLabels.setColor(new SolidColor(0, 0, 0));
			dataLabels.setY(-10);
			dataLabels.setAlign(HorizontalAlign.LEFT);
		} else {
			dataLabels.setColor(new SolidColor(255, 255, 255));
			dataLabels.setY(10);
			dataLabels.setAlign(HorizontalAlign.RIGHT);
		}

		dataLabels.setFormat("{point.y}");
		return dataLabels;
	}

	private void unregisterFromSelectionEvent() {
		pointSelectListenerRegistration.ifPresent(Registration::remove);
		pointSelectListenerRegistration = Optional.empty();
	}

	private void registerToSelectionEvent() {
		unregisterFromSelectionEvent();
		pointSelectListenerRegistration = Optional.of(chart.addPointSelectListener(this::onPointSelect));
	}

}
