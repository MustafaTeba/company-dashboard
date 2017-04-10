package com.vaadin.example.ui;

import java.math.BigDecimal;

import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.Background;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.Pane;
import com.vaadin.addon.charts.model.PlotOptionsArea;
import com.vaadin.addon.charts.model.TickmarkPlacement;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.validator.BigDecimalRangeValidator;
import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.theme.MyTheme;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@ViewScope
public class CompanyDetailsEditor extends Panel {

	// Naming fields here with the same name as the get/set property methods in
	// the CompanyData entity allows automatic binding. Otherwise either get and
	// set methods would need to be defined in binding or @PropertyId annotation
	// would be needed.
	private final TextField name;
	private final TextField price;
	private final TextField revenuePct;
	private final TextField growthPct;
	private final TextField productPct;
	private final TextField marketPct;
	private final Binder<CompanyData> binder;
	private final ListSeries chartSeries;
	private final VerticalLayout content;
	private final Chart chart;

	private static final String NUMBER_FIELD_ERROR_MESSAGE = "Please provide a value between 0 and 100";

	public CompanyDetailsEditor(UIEventBus eventBus) {
		// Defined in mytheme.scss
		setStyleName(MyTheme.PANEL_BLUE);
		setCaption("Company Details");
		
		// Create polar chart
		chart = createChart();
		chartSeries = createSeries(chart);

		// Create form layout to edit company details
		FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);

		// Create form fields and add them to form layout
		// Because we are saving values to database every time when field value
		// is changed, let's change the change event to occur when focus is lost
		// from the field.
		name = new TextField("Name:");
		name.setReadOnly(true);
		price = new TextField("Price:");
		price.setValueChangeMode(ValueChangeMode.BLUR);
		revenuePct = new TextField("Revenue %:");
		revenuePct.setValueChangeMode(ValueChangeMode.BLUR);
		growthPct = new TextField("Growth %:");
		growthPct.setValueChangeMode(ValueChangeMode.BLUR);
		productPct = new TextField("Product %:");
		productPct.setValueChangeMode(ValueChangeMode.BLUR);
		marketPct = new TextField("Market %:");
		marketPct.setValueChangeMode(ValueChangeMode.BLUR);
		formLayout.addComponents(name, price, revenuePct, growthPct, productPct, marketPct);

		// Create editor content layout
		content = new VerticalLayout();
		content.setMargin(false);
		content.addComponents(chart, formLayout);
		content.setComponentAlignment(chart, Alignment.TOP_CENTER);
		setContent(content);

		binder = new Binder<>(CompanyData.class);

		// Create validator and converter
		BigDecimalRangeValidator rangeValidator = new BigDecimalRangeValidator(NUMBER_FIELD_ERROR_MESSAGE,
				BigDecimal.ZERO, BigDecimal.valueOf(100));
		StringToBigDecimalConverter numberConverter = new StringToBigDecimalConverter(NUMBER_FIELD_ERROR_MESSAGE);

		// Bind with setter as null to make it read-only
		binder.forMemberField(price).withConverter(numberConverter).withValidator(
				new BigDecimalRangeValidator("Please provide non-negative number", BigDecimal.ZERO, null));
		binder.forMemberField(revenuePct).withConverter(numberConverter).withValidator(rangeValidator);
		binder.forMemberField(growthPct).withConverter(numberConverter).withValidator(rangeValidator);
		binder.forMemberField(productPct).withConverter(numberConverter).withValidator(rangeValidator);
		binder.forMemberField(marketPct).withConverter(numberConverter).withValidator(rangeValidator);

		binder.bindInstanceFields(this);
		binder.addValueChangeListener(event -> {
			if (event.isUserOriginated()) {
				drawChart(binder.getBean());
				eventBus.publish(this, new EditEvent(binder.getBean()));
			}
		});
	}

	public void setEditedItem(CompanyData item) {
		binder.setBean(item);
		drawChart(item);
	}

	public void clearEditor() {
		setEditedItem(null);
	}

	private Chart createChart() {
		Chart chart = new Chart();
		chart.setHeight("300px");

		Configuration conf = chart.getConfiguration();
		conf.setTitle("");
		conf.getChart().setPolar(true);

		Pane pane = new Pane(0, 360);
		conf.addPane(pane);
		pane.setBackground(new Background[] {});

		XAxis xaxis = new XAxis();
		xaxis.setTickInterval(1);
		xaxis.setMin(0);
		xaxis.setMax(5);
		xaxis.setTickmarkPlacement(TickmarkPlacement.ON);
		xaxis.setGridLineWidth(1);

		Labels labels = new Labels();
		labels.setFormatter("function() {return this.value}");
		xaxis.setLabels(labels);

		YAxis yaxis = new YAxis();
		yaxis.setMin(0);
		yaxis.setMax(100);
		yaxis.setTickInterval(20);
		conf.getLegend().setEnabled(false);
		conf.addxAxis(xaxis);
		conf.addyAxis(yaxis);
		conf.getTooltip().setHeaderFormat("");
		conf.getTooltip().setPointFormat("{point.category}: <b>{point.y}</b>");

		// Instead of using numerical x values, let's use category strings
		xaxis.setCategories("Market %", "Price $", "Revenue %", "Growth %", "Product %");

		return chart;
	}

	private ListSeries createSeries(Chart chart) {
		ListSeries series = new ListSeries();
		PlotOptionsArea plotOptionsArea = new PlotOptionsArea();
		series.setPlotOptions(plotOptionsArea);
		chart.getConfiguration().setSeries(series);
		return series;
	}

	private void drawChart(CompanyData item) {
		chartSeries.setData(item.getMarketPct(), item.getPrice(), item.getRevenuePct(), item.getGrowthPct(),
				item.getProductPct());
		chart.drawChart();
	}

	public static class EditEvent extends CustomEvent<CompanyData> {
		public EditEvent(CompanyData data) {
			super(data);
		}
	}
}
