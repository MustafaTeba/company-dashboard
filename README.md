# Company Dashboard sample
Sample application for implementing a dashboard application having a simple CRUD. You need Maven to build the project.
Just execute `mvn package spring-boot:run`. Alternatively you can just import the project as a Maven project to your IDE and execute the CompanydashboardApplication as a Java application. Finally, navigate to http://localhost:8080

This sample demonstrates one way to implement certain things compared to GXT. The GXT sample is available at https://github.com/johannesh2/company-dashboard-gxt.

There are a bunch of useful add-ons used in addition to Vaadin Framework core: Spring Boot, Spring Data JPA, Vaadin Spring, Extensions for Vaadin Spring, Add-ons for Vaadin Spring, and Vaadin Charts.

This sample contains dependency for Vaadin Charts which is licensed under CVALv3.
Trial licenses available at https://vaadin.com/charts.

Project sample code is licensed under Apache 2.0.

## Spring Boot
"Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run". We take an opinionated view of the Spring platform and third-party libraries so you can get started with minimum fuss. Most Spring Boot applications need very little Spring configuration." - Spring Boot [web page](https://projects.spring.io/spring-boot/).

In practice this means, that instead of building the project as WAR, the project is built as a JAR. The project configuration and pulled dependencies rely on conventions and bunch of defaults. Use src/main/resources/application.properties to change configration.

## Why is MyUI.init empty?
Short answer: Because of Vaadin Spring.
Longer answer: Vaadin Spring uses SpringNavigator, which picks up the `@SpringView` annotated CompanyDashboard view. MyUI is annoted with `@SpringViewDisplay` which allows SpringNavigator to use the `@SpringUI` annotated UI as the view display. Magic yes, but the right kind-of well documented magic. Check CRUD tutorial https://spring.io/guides/gs/crud-with-vaadin/ and Vaadin Spring tutorial http://vaadin.github.io/spring-tutorial/

## Custom theme based on the Valo theme
The project has a custom theme, with some minimal changes mostly done with Valo theme engine variables. By default the build compiles the SASS based theme to CSS. If you want to use on-the-fly theme compilation, comment out the compile-theme goal execution for maven-vaadin-plugin in the pom.xml file.
