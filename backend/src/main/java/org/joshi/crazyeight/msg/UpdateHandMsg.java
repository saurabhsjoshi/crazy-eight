package org.joshi.crazyeight.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.network.Message;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateHandMsg extends Message {
    private final List<Card> cards;
}
