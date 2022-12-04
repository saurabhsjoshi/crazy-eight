package org.joshi.crazyeight.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.joshi.crazyeight.deck.Card;
import org.joshi.crazyeight.network.Message;

import java.util.List;

/**
 * Message sent to each player at the beginning of a round.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class StartRoundMsg extends Message {
    private final List<Card> cards;
}
