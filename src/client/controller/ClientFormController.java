package client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;

/**
 * @author :Dilshan
 **/
public class ClientFormController {
    public static String userName;
    public VBox vBoxMsg;
    public TextField txtMeg;
    public Label lblHeader;
    public ScrollPane scrollPane;
    Socket socket;
    Socket socketImage;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    DataInputStream dataInputStreamImage;
    DataOutputStream dataOutputStreamImage;
    String massage = "";


    public void initialize() throws IOException {
        lblHeader.setText(userName);
        socket = new Socket("localhost", 40000);
        socketImage = new Socket("localhost", 40001);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());


        dataInputStreamImage = new DataInputStream(socketImage.getInputStream());
        dataOutputStreamImage = new DataOutputStream(socketImage.getOutputStream());

        dataOutputStream.writeUTF(userName);
        vBoxMsg.heightProperty().addListener((observable, oldValue, newValue) -> {
            scrollPane.setVvalue((Double) newValue);
        });
        new Thread(() -> {
            while (!massage.equals("end")) {
                try {
                    massage = dataInputStream.readUTF();
                    final String[] nameAr = massage.split(" ",2);
                    Text nameClient = new Text(nameAr[0]+":");
                    nameClient.setStyle("-fx-fill: #707072");
                    Text text = new Text(nameAr[1]);
                    text.setWrappingWidth(250);
                    text.setStyle("-fx-font-weight: bold");

                    final VBox vBox = new VBox(nameClient,text);

                    TextFlow textFlow = new TextFlow(vBox);
                    textFlow.setPadding(new Insets(5, 0, 5, 10));

                    final HBox hBox = new HBox(textFlow);
                    hBox.setPadding(new Insets(0, 0, 10, 0));

                    setMassageVbox(hBox);
                } catch (IOException e) {
                    closeAllInstance(socket, dataInputStream, dataOutputStream);
                    e.printStackTrace();
                    break; // Test
                }
            }

        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    final int readInt = dataInputStreamImage.readInt();
                    final byte[] bytes = new byte[readInt];
                    dataInputStreamImage.readFully(bytes);
                    Text text=new Text(dataInputStreamImage.readUTF()+":");
                    text.setStyle("-fx-fill: #707072");
                    Image image = new Image(new ByteArrayInputStream(bytes));
                    ImageView imageView = new ImageView(image);
                    imageView.setStyle("-fx-background-color: rgba(229,229,235,0.51);-fx-background-radius : 20px;");
                    imageView.preserveRatioProperty().set(true);
                    if (image.getWidth() > 50) {
                        imageView.setFitHeight(250);
                        imageView.setFitWidth(250);
                    }
                    TextFlow textFlow = new TextFlow(new VBox(text,imageView));
                    textFlow.setPadding(new Insets(10, 10, 10, 10));
                    HBox hBox = new HBox(textFlow);
                    hBox.setPadding(new Insets(0, 0, 10, 0));
                    setMassageVbox(hBox);
                } catch (IOException e) {
                    closeAllInstance(socket, dataInputStream, dataOutputStream);
                    e.printStackTrace();
                    break; // Test
                }
            }

        }).start();


    }

    private void closeAllInstance(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            if (dataInputStream != null) dataInputStream.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket != null) socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMassageVbox(HBox hBox) {
        Platform.runLater(() -> {
            vBoxMsg.getChildren().add(hBox);
        });
    }

    public void btnSendTextMassage(ActionEvent actionEvent) {
        sendMassage();
    }

    public void sendMassage() {
        massage = txtMeg.getText();
        if (massage.trim().isEmpty()) return;
        try {
            dataOutputStream.writeUTF(massage);
            dataOutputStream.flush();
            Text text = new Text(massage);
            text.setWrappingWidth(250);
            Text you = new Text("You:");
            you.setStyle("-fx-fill: #0bdc0b");
            text.setStyle("-fx-font-weight: bold");
            TextFlow textFlow = new TextFlow(new VBox(you,text));
            final HBox hBox = new HBox(textFlow);
            hBox.setPadding(new Insets(0, 0, 10, 0));
            hBox.setAlignment(Pos.CENTER_RIGHT);
            setMassageVbox(hBox);
            textFlow.setPadding(new Insets(5, 10, 5, 10));
            txtMeg.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void txtMsgAction(ActionEvent actionEvent) {
        sendMassage();
    }


    public void setEmojiImage(String emoji) {
        try {
            File file = new File("src/client/view/asset/img/" + emoji);
            FileInputStream fileInputStream = new FileInputStream(file);
            setImageIfSend(file.getPath());
            final byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            dataOutputStreamImage.writeInt((int) file.length());
            dataOutputStreamImage.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnImageChoose(MouseEvent mouseEvent) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        final File file = fileChooser.showOpenDialog(vBoxMsg.getScene().getWindow());

        setImageIfSend(file.getPath());

        FileInputStream fileInputStream = new FileInputStream(file);
        final byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);
        dataOutputStreamImage.writeInt((int) file.length());
        dataOutputStreamImage.write(bytes);
    }

    private void setImageIfSend(String filePath) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(0, 0, 10, 0));
        Image image = new Image("file:" + filePath);
        ImageView imageView = new ImageView(image);
        imageView.preserveRatioProperty().set(true);
        if (image.getWidth() > 50) {
            imageView.setFitHeight(250);
            imageView.setFitWidth(250);
        }
        TextFlow textFlow = new TextFlow(imageView);
        textFlow.setStyle("-fx-background-color: rgba(47,146,211,0.65);-fx-background-radius : 20px;");
        textFlow.setPadding(new Insets(10, 10, 10, 10));
        hBox.getChildren().add(textFlow);
        vBoxMsg.getChildren().add(hBox);
    }

    public void btnEmojiLike(MouseEvent mouseEvent) {
        setEmojiImage("likeImg.png");
    }

    public void btnEmojiSmile(MouseEvent mouseEvent) {
        setEmojiImage("smileImg.png");
    }

    public void btnEmojiKiss(MouseEvent mouseEvent) {
        setEmojiImage("kissImg.png");
    }

    public void btnEmojiHeart(MouseEvent mouseEvent) {
        setEmojiImage("heart.png");
    }

    public void btnEmojiAngry(MouseEvent mouseEvent) {
        setEmojiImage("hotImg.png");
    }

    public void btnEmojiSad(MouseEvent mouseEvent) {
        setEmojiImage("sadImg.png");
    }


}
