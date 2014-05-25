package org.tendiwa.drawing;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartTest extends Application {
	private final int width;
	Map<String, Number> slices = new HashMap<>();
	private int MAX_TABLE_WIDTH = 300;

	public PieChartTest(int width) {
		this.width = width;
	}

	public void add(String sliceName, int value) {
		if (slices.containsKey(sliceName)) {
			Number previousValue = slices.get(sliceName);
			if (previousValue instanceof Integer) {
				slices.put(sliceName, previousValue.intValue() + value);
			} else if (previousValue instanceof Double) {
				slices.put(sliceName, previousValue.doubleValue() + value);
			} else {
				assert false;
			}
		} else {
			slices.put(sliceName, value);
		}
	}

	public void add(String sliceName, double value) {
		if (slices.containsKey(sliceName)) {
			Number previousValue = slices.get(sliceName);
			if (previousValue instanceof Integer) {
				slices.put(sliceName, previousValue.intValue() + value);
			} else if (previousValue instanceof Double) {
				slices.put(sliceName, previousValue.doubleValue() + value);
			} else {
				assert false;
			}
		} else {
			slices.put(sliceName, value);
		}
	}

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group(), width + MAX_TABLE_WIDTH, width);
		scene.getStylesheets().add(getClass().getResource("/pieChart.css").toExternalForm());
		stage.setTitle("Pie chart");
		stage.setWidth(width);
		stage.setHeight(width);

		ObservableList<PieChart.Data> pieChartData = getDatas();
		PieChart chart = new PieChart(pieChartData);
		chart.setLegendVisible(false);
		Node table = getDataTableView(pieChartData);


		Pane pane = new HBox();
		pane.getChildren().addAll(table, chart);
		((Group) scene.getRoot()).getChildren().add(pane);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setResizable(false);
		stage.show();
	}

	private ObservableList<PieChart.Data> getDatas() {
		List<PieChart.Data> dataList = slices
			.keySet()
			.stream()
			.map(name -> new PieChart.Data(name, slices.get(name).doubleValue()))
			.collect(Collectors.toList());
		return FXCollections.observableArrayList(dataList);
	}

	private TableView<PieChart.Data> getDataTableView(ObservableList<PieChart.Data> pieChartData) {
		TableView<PieChart.Data> table = new TableView<>(pieChartData);
		TableColumn<PieChart.Data, String> name = new TableColumn<>("Name");
		name.setCellValueFactory(
			dataStringCellDataFeatures -> new ReadOnlyStringWrapper(dataStringCellDataFeatures.getValue().getName())
		);
		TableColumn<PieChart.Data, Number> value = new TableColumn<>("Value");
		value.setCellValueFactory(
			a -> a.getValue().pieValueProperty()
		);
		table.getColumns().setAll(name, value);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setMaxWidth(MAX_TABLE_WIDTH);
		// Hide header
		table.widthProperty().addListener((a, b, c) -> {
			Pane header = (Pane) table.lookup("TableHeaderRow");
			header.setMaxHeight(0);
			header.setMinHeight(0);
			header.setPrefHeight(0);
			header.setVisible(false);
			header.setManaged(false);
		});
		return table;
	}

	public void draw() {
		// This will initialize javafx toolkit
		new JFXPanel();
		Platform.runLater(() -> this.start(new Stage()));
	}
}
