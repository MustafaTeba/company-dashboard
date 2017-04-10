package com.vaadin.example.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.example.backend.CompanyData;
import com.vaadin.example.backend.CompanyDataRepository;
import com.vaadin.example.ui.CompanyDetailsEditor.EditEvent;
import com.vaadin.example.ui.CompanyGrid.SelectEvent;
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
			CompanyDetailsEditor detailsEditor,
			CompanyGrid grid) {
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
		List<CompanyData> companyData = repository.findAll();
		grid.setItems(companyData);
		chart.setItems(companyData);
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onItemSelected(SelectEvent event) {
		// Fetch latest from db
		CompanyData data = repository.findOne(event.getPayload().getId());
		grid.refresh(data);
		detailsEditor.setEditedItem(data);
	}

	@EventBusListenerMethod(scope = EventScope.UI)
	public void onItemEdited(EditEvent event) {
		CompanyData saved = repository.saveAndFlush(event.getPayload());
		grid.refresh(saved);
		detailsEditor.setEditedItem(saved);
	}
}
