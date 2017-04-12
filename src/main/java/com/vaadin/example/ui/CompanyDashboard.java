package com.vaadin.example.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.backend.CompanyDataRepository;
import com.vaadin.example.ui.Events.ChartSelectEvent;
import com.vaadin.example.ui.Events.EditEvent;
import com.vaadin.example.ui.Events.GridSelectEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = CompanyDashboard.VIEW_NAME)
@UIScope
public class CompanyDashboard extends VerticalLayout implements View {

	// Having the view name empty makes it default view
	public static final String VIEW_NAME = "";
	private final CompanyDataRepository repository;
	private final CompanyChart chart;
	private final CompanyDetailsEditor detailsEditor;
	private final CompanyGrid grid;

	@Autowired
	public CompanyDashboard(CompanyDataRepository repository, UIEventBus eventBus, CompanyChart chart,
			CompanyDetailsEditor detailsEditor, CompanyGrid grid) {
		this.repository = repository;
		this.chart = chart;
		this.detailsEditor = detailsEditor;
		this.grid = grid;
		eventBus.subscribe(this);
		setSizeFull();
		chart.setHeight("300px");
		addComponent(chart);
		grid.setSizeFull();
		detailsEditor.setWidth("450px");
		detailsEditor.setHeight("100%");

		HorizontalLayout bottom = new HorizontalLayout();
		bottom.setSizeFull();
		addComponentsAndExpand(bottom);
		bottom.addComponentsAndExpand(grid);
		bottom.addComponent(detailsEditor);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		List<CompanyData> items = repository.findAll();
		grid.setItems(items);
		chart.setItems(items);
		if (!items.isEmpty()) {
			grid.select(items.get(0));
		}
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onGridItemSelected(GridSelectEvent event) {
		// Fetch latest from db
		CompanyData fresh = (event.getPayload() != null) ? repository.findOne(event.getPayload().getId()) : null;
		refreshDataAndSelections(fresh);
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onChartItemSelected(ChartSelectEvent event) {
		// Fetch latest from db
		CompanyData fresh = (event.getPayload() != null) ? repository.findOne(event.getPayload().getId()) : null;
		refreshDataAndSelections(fresh);
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onItemEdited(EditEvent event) {
		// CompanyDetailsEditor fired an event of updating a bean, let's store
		// it and refresh chart and grid
		CompanyData saved = repository.saveAndFlush(event.getPayload());
		refreshDataAndSelections(saved);
	}

	private void refreshDataAndSelections(CompanyData data) {
		grid.refresh(data);
		grid.select(data, false);
		chart.refresh(data);
		chart.select(data);
		detailsEditor.setEditedItem(data);
	}
}
