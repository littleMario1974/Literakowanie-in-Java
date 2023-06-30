import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
  private List<String> database;
  private TextField inputField;
  private ListView<String> wordList;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    database = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader("words.txt"))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] words = line.split("\\s+"); // Podział linii na słowa
        for (String word : words) {
          database.add(word);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    primaryStage.setTitle("Literakowanie");

    inputField = new TextField();
    inputField.setPromptText("Wprowadź litery");
    inputField.setOnKeyReleased(event -> searchWords(inputField.getText()));

    wordList = new ListView<>();

    Button clearButton = new Button("Wyczyść");
    clearButton.setOnAction(event -> {
      inputField.clear();
      wordList.getItems().clear();
    });

    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    vbox.getChildren().addAll(inputField, wordList, clearButton);

    Scene scene = new Scene(vbox, 300, 400);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void searchWords(String inputLetters) {
    int letterCount = inputLetters.length();
    List<String> foundWords = findWords(database, inputLetters, letterCount);

    ObservableList<String> words = FXCollections.observableArrayList(foundWords);
    wordList.setItems(words);
  }

  private List<String> findWords(List<String> database, String inputLetters, int letterCount) {
    List<String> foundWords = new ArrayList<>();
    Map<Character, Integer> letters = new HashMap<>();

    for (char letter : inputLetters.toCharArray()) {
      letters.put(letter, letters.getOrDefault(letter, 0) + 1);
    }

    for (String word : database) {
      if (canFormWord(word, new HashMap<>(letters), letterCount)) {
        foundWords.add(word);
      }
    }

    return foundWords;
  }

  private boolean canFormWord(String word, Map<Character, Integer> letters, int letterCount) {
    if (word.length() != letterCount) {
      return false;
    }

    Map<Character, Integer> wordLetters = new HashMap<>();
    for (char letter : word.toCharArray()) {
      wordLetters.put(letter, wordLetters.getOrDefault(letter, 0) + 1);
    }

    for (char letter : wordLetters.keySet()) {
      if (!letters.containsKey(letter) || letters.get(letter) < wordLetters.get(letter)) {
        return false;
      }
    }

    return true;
  }
}
