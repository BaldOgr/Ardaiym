package com.turlygazhy.command;

import com.turlygazhy.exception.NotRealizedMethodException;

/**
 * Created by user on 1/1/17.
 */
public enum CommandType {
    SHOW_INFO(1),
    SIGN_UP(2),
    MAIN_MENU(3),
    PERSONAL_AREA(4),
    NEW_STOCK(5),
    ADMIN_MENU(6),
    ADD_PARTICIPANT_OF_STOCK(7),
    SHOW_NEWS(8),
    NEW_DISTRIBUTION(9),
    SHOW_STOCK(10),
    EDIT_DESCRIPTION(11);

    private final int id;

    CommandType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CommandType getType(long id) {
        for (CommandType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new NotRealizedMethodException("There are no type for id: " + id);
    }
}
