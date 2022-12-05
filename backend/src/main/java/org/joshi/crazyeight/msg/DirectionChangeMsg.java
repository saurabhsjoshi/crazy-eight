package org.joshi.crazyeight.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joshi.crazyeight.network.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class DirectionChangeMsg extends Message {
    private final Integer direction;
}
