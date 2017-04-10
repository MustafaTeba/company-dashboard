package com.vaadin.example.ui;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.addon.charts.Chart;
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
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Panel;

@SpringComponent
@ViewScope
public class CompanyChart extends Panel {

	private final DataSeries chartSeries;
	private final Chart chart;

	public CompanyChart() {
		setStyleName(MyTheme.PANEL_BLUE);
		setCaption("Company Dashboard");

		chart = createChart();
		chart.setSizeFull();
		chartSeries = createSeries(chart);
		setContent(chart);
	}

	public void setItems(List<CompanyData> companyData) {
		companyData.stream().map(this::dataSeriesItemOf)
				.forEach(item -> chartSeries.add(item, false, false));
		chart.drawChart();
	}

	private DataSeries createSeries(Chart chart) {
		DataSeries series = new DataSeries();
		chart.getConfiguration().setSeries(series);
		return series;
	}

	private DataSeriesItem dataSeriesItemOf(CompanyData data) {
		DataSeriesItem item = new DataSeriesItem(data.getName(), data.getPrice());
		if (data.getPrice().compareTo(BigDecimal.valueOf(45.0)) < 0) {
			item.setDataLabels(createLabel(LabelPosition.POSITION_ABOVE));
		}
		return item;
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
		conf.getTooltip().setPointFormat("{point.category}: <b>{point.y}</b>");

		PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
		plotOptionsColumn.setDataLabels(createLabel(LabelPosition.POSITION_BELOW));
		conf.setPlotOptions(plotOptionsColumn);

		return chart;
	}

	private DataLabels createLabel(LabelPosition position) {
		DataLabels dataLabels = new DataLabels();
		dataLabels.setEnabled(true);
		dataLabels.setRotation(-90);

		dataLabels.setAlign(HorizontalAlign.RIGHT);
		if (position == LabelPosition.POSITION_ABOVE) {
			dataLabels.setColor(new SolidColor(0, 0, 0));
			dataLabels.setY(-10);
			dataLabels.setAlign(HorizontalAlign.LEFT);
		} else {
			dataLabels.setColor(new SolidColor(255, 255, 255));
			dataLabels.setY(10);
			dataLabels.setAlign(HorizontalAlign.RIGHT);
		}

		dataLabels.setFormatter("this.y");
		return dataLabels;
	}
	
	private enum LabelPosition {
		POSITION_ABOVE,
		POSITION_BELOW
	}

}
