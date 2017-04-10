package com.vaadin.example.ui;

import com.vaadin.example.backend.CompanyData;

public final class Events {

	public static class GridSelectEvent extends CustomEvent<CompanyData> {

		public GridSelectEvent(CompanyData payload) {
			super(payload);
		}

	}

	public static class EditEvent extends CustomEvent<CompanyData> {
		public EditEvent(CompanyData data) {
			super(data);
		}
	}

	public static class ChartSelectEvent extends CustomEvent<CompanyData> {

		public ChartSelectEvent(CompanyData payload) {
			super(payload);
		}
	}
}
