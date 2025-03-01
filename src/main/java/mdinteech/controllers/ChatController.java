package mdinteech.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import mdinteech.entities.Ville;
import mdinteech.utils.VilleBase;
import mdinteech.entities.ChatManager;

public class ChatController {
    @FXML private ListView<String> chatHistory;
    @FXML private TextField messageInput;

    @FXML
    private void envoyerMessage() {
        String message = messageInput.getText().trim();
        if(message.isEmpty()) return;

        chatHistory.getItems().add("Vous: " + message);
        String reponse = traiterRequete(message);
        chatHistory.getItems().add("Bot: " + reponse);
        messageInput.clear();
    }

    private String traiterRequete(String message) {
        // Gestion spéciale avant extraction ville
        String specialResponse = ChatManager.processMessage(message, null);
        if(specialResponse != null) return specialResponse;

        String villeNom = extraireVille(message);
        if(villeNom.isEmpty()) return "Veuillez mentionner une ville (ex: 'gastronomie à Tunis')";

        Ville ville = VilleBase.getVille(villeNom);
        if(ville == null) return "Je ne connais pas cette ville :(";

        // Réponses avec gestion de scénario
        String scenarioResponse = ChatManager.processMessage(message, ville);
        if(scenarioResponse != null) return scenarioResponse;

        if(message.matches(".*(histoire|historique).*")) return ville.getHistoire();
        if(message.matches(".*(anecdote|curiosité).*")) return ville.getAnecdotes();
        if(message.matches(".*(activité|que faire|visiter).*")) return ville.getActivites();

        return "Je peux vous parler de : Histoire, Anecdotes, Activités, Gastronomie ou Nature !";
    }

    private String extraireVille(String message) {
        return message.replaceAll("(?i).*?(Tunis|Sousse|Bizerte|Ariana|La Marsa|Zaghouan|Hammamet|Le Kef|Nabeul|Djerba|Gabès|Manouba).*", "$1");
    }
}