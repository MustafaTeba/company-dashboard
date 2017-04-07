package com.vaadin.example.ui;

import java.util.List;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.TickmarkPlacement;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.theme.MyTheme;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Panel;

@SpringComponent
@ViewScope
public class CompanyChart extends Panel {

	private final ListSeries chartSeries;
	private final Chart chart;

	public CompanyChart() {
		setStyleName(MyTheme.PANEL_BLUE);
		setCaption("Company Dashboard");

		chart = createChart();
		chartSeries = createSeries(chart);
	}

	public void setItems(List<CompanyData> companyData) {
		companyData.stream().map(CompanyData::getPrice).forEach(price -> chartSeries.addData(price, false, false));
		chart.drawChart();
	}

	private ListSeries createSeries(Chart chart) {
		ListSeries series = new ListSeries();
		// PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
		// series.setPlotOptions(plotOptionsArea);
		chart.getConfiguration().setSeries(series);
		return series;
	}

	private Chart createChart() {
		Chart chart = new Chart();

		Configuration conf = chart.getConfiguration();
		conf.setTitle("");

		XAxis axis = new XAxis();
		axis.setTickInterval(1);
		axis.setTickmarkPlacement(TickmarkPlacement.ON);
		axis.setGridLineWidth(1);
		Labels labels = new Labels();
		labels.setFormatter("function() {return this.value}");
		axis.setLabels(labels);
		YAxis yaxs = new YAxis();
		conf.getLegend().setEnabled(false);
		conf.addxAxis(axis);
		conf.addyAxis(yaxs);
		conf.getTooltip().setHeaderFormat("");
		conf.getTooltip().setPointFormat("{point.category}: <b>{point.y}</b>");

		axis.setCategories("Market %", "Price $", "Revenue %", "Growth %", "Product %");

		return chart;
	}
}
