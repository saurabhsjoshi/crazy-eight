package org.joshi.crazyeight.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joshi.crazyeight.network.Message;


@EqualsAndHashCode(callSuper = true)
@Data
public class RigRoundMsg extends Message {
    /**
     * Comma separated string to get the rigged information.
     * First part would indicate the top card
     * Second part would indicate the list of cards to rig in the deck
     * The rest would be for each player
     */
    private String riggedCards;
}
