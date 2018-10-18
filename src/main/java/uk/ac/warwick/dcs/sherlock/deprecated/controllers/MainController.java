package uk.ac.warwick.dcs.sherlock.deprecated.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import uk.ac.warwick.dcs.sherlock.deprecated.SherlockApplication;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

	private static final String FXMLLocation = "/fxml/";
	@FXML
	private Button dashboardButton;
	@FXML
	private Button overviewButton;
	@FXML
	private Button matchGraphButton;
	@FXML
	private Button comparisonButton;
	@FXML
	private Button reportButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//disable all buttons apart from dashboard since they have no functionality in this iteration.
		overviewButton.setDisable(true);
		matchGraphButton.setDisable(true);
		comparisonButton.setDisable(true);
		reportButton.setDisable(true);

		dashboardButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectDashboardScene();
			}
		});

		overviewButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectOverviewScene();
			}
		});

		matchGraphButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectMatchGraphScene();
			}
		});

		comparisonButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectComparisonScene();
			}
		});

		reportButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selectReportScene();
			}
		});

	}

	private void selectDashboardScene() {
		System.out.println("Select the Dashboard scene");
		SherlockApplication.replaceCentre(FXMLLocation + "Dashboard.fxml");
	}

	private void selectOverviewScene() {
		System.out.println("Select the overview scene");
		SherlockApplication.replaceCentre(FXMLLocation + "Overview.fxml");
	}

	private void selectMatchGraphScene() {
		System.out.println("Select the Match graph scene");
		SherlockApplication.replaceCentre(FXMLLocation + "MatchGraph.fxml");
	}

	private void selectReportScene() {
		System.out.println("Select the report scene");
		//		SherlockApplication.replaceCentre("");
	}

	private void selectComparisonScene() {
		System.out.println("Select the Side by Side comparison scene");
		//		SherlockApplication.replaceCentre("");
	}
}
