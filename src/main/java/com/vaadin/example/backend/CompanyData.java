package com.vaadin.example.backend;

import java.math.BigDecimal;

import javax.persistence.Entity;

@Entity(name = "CompanyData")
public class CompanyData extends AbstractEntity {

	private String name;

	private BigDecimal price;

	private BigDecimal revenuePct;

	private BigDecimal growthPct;

	private BigDecimal productPct;

	private BigDecimal marketPct;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getRevenuePct() {
		return revenuePct;
	}

	public void setRevenuePct(BigDecimal revenuePct) {
		this.revenuePct = revenuePct;
	}

	public BigDecimal getGrowthPct() {
		return growthPct;
	}

	public void setGrowthPct(BigDecimal growthPct) {
		this.growthPct = growthPct;
	}

	public BigDecimal getProductPct() {
		return productPct;
	}

	public void setProductPct(BigDecimal productPct) {
		this.productPct = productPct;
	}

	public BigDecimal getMarketPct() {
		return marketPct;
	}

	public void setMarketPct(BigDecimal marketPct) {
		this.marketPct = marketPct;
	}
}
