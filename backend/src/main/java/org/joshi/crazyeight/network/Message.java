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
        @JsonSubTypes.Type(value = StartRoundMsg.class, name = "StartRound")
})
public abstract class Message {
}
