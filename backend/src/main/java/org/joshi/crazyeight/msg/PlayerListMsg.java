package org.joshi.crazyeight.msg;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joshi.crazyeight.network.Message;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerListMsg extends Message {

    public record PlayerScores(String username, Integer score) {
    }

    private List<PlayerScores> players = new ArrayList<>();
}
