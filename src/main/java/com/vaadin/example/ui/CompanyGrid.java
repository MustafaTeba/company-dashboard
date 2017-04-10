package com.vaadin.example.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.theme.MyTheme;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;

@SpringComponent
@ViewScope
public class CompanyGrid extends Panel {

	private final Grid<CompanyData> grid = new Grid<>();
	private final EventBus.UIEventBus eventBus;

	@Autowired
	public CompanyGrid(EventBus.UIEventBus eventBus) {
		setCaption("Company Data");
		setStyleName(MyTheme.PANEL_BLUE);
		setContent(grid);
		grid.setSizeFull();
		// Configure columns to set the display order, custom caption and to
		// hide showing entity version and id.
		grid.addColumn(CompanyData::getName).setCaption("Name");
		grid.addColumn(CompanyData::getPrice).setCaption("Price $");
		grid.addColumn(CompanyData::getRevenuePct).setCaption("Revenue %");
		grid.addColumn(CompanyData::getGrowthPct).setCaption("Growth %");
		grid.addColumn(CompanyData::getProductPct).setCaption("Product %");
		grid.addColumn(CompanyData::getMarketPct).setCaption("Market %");
		this.eventBus = eventBus;
	}

	public void setItems(List<CompanyData> items) {
		grid.setItems(items);
		grid.addSelectionListener(
				event -> eventBus.publish(this,
						new SelectEvent(event.getFirstSelectedItem().orElse(null))));
		if (items.size() > 0) {
			grid.select(items.get(0));
		}
	}

	public void refresh(CompanyData item) {
		grid.getDataProvider().refreshItem(item);
	}

	public class SelectEvent extends CustomEvent<CompanyData> {

		public SelectEvent(CompanyData payload) {
			super(payload);
		}

	}
}
