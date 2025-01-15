package com.example;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class App extends Application {
	private static Scene scene;
	private static Pane root = new Pane();
	private static Button btnSend = new Button("Send");
	private static Button btnConnect = new Button("Connect");
	private static List<TextField> listOfTextField = new ArrayList<>();
	private static VBox chatBox = new VBox();
	private static ScrollPane scrollPane = new ScrollPane();
	private static TextField chatField = new TextField();
	private static TextField hostField = new TextField();
	private static TextField portField = new TextField();
	private static Label alert = new Label();
	private static Label noti = new Label();
	private static Label userNameLabel = new Label("Username");
	private static TextField userName = new TextField();
	private static TextField encryptMethod = new TextField();
	private static Label encryptMethodLabel = new Label("Encrypt Method: DES or Ceasar");
	private static Button btnGo = new Button("Go");
	private static Button btnSet = new Button("Set");
	private String encryptcontent = encryptMethod.getText();
	private static String username = userName.getText();

	@Override
	public void start(Stage primaryStage) {
		try {
			chatUI();
			root.getChildren().addAll(encryptMethodLabel, encryptMethod, userNameLabel, userName, noti, alert,
					chatField, hostField, portField, scrollPane, btnSend, btnConnect, btnGo, btnSet);
			scene = new Scene(root, 600, 620);
			// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Messenger Application");
			primaryStage.setMaxHeight(620);
			primaryStage.setMaxWidth(600);
			primaryStage.setMinHeight(620);
			primaryStage.setMinWidth(600);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void chatUI() {
		scrollPane.setPrefSize(600, 300);
		chatBox.setPrefSize(600, 300);
		scrollPane.setContent(chatBox);
		btnSend.setLayoutX(520);
		btnSend.setLayoutY(400);
		btnSend.setMinSize(60, 60);
		btnConnect.setLayoutX(520);
		btnConnect.setLayoutY(480);
		btnConnect.setMinSize(60, 60);
		chatField.setLayoutX(10);
		chatField.setLayoutY(400);
		hostField.setLayoutX(10);
		hostField.setLayoutY(480);
		portField.setLayoutX(10);
		portField.setLayoutY(520);
		userNameLabel.setLayoutX(10);
		userNameLabel.setLayoutY(340);
		userName.setLayoutX(10);
		userName.setLayoutY(360);
		btnGo.setLayoutX(170);
		btnGo.setLayoutY(360);
		btnSet.setLayoutX(410);
		btnSet.setLayoutY(360);
		listOfTextField.add(chatField);
		listOfTextField.add(hostField);
		listOfTextField.add(portField);
		encryptMethodLabel.setLayoutX(250);
		encryptMethodLabel.setLayoutY(340);
		encryptMethod.setLayoutX(250);
		encryptMethod.setLayoutY(360);
		for (TextField tf : listOfTextField) {
			tf.setMinSize(500, 30);
		}
		chatField.setMinHeight(60);
		chatField.setPromptText("Start Chat here");
		hostField.setPromptText("Enter Host Address");
		portField.setPromptText("Enter port Address");
		userName.setPromptText("Enter your Username here");
		encryptMethod.setPromptText("DES/Ceasar");
		alert.setLayoutX(10);
		alert.setLayoutY(560);
		alert.setFocusTraversable(true);
		noti.setLayoutX(10);
		noti.setLayoutY(310);
		noti.setText(
				"Localhost Address: 127.0.0.1, Port Number: 8080, AWS EC2 IP Address: 3.27.228.108, Port Number: 8080");
		new Thread(new Runnable() {

			@Override
			public void run() {
				addReply();
			}

		});

		btnSet.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				encryptcontent = encryptMethod.getText();
				if (encryptcontent.equalsIgnoreCase("DES") || encryptcontent.equalsIgnoreCase("Ceasar")) {
					System.out.println(encryptcontent);
					alert.setText("You set: " + encryptcontent + " Protocol");
				} else {
					alert.setText("Please type DES or Ceasar only!");
				}
			}
		});
		btnGo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				username = userName.getText();
				if (username != null) {
					alert.setText("Hello " + " " + username);
				} else {
					alert.setText("Please Enter you Username");
				}
			}
		});
		
		chatField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
				//System.out.println("update: "+ oldValue +" "+ newValue );
			}
			
		});
		
		btnSend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				HBox hBox = new HBox();
				hBox.setAlignment(Pos.CENTER_RIGHT);
				hBox.setPadding(new Insets(5, 5, 5, 10));
				Text text = new Text(chatField.getText());
				TextFlow textFlow = new TextFlow(text);
				textFlow.setPadding(new Insets(10, 5, 5, 10));
				textFlow.setBackground(
				new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
				text.setFill(Color.BLUE);
				hBox.getChildren().add(textFlow);
				chatBox.getChildren().add(hBox);
				if(!chatField.getText().isEmpty()) {
					if (encryptcontent.equalsIgnoreCase("DES")) {
						try {
							Client.sendMessagesByDESTest(chatField.getText());

						} catch (InvalidKeyException e) {
							alert.setText("Not Correct Encrypt Key");
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadPaddingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (encryptcontent.equalsIgnoreCase("Ceasar")) {
						try {
							Client.sendMessagesByCeasarTest(chatField.getText());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						alert.setText("Please choose encrypt protocol first");
						return;
					}
					chatField.clear();
					addReply();	
				} else {
					alert.setText("Please enter your messenger");
				}
					
			}

		});
		btnConnect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					if (!(Client.isServerAvailable(hostField.getText(), Integer.parseInt(portField.getText())))) {
						alert.setText("Server is not available: Connection refused\n"
								+ "Failed to connect: Connection refused Please check the server status");		
					} else if ((hostField.getText().equals("127.0.0.1") && Integer.parseInt(portField.getText()) == 8080)
							|| (hostField.getText().equals("3.27.228.108")
									&& Integer.parseInt(portField.getText()) == 8080)) {
						try {
							Client.connection(hostField.getText(), Integer.parseInt(portField.getText()));
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						alert.setText("Server connect successfully!!!!!!");
					} else {
						alert.setText("Host or Address are not correct, please try again");
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		chatBox.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ObservableValue, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				scrollPane.setVvalue((Double) newValue);
			}
		});
	}
	public static void addReply() {
		new Thread(() -> {
			while (true) {
				try {
					String message = Client.receiveMessagesFX();
					if (message != null && !message.trim().isEmpty()) {
						HBox hBox = new HBox();
						hBox.setAlignment(Pos.CENTER_LEFT);
						hBox.setPadding(new Insets(5, 5, 5, 20));

						Text text = new Text(message);
						TextFlow textFlow = new TextFlow(text);
						textFlow.setPadding(new Insets(5, 10, 5, 10));
						textFlow.setBackground(
								new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
						hBox.getChildren().add(textFlow);
						Platform.runLater(() -> chatBox.getChildren().add(hBox));
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}