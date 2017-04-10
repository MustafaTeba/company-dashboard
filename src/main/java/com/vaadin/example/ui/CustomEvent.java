package com.vaadin.example.ui;

public abstract class CustomEvent<T> {
	private final T payload;

	public CustomEvent(T payload) {
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}
}
