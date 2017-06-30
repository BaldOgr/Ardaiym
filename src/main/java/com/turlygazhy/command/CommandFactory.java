package com.turlygazhy.command;

import com.turlygazhy.command.impl.*;
import com.turlygazhy.command.impl.admin.*;
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
            case NEW_STOCK:
                return new NewStockCommand();
            case ADMIN_MENU:
                return new AdminMenuCommand();
            case ADD_PARTICIPANT_OF_STOCK:
                return new AddToParticipantOfStock();
            case SHOW_NEWS:
                return new ShowNewsCommand();
            case NEW_DISTRIBUTION:
                return new NewDistributionCommand();
            case SHOW_STOCK:
                return new ShowStockCommand();
            case EDIT_DESCRIPTION:
                return new EditDescriptionCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
