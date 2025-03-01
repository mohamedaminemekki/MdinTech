package mdinteech.entities;

import mdinteech.entities.Ville;

public class ChatManager {
    private static String currentScenario = "";
    private static String currentCity = "";
    private static int storyStep = 0;

    public static String processMessage(String message, Ville ville) {
        message = message.toLowerCase();

        // Gestion des salutations
        if(message.matches(".*(bonjour|salut|hello|hey|hi).*")) {
            return "Bonjour ! Je suis votre guide Tunisien. Posez-moi des questions sur :\n"
                    + "- Histoire\n- Anecdotes\n- Activités\n- Gastronomie\n- Nature\n"
                    + "- Ou essayez une aventure interactive avec 'histoire interactive' !";
        }

        // Gestion des scénarios spéciaux
        if(message.contains("histoire interactive")) {
            currentScenario = "AVENTURE";
            currentCity = ville.getNom();
            return demarrerAventure(ville);
        }

        // Réponses standard
        if(message.contains("gastronomie")) return ville.getGastronomie();
        if(message.contains("nature")) return ville.getNature();

        return null;
    }

    private static String demarrerAventure(Ville ville) {
        String[] etapes = ville.getHistoireInteractive().split("\\|");
        if(storyStep < etapes.length) {
            String[] parts = etapes[storyStep].split(":");
            storyStep++;
            return parts[1].trim();
        }
        storyStep = 0;
        currentScenario = "";
        return "Fin de l'aventure ! Voulez-vous :\n1. Recommencer\n2. Changer de ville\n3. Menu principal";
    }
}