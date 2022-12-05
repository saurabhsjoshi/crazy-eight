package org.joshi.crazyeight.network;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joshi.crazyeight.msg.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegisterMsg.class, name = "UserRegister"),
        @JsonSubTypes.Type(value = PlayerListMsg.class, name = "PlayerList"),
        @JsonSubTypes.Type(value = HostMsg.class, name = "Host"),
        @JsonSubTypes.Type(value = StartGameMsg.class, name = "StartGame"),
        @JsonSubTypes.Type(value = StartRoundMsg.class, name = "StartRound"),
        @JsonSubTypes.Type(value = StartTurnMsg.class, name = "StartTurn"),
        @JsonSubTypes.Type(value = CompleteTurnMsg.class, name = "CompleteTurn"),
        @JsonSubTypes.Type(value = UpdateHandMsg.class, name = "UpdateHand"),
        @JsonSubTypes.Type(value = DrawCardMsg.class, name = "DrawCard"),
        @JsonSubTypes.Type(value = PlayerSkippedMsg.class, name = "PlayerSkipped"),
        @JsonSubTypes.Type(value = DirectionChangeMsg.class, name = "DirectionChange"),
        @JsonSubTypes.Type(value = RoundWinnerMsg.class, name = "RoundWinner"),
        @JsonSubTypes.Type(value = GameWinnerMsg.class, name = "GameWinner"),
        @JsonSubTypes.Type(value = RigRoundMsg.class, name = "RigRound")
})
public abstract class Message {
}
