package com.vaadin.example.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointSelectEvent;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
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
	private DataSeries series;
	private Optional<Registration> pointSelectListenerRegistration = Optional.empty();
	private Optional<CompanyData> oldSelection = Optional.empty();

	public CompanyChart(UIEventBus eventBus) {
		this.eventBus = eventBus;
		setStyleName(MyTheme.PANEL_BLUE);
		setCaption("Company Dashboard");

		chart = createChart();
		registerToSelectionEvent();
		chart.setSizeFull();
		setContent(chart);
	}

	public void setItems(List<CompanyData> items) {
		this.items = items;
		this.series = new DataSeries();
		items.stream().map(data -> {
			DataSeriesItem item = new DataSeriesItem(data.getName(), data.getPrice());
			item.setDataLabels(createLabel(data));
			return item;
		}).forEach(series::add);
		chart.getConfiguration().setSeries(series);
	}

	public void selectAndRefresh(CompanyData item) {
		oldSelection.ifPresent(this::deselect);
		if (item != null) {
			refresh(item);
			int index = items.indexOf(item);
			if (index != -1) {
				oldSelection = Optional.of(item);
				DataSeriesItem updated = series.get(index);
				updated.setSelected(true);
				series.update(updated);
			}
		}
	}

	private void deselect(CompanyData item) {
		int index = items.indexOf(item);
		if (index != -1) {
			DataSeriesItem updated = series.get(index);
			updated.setSelected(false);
			series.update(updated);
		}
	}

	public void refresh(CompanyData item) {
		// We are handling the data source a bit manually here, so the update
		// code isn't that beautiful
		if (item != null) {
			int index = items.indexOf(item);
			items.add(index, item);
			items.remove(index + 1);
			DataSeriesItem updated = series.get(index);
			updated.setY(item.getPrice());
			updated.setName(item.getName());
			series.update(updated);
		}
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
		eventBus.publish(this, new Events.ChartSelectEvent(items.get(event.getPointIndex())));
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
