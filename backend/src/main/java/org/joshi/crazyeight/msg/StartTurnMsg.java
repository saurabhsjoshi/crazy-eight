package org.joshi.crazyeight.msg;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.network.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class StartTurnMsg extends Message {
    private Integer cardsToDraw = 0;

    private Card topCard;
}
