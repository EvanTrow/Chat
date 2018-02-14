import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.ImageIcon;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClientGUI extends Application {
	
	private String server, username;
	private int port;
	private boolean connected;
	
	public TextArea chatArea = new TextArea();
	public ListView<String> listView = new ListView<String>();
	
	private Client client;
	//private Image icon = Image("res/icon.png");

	//  icon - https://stackoverflow.com/questions/10121991/javafx-application-icon
	//  javafx is completely different from just java we are using javafx cause its better
	
    void cleanup() {
        // stop, reset  -  cleans up window
    }
    
    // show server select dialog
    @Override
    public void start(Stage serverStage) { // the stage name for each view is here serverStage 
    	// there is a diffrent stage name for each part of the app
    	// basicly javafx is simpler that java
    	
    		connected = false;
    	
	    	serverStage.setTitle("Chat Setup");
	    	serverStage.show();
	    	serverStage.getIcons().add(new Image(this.getClass().getResourceAsStream("res/icon64.png"))); //  this works but not the "res/icon.png"
	    	
	    	// dock icon mac os
	    	try {
		    	//com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
		    	//macApp.setDockIconImage (new ImageIcon (getClass ().getResource ("res/icon256.png")).getImage ());
		} catch (Exception e) {
			// TODO: handle exception
		}
	    	
	    	
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 600, 500);
		serverStage.setScene(scene);
		
		Text scenetitle = new Text("Enter Server Address");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label serverLabel = new Label("Server:");
		grid.add(serverLabel, 0, 1);

		TextField serverTextField = new TextField("trowlink.com");
		grid.add(serverTextField, 1, 1);
		
		Label portLabel = new Label("port:");
		grid.add(portLabel, 0, 2);

		TextField portTextField = new TextField("8000");
		grid.add(portTextField, 1, 2);
		
		Button ConnectToServerbtn = new Button("Connect");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(ConnectToServerbtn);
		grid.add(hbBtn, 1, 4);
		
		final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        
            
        ConnectToServerbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent e) {
        		server = serverTextField.getText().trim();
    			port = Integer.parseInt(portTextField.getText().trim());
        		loginStage(serverStage);
            }
        });
    }
    
    
    //show login dialog
	void loginStage(Stage loginStage) {
	    	loginStage.setTitle("Chat Login");
	    	loginStage.show();
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 600, 500);
		loginStage.setScene(scene);
		
		Text scenetitle = new Text("Login To: "+server);
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label userName = new Label("User Name:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField("test");
		grid.add(userTextField, 1, 1);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 2);

		PasswordField passField = new PasswordField();
		passField.setText("12345678");
		grid.add(passField, 1, 2);
		
		Button SignInbtn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(SignInbtn);
		grid.add(hbBtn, 1, 4);
		
		final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        
        SignInbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent e) {

	        		try{
	        			Class.forName("com.mysql.jdbc.Driver");

	        			Connection con=DriverManager.getConnection("jdbc:mysql://"+server+":3306/JavaChat?autoReconnect=true&useSSL=false","chat","7xsVuPeF1rCQOeo2");
	        			//here sonoo is the database name, root is the username and root is the password
	        			Statement stmt=con.createStatement();
	        			
	        	        final String encodedPassword = Decrypt(passField.getText().trim());
	        			
	        			ResultSet result = stmt.executeQuery("select * from users where user='"+userTextField.getText().trim()+"' AND pass='"+encodedPassword+"'");
	        			if(result.next()){
	        				//System.out.println(result.getInt(1)+"  "+result.getString(2)+"  '"+result.getString(3)+"' = '"+encoded+"'");
	        				username = userTextField.getText().trim();
	        				actiontarget.setFill(Color.BLUE);
	        				actiontarget.setText("Login Successful!");
	        			    cleanup();
	        			    initChat(loginStage);
        			    } else {
        					actiontarget.setFill(Color.FIREBRICK);
	        				actiontarget.setText("Invalid login.");
        				}
	        			con.close();

        			}catch(Exception e1){ 
        				System.out.println(e1.getMessage());
        				if(e1.getMessage().contains("empty result set")) {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Invalid login.");
        				} else if(e1.getMessage().contains("Could not create connection to database server")) {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Could not connect to server.");
        				} else {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Error while processing request.");
        				}
    				}
            }
        });
    }
    
    void initChat(Stage chatStage) {
    	
	    	chatStage.setTitle("Chat: "+server+":"+port);
	    	chatStage.show();
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
	
		Scene scene = new Scene(grid, 600, 300);
		chatStage.setScene(scene);
		
		Text scenetitle = new Text("Chat: "+server+":"+port);
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0);
    	
    		try {
    			client = new Client(server, port, username, this);
    			if(client.start()) {
    				connected = true;
    				
    				chatArea.setEditable(false);
    				chatArea.setWrapText(true);
    				grid.add(chatArea, 0, 1);
    				
    				listView.setEditable(false);
    				grid.add(listView, 1, 1);
    				
    				TextField msgTextField = new TextField();
    				grid.add(msgTextField, 0, 2);
    				msgTextField.requestFocus();
    				
    				msgTextField.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
    			            public void handle(ActionEvent e) {
    			        		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgTextField.getText()));
    			        		msgTextField.setText("");
    			        }
    				});
    				
    				Button SendMsgbtn = new Button("Send");
    				HBox sdBtn = new HBox(10);
    				sdBtn.setAlignment(Pos.CENTER_LEFT);
    				sdBtn.getChildren().add(SendMsgbtn);
    				grid.add(sdBtn, 1, 2);
    						
    				SendMsgbtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
    			            public void handle(ActionEvent e) {
    			        		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgTextField.getText()));
    			        		msgTextField.setText("");
    			        }
    				});
    			} else {
    				scenetitle.setText("Unable to connect to server.");
    				
    				Button Reconnectbtn = new Button("Reconnect");
    				HBox rcBtn = new HBox(10);
    				rcBtn.setAlignment(Pos.CENTER_LEFT);
    				rcBtn.getChildren().add(Reconnectbtn);
    				grid.add(rcBtn, 1, 0);
    				
    				Reconnectbtn.setOnAction(new EventHandler<ActionEvent>() {
    			        @Override
    			            public void handle(ActionEvent e) {
    			        			initChat(chatStage);
    			        }
    				});
    			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			scenetitle.setText("Unable to connect to server.");
			
			Button Reconnectbtn = new Button("Reconnect");
			HBox rcBtn = new HBox(10);
			rcBtn.setAlignment(Pos.CENTER_LEFT);
			rcBtn.getChildren().add(Reconnectbtn);
			grid.add(rcBtn, 1, 0);
			
			Reconnectbtn.setOnAction(new EventHandler<ActionEvent>() {
		        @Override
		            public void handle(ActionEvent e) {
		        			initChat(chatStage);
		        }
			});
		}
    }
    
    public void append(String msg) {
    		try {
    			if(msg.contains("ServerAddToUserList:")) {
    				//listView.getItems().add(1, "user");
    			} else {
            		chatArea.appendText(msg);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
    
    public void connectionFailed() {
    		chatArea.appendText("Connection to chat server failed");
	}
    
    // decrypts the password for database
    public String Decrypt(String text) throws NoSuchAlgorithmException {
    	
    		String salt = "nb9af3uobu80ag87bpfu4iwef";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((text+salt).getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	return hexString.toString();
    }
}
