package org.joshi.crazyeight.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joshi.crazyeight.deck.Suit;
import org.joshi.crazyeight.network.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompleteTurnMsg extends Message {
    private String card;

    private String additionalCard;

    private Suit suit;
}
