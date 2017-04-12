package com.vaadin.example.ui;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.theme.MyTheme;
import com.vaadin.example.ui.Events.GridSelectEvent;
import com.vaadin.shared.Registration;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;

@SpringComponent
@ViewScope
public class CompanyGrid extends Panel {

	private final Grid<CompanyData> grid = new Grid<>();
	private final EventBus.UIEventBus eventBus;
	private Optional<Registration> gridSelectListenerRegistration = Optional.empty();

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
		registerToSelectionEvent();
		this.eventBus = eventBus;
	}

	public void setItems(List<CompanyData> items) {
		grid.setItems(items);
	}

	public void refresh(CompanyData item) {
		grid.getDataProvider().refreshItem(item);
	}

	public void select(CompanyData item) {
		select(item, true);
	}

	public void select(CompanyData item, boolean fireEvent) {
		// To avoid circular events from grid and chart in CompanyDashboard,
		// let's not necessarily fire events from row selection
		if (!fireEvent) {
			unregisterFromSelectionEvent();
		}
		grid.select(item);
		registerToSelectionEvent();
	}

	private void onGridSelection(SelectionEvent<CompanyData> event) {
		eventBus.publish(this, new GridSelectEvent(event.getFirstSelectedItem().orElse(null)));
	}

	private void unregisterFromSelectionEvent() {
		gridSelectListenerRegistration.ifPresent(Registration::remove);
		gridSelectListenerRegistration = Optional.empty();
	}

	private void registerToSelectionEvent() {
		unregisterFromSelectionEvent();
		gridSelectListenerRegistration = Optional.of(grid.addSelectionListener(this::onGridSelection));
	}
}
