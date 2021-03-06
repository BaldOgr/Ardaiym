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
    EDIT_DESCRIPTION(11),
    CHOOSE_CAR(12),
    CHOOSE_FAMILIES(13),
    HAS_CAR(14),
    ABOUT_US(16),
    CONTACTS(15),
    CALENDAR(17),
    ADMIN_CONTROL_MENU(18),
    MANUAL_STOCK_COMMAND(19),
    SURVEY(20),
    NEW_TEXT(21),
    SEND_STATISTIC_TO_USERS(22),
    SEND_REJECTED_FAMILIES(23),
    SEND_STATISTIC_TO_SHEET(24),
    ACCEPT_SIGN_UP(25),
    ACCEPT_USER_INVITE_BY_VOLUNTEERS(26);


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
