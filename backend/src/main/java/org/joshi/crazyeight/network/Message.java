package org.joshi.crazyeight.network;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joshi.crazyeight.msg.HostMsg;
import org.joshi.crazyeight.msg.PlayerListMsg;
import org.joshi.crazyeight.msg.StartGameMsg;
import org.joshi.crazyeight.msg.UserRegisterMsg;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegisterMsg.class, name = "UserRegister"),
        @JsonSubTypes.Type(value = PlayerListMsg.class, name = "PlayerList"),
        @JsonSubTypes.Type(value = HostMsg.class, name = "Host"),
        @JsonSubTypes.Type(value = StartGameMsg.class, name = "StartGame")
})
public abstract class Message {
}
