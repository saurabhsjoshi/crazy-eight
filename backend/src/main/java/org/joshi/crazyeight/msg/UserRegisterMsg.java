package org.joshi.crazyeight.msg;

import org.joshi.crazyeight.network.Message;

/**
 * Message sent when a new user connects to the server.
 */
public class UserRegisterMsg extends Message {
    public String username;
}
