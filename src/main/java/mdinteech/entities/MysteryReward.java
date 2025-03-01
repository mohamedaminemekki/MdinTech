package mdinteech.entities;

public class MysteryReward {
    private String id;
    private String rewardType;
    private int requiredTrips;
    private int lastAwardedTrips; // nombre de trajets lors du dernier award

    public MysteryReward(String id, String rewardType, int requiredTrips) {
        this.id = id;
        this.rewardType = rewardType;
        this.requiredTrips = requiredTrips;
        this.lastAwardedTrips = 0;
    }

    public boolean isEligible(int totalTrips) {
        // Vérifie si le nombre de trajets écoulés depuis la dernière récompense atteint le seuil
        return totalTrips >= requiredTrips && (totalTrips - lastAwardedTrips) >= requiredTrips;
    }

    public void award() {
        // Enregistrez la récompense (vous pouvez aussi intégrer une logique d'envoi par mail, etc.)
        lastAwardedTrips += requiredTrips;
    }

    public String getRewardType() {
        return rewardType;
    }
}
