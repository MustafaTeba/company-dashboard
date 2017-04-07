package com.vaadin.example.ui;

public final class Events {
	public static abstract class EventPayloadWrapper<T> {
		private final T payload;
		
		public EventPayloadWrapper(T payload) {
			this.payload = payload;
		}
		
		public T getPayload() {
			return payload;
		}
	}
}
