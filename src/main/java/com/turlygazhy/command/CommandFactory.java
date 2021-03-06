package com.turlygazhy.command;

import com.turlygazhy.command.impl.*;
import com.turlygazhy.command.impl.admin.*;
import com.turlygazhy.command.impl.admin.AcceptSignUpCommand;
import com.turlygazhy.command.impl.auto_mode.ChooseCarCommand;
import com.turlygazhy.command.impl.auto_mode.ChooseFamiliesCommand;
import com.turlygazhy.command.impl.ManualStockCommand;
import com.turlygazhy.command.impl.auto_mode.RegistrationInStockCommand;
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
            case CHOOSE_CAR:
                return new ChooseCarCommand();
            case CHOOSE_FAMILIES:
                return new ChooseFamiliesCommand();
            case HAS_CAR:
                return new RegistrationInStockCommand();
            case ABOUT_US:
                return new AboutUsCommand();
            case CONTACTS:
                return new ContactsCommand();
            case CALENDAR:
                return new CalendarCommand();
            case ADMIN_CONTROL_MENU:
                return new AdminControlCommand();
            case MANUAL_STOCK_COMMAND:
                return new ManualStockCommand();
            case SURVEY:
                return new SurveyCommand();
            case NEW_TEXT:
                return new NewTextCommand();
            case SEND_STATISTIC_TO_USERS:
                return new SendStatisticToUsersCommand();
            case SEND_REJECTED_FAMILIES:
                return new SendRejectedFamiliesCommand();
            case SEND_STATISTIC_TO_SHEET:
                return new SendUserStatisticToSheetsCommand();
            case ACCEPT_SIGN_UP:
                return new AcceptSignUpCommand();
            case ACCEPT_USER_INVITE_BY_VOLUNTEERS:
                return new com.turlygazhy.command.impl.AcceptSignUpCommand();
            default:
                throw new NotRealizedMethodException("Not realized for type: " + type);
        }
    }
}
