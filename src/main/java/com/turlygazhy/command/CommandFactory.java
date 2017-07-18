package com.turlygazhy.command;

import com.turlygazhy.command.impl.*;
import com.turlygazhy.command.impl.admin_commands.AddReminderCommand;
import com.turlygazhy.command.impl.admin_commands.AdminMenuCommands;
import com.turlygazhy.command.impl.admin_commands.EditQuestionsCommand;
import com.turlygazhy.command.impl.admin_commands.ShowRequestCallCommand;
import com.turlygazhy.exception.NotRealizedMethodException;

/**
 * Created by user on 1/2/17.
 */
public class CommandFactory {
    public static Command getCommand(long id) {
        CommandType type = CommandType.getType(id);
        switch (type) {
            case SHOW_INFO:
                return new ShowInfoCommand();
            case SIGN_UP:
                return new SignUpCommand();
            case PERSONAL_AREA:
                return new PersonalAreaCommand();
            case ADMIN_MENU:
                return new AdminMenuCommands();
            case EDIT_QUESTIONS:
                return new EditQuestionsCommand();
            case ADD_CALL_REQUEST:
                return new RequestCallCommand();
            case SHOW_CALL_REQUEST:
                return new ShowRequestCallCommand();
            case ADD_REMINDER:
                return new AddReminderCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
