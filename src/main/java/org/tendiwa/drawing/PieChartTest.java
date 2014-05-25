package org.tendiwa.drawing;

import com.google.common.math.DoubleMath;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PieChartTest extends Application {
	private final int width;
	Map<String, Number> slices = new HashMap<>();
	private int MAX_TABLE_WIDTH = 300;
	private boolean drawn = false;
	private ObservableList<PieChart.Data> pieChartData;
	private PieChart chart;
	private Stage stage;
	private Supplier<String> titleSupplier;

	public PieChartTest(int width) {
		this.width = width;
	}

	public void add(String sliceName, int value) {
		if (drawn) {
			throw new RuntimeException("Pie chart has already been drawn");
		}
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
		System.out.println("DOUBLE");
		// TODO: Duplicated method with double/int parameter.
		if (drawn) {
			throw new RuntimeException("Pie chart has already been drawn");
		}
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

	public List<PieChart.Data> getData() {
		if (!drawn) {
			throw new RuntimeException("Pie chart should be drawn before getting its data");
		}
		return pieChartData;
	}

	public void setTitle(String title) {
		if (!drawn) {
			throw new RuntimeException("Pie chart should be drawn before setting its title");
		}
		chart.setTitle(title);
		stage.setTitle(title);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		Scene scene = new Scene(new Group(), width + MAX_TABLE_WIDTH, width);
		scene.getStylesheets().add(getClass().getResource("/pieChart.css").toExternalForm());
		stage.setTitle("Pie chart");
		stage.setWidth(width);
		stage.setHeight(width);

		pieChartData = getDatas();
		chart = new PieChart(pieChartData);
		chart.setLegendVisible(false);
		Node table = getDataTableView(pieChartData);


		Pane pane = new HBox();
		pane.getChildren().addAll(table, chart);

		if (titleSupplier != null) {
			setTitle(titleSupplier.get());
		}

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
			a -> {
				DoubleProperty property = a.getValue().pieValueProperty();
				if (DoubleMath.isMathematicalInteger(property.doubleValue())) {
					return new SimpleIntegerProperty(property.intValue());
				}
				return property;
			}
		);
		table.getColumns().setAll(name, value);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setMaxWidth(MAX_TABLE_WIDTH);
		// Hide header
		return table;
	}

	/**
	 * Spawns javafx thread and draws the chart with a table.
	 */
	public void draw() {
		// This will initialize javafx toolkit
		drawn = true;
		new JFXPanel();
		Platform.runLater(() -> this.start(new Stage()));
	}

	/**
	 * Set a function that will supply chart's title once the chart is drawn.
	 *
	 * @param supplier
	 * 	String supplier.
	 * @throws java.lang.RuntimeException
	 * 	If pie chart is already drawn.
	 */
	public void setTitleSupplier(Supplier<String> supplier) {
		if (drawn) {
			throw new RuntimeException("Pie chart is already drawn");
		}
		titleSupplier = supplier;
	}
}
