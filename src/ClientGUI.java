import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Encoder;

public class ClientGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    		primaryStage.setTitle("Chat Login");
    		primaryStage.show();
    		
    		GridPane grid = new GridPane();
    		grid.setAlignment(Pos.CENTER);
    		grid.setHgap(10);
    		grid.setVgap(10);
    		grid.setPadding(new Insets(25, 25, 25, 25));

    		Scene scene = new Scene(grid, 300, 275);
    		primaryStage.setScene(scene);
    		
    		Text scenetitle = new Text("Welcome");
    		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    		grid.add(scenetitle, 0, 0, 2, 1);

    		Label userName = new Label("User Name:");
    		grid.add(userName, 0, 1);

    		TextField userTextField = new TextField();
    		grid.add(userTextField, 1, 1);

    		Label pw = new Label("Password:");
    		grid.add(pw, 0, 2);

    		PasswordField passField = new PasswordField();
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

	        			Connection con=DriverManager.getConnection("jdbc:mysql://trowlink.com:3306/JavaChat?autoReconnect=true&useSSL=false","chat","7xsVuPeF1rCQOeo2");
	        			//here sonoo is the database name, root is the username and root is the password
	        			Statement stmt=con.createStatement();
	        			
	        	        final byte[] authBytes = passField.getText().getBytes(StandardCharsets.UTF_8);
	        	        final String encoded = Base64.getEncoder().encodeToString(authBytes);
	        			
	        			ResultSet result = stmt.executeQuery("select * from users where user='"+userTextField.getText()+"' AND pass='"+encoded+"'");
	        			if(result.next()){
	        				System.out.println(result.getInt(1)+"  "+result.getString(2)+"  '"+result.getString(3)+"' = '"+encoded+"'");
	        				actiontarget.setFill(Color.BLUE);
	        				actiontarget.setText("Login Successful!");
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
        				} else {
            				actiontarget.setFill(Color.FIREBRICK);
            				actiontarget.setText("Error while processing request.");
        				}
    				}
            }
        });
    }
    
    public void login() {

	}
}
