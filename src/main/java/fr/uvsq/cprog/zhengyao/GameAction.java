package fr.uvsq.cprog.zhengyao;

import java.io.Serializable;
import java.util.List;

public class GameAction implements Serializable {
    private String playerName;
    private String actionDescription;
    private List<Card> playedCards;

    public GameAction(String playerName, String actionDescription, List<Card> playedCards) {
        this.playerName = playerName;
        this.actionDescription = actionDescription;
        this.playedCards = playedCards;

    }

    @Override
    public String toString() {
        if (playerName != null && playedCards != null) {
            return playerName + " a joué : " + actionDescription +
                    (playedCards != null && !playedCards.isEmpty()
                            ? " " + playedCards
                            : "");
        } else {

            return actionDescription;

        }

    }
}
