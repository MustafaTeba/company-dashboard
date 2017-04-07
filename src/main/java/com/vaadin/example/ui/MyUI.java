package com.vaadin.example.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.UI;

@SpringUI
@Theme("mytheme")
@SpringViewDisplay
public class MyUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
	}
}
