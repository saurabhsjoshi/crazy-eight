package org.joshi.crazyeight.network;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joshi.crazyeight.msg.PlayerListMsg;
import org.joshi.crazyeight.msg.UserRegisterMsg;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegisterMsg.class, name = "UserRegister"),
        @JsonSubTypes.Type(value = PlayerListMsg.class, name = "PlayerList")
})
public abstract class Message {
}
