;              
CREATE USER IF NOT EXISTS SA SALT '098691476923ee29' HASH 'd3e5469044f03a8e1cde184c73d3bb64d250ce8b407d2673dc4c62658cb18cfc' ADMIN;
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_797867CF_BBD0_4544_97A0_140F4BFDEFDD START WITH 33 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_96A1EE97_8FB7_4757_BD00_7852FDEAD834 START WITH 66 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_2629ED1B_3BBE_4D7C_9742_32024CE36709 START WITH 37 BELONGS_TO_TABLE;
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_0ACF8A82_BEDE_43DF_9D6E_2E95D6D25D6D START WITH 66 BELONGS_TO_TABLE;
CREATE CACHED TABLE PUBLIC.MESSAGE(
    ID INT NOT NULL,
    TEXT TEXT,
    PHOTO TEXT,
    KEYBOARD_ID INT
);
ALTER TABLE PUBLIC.MESSAGE ADD CONSTRAINT PUBLIC.CONSTRAINT_63 PRIMARY KEY(ID);
-- 37 +/- SELECT COUNT(*) FROM PUBLIC.MESSAGE;
INSERT INTO PUBLIC.MESSAGE(ID, TEXT, PHOTO, KEYBOARD_ID) VALUES
(1, STRINGDECODE('\u0427\u0442\u043e\u0431\u044b \u043f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c \u043d\u0443\u0436\u043d\u043e \u0437\u0430\u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f'), NULL, 1),
(2, STRINGDECODE('\u0413\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e'), NULL, 3),
(3, STRINGDECODE('\u041c\u0435\u043d\u044e \u0430\u0434\u043c\u0438\u043d\u0430'), NULL, 7),
(10, STRINGDECODE('\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u043f\u043e\u043b'), NULL, 2),
(11, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0432\u0430\u0448\u0435 \u0424\u0418\u041e'), NULL, 10),
(12, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0432\u0430\u0448\u0443 \u0434\u0430\u0442\u0443 \u0440\u043e\u0436\u0434\u0435\u043d\u0438\u044f'), NULL, 10),
(13, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u0432\u043e\u0439 \u0433\u043e\u0440\u043e\u0434'), NULL, 10),
(14, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u0432\u043e\u0439 \u043d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430'), NULL, 5),
(15, STRINGDECODE('\u0413\u043e\u0442\u043e\u0432\u043e! \u0427\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438 \u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e, \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 /start'), NULL, 0),
(20, STRINGDECODE('\u041b\u0438\u0447\u043d\u044b\u0439 \u043a\u0430\u0431\u0438\u043d\u0435\u0442'), NULL, 4),
(21, STRINGDECODE('\u0427\u0442\u043e \u0431\u0443\u0434\u0435\u043c \u043c\u0435\u043d\u044f\u0442\u044c?'), NULL, 6),
(22, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u043e\u0432\u043e\u0435 \u0438\u043c\u044f'), NULL, 10),
(23, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u043e\u0432\u044b\u0439 \u043d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430'), NULL, 5),
(24, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u043e\u0432\u044b\u0439 \u0433\u043e\u0440\u043e\u0434'), NULL, 10),
(25, STRINGDECODE('\u0414\u0430\u043d\u043d\u044b\u0435 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u043e\u0431\u043d\u043e\u0432\u043b\u0435\u043d\u044b!'), NULL, 0),
(26, STRINGDECODE('\u041c\u0435\u043d\u044e \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u044f \u0432\u043e\u043f\u0440\u043e\u0441\u0430\u043c\u0438'), NULL, 8),
(27, STRINGDECODE('\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0432\u043e\u043f\u0440\u043e\u0441'), NULL, 10),
(28, STRINGDECODE('\u0427\u0442\u043e \u0431\u0443\u0434\u0435\u043c \u043c\u0435\u043d\u044f\u0442\u044c?'), NULL, 9),
(29, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0442\u0435\u043a\u0441\u0442 \u0432\u043e\u043f\u0440\u043e\u0441\u0430'), NULL, 10),
(30, STRINGDECODE('\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0442\u0438\u043f \u043e\u0442\u0432\u0435\u0442\u0430'), NULL, 11),
(31, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441 \u0442\u0435\u043f\u0435\u0440\u044c \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442\u0441\u044f!'), NULL, 0),
(32, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441 \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442\u0441\u044f'), NULL, 0),
(33, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u043e\u0431\u043d\u043e\u0432\u043b\u0435\u043d!'), NULL, 0),
(34, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u043d!'), NULL, 8),
(35, STRINGDECODE('\u0413\u043e\u0442\u043e\u0432\u043e! \u0412\u043e\u043f\u0440\u043e\u0441\u043e\u0432 \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435\u0442'), NULL, 0),
(36, STRINGDECODE('\u041e\u0442\u043f\u0440\u0430\u0432\u044c\u0442\u0435 \u0444\u043e\u0442\u043e'), NULL, 0),
(37, STRINGDECODE('\u0417\u0430\u043f\u0438\u0448\u0438\u0442\u0435 \u0430\u0443\u0434\u0438\u043e'), NULL, 0);
INSERT INTO PUBLIC.MESSAGE(ID, TEXT, PHOTO, KEYBOARD_ID) VALUES
(38, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441: '), NULL, 0),
(39, STRINGDECODE('\u041e\u0442\u0432\u0435\u0442: '), NULL, 0),
(40, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0432\u043e\u043f\u0440\u043e\u0441'), NULL, 0),
(41, STRINGDECODE('\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430'), NULL, 0),
(42, STRINGDECODE('\u0412\u0430\u0448\u0430 \u0437\u0430\u044f\u0432\u043a\u0430 \u043f\u0440\u0438\u043d\u044f\u0442\u0430!'), NULL, 0),
(43, STRINGDECODE('\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0437\u0430\u043f\u0440\u043e\u0441 \u0437\u0432\u043e\u043d\u043a\u0430'), NULL, 10),
(44, STRINGDECODE('\u0417\u0432\u043e\u043d\u043e\u043a \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d'), NULL, 0),
(45, STRINGDECODE('\u0418\u043c\u044f: '), NULL, 0),
(46, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441: '), NULL, 0),
(47, STRINGDECODE('\u041d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430: '), NULL, 0);
CREATE CACHED TABLE PUBLIC.BUTTON(
    ID INT NOT NULL,
    TEXT TEXT,
    COMMAND_ID INT,
    URL TEXT,
    REQUEST_CONTACT BOOLEAN
);
ALTER TABLE PUBLIC.BUTTON ADD CONSTRAINT PUBLIC.CONSTRAINT_7 PRIMARY KEY(ID);
-- 26 +/- SELECT COUNT(*) FROM PUBLIC.BUTTON;
INSERT INTO PUBLIC.BUTTON(ID, TEXT, COMMAND_ID, URL, REQUEST_CONTACT) VALUES
(1, '/start', 1, NULL, FALSE),
(2, '/admin', 4, NULL, FALSE),
(3, STRINGDECODE('\u0417\u0430\u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f'), 2, NULL, FALSE),
(4, STRINGDECODE('\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u043d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430'), 0, NULL, TRUE),
(5, STRINGDECODE('\u041b\u0438\u0447\u043d\u044b\u0439 \u043a\u0430\u0431\u0438\u043d\u0435\u0442'), 3, NULL, FALSE),
(10, STRINGDECODE('\u041d\u0430\u0437\u0430\u0434'), 0, NULL, FALSE),
(11, STRINGDECODE('\u041c\u0443\u0436\u0447\u0438\u043d\u0430'), 0, NULL, FALSE),
(12, STRINGDECODE('\u0416\u0435\u043d\u0449\u0438\u043d\u0430'), 0, NULL, FALSE),
(22, STRINGDECODE('\u0420\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u0440\u043e\u0444\u0438\u043b\u044c'), 0, NULL, FALSE),
(23, STRINGDECODE('\u0424\u0418\u041e'), 0, NULL, FALSE),
(24, STRINGDECODE('\u041d\u043e\u043c\u0435\u0440 \u0442\u0435\u043b\u0435\u0444\u043e\u043d\u0430'), 0, NULL, FALSE),
(25, STRINGDECODE('\u0413\u043e\u0440\u043e\u0434'), 0, NULL, FALSE),
(26, STRINGDECODE('\u0420\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0432\u043e\u043f\u0440\u043e\u0441\u044b \u043f\u0440\u0438 \u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u0438'), 5, NULL, FALSE),
(27, STRINGDECODE('\u0420\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0432\u043e\u043f\u0440\u043e\u0441\u044b'), 0, NULL, FALSE),
(28, STRINGDECODE('\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0432\u043e\u043f\u0440\u043e\u0441'), 0, NULL, FALSE),
(29, STRINGDECODE('\u0422\u0435\u043a\u0441\u0442 \u0432\u043e\u043f\u0440\u043e\u0441\u0430'), 0, NULL, FALSE),
(30, STRINGDECODE('\u0422\u0438\u043f \u043e\u0442\u0432\u0435\u0442\u0430'), 0, NULL, FALSE),
(31, STRINGDECODE('\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432\u043e\u043f\u0440\u043e\u0441?'), 0, NULL, FALSE),
(32, STRINGDECODE('\u0422\u0435\u043a\u0441\u0442'), 0, NULL, FALSE),
(33, STRINGDECODE('\u0424\u043e\u0442\u043e'), 0, NULL, FALSE),
(34, STRINGDECODE('\u0410\u0443\u0434\u0438\u043e'), 0, NULL, FALSE),
(35, STRINGDECODE('\u041a\u043e\u043d\u0442\u0430\u043a\u0442'), 0, NULL, FALSE),
(36, STRINGDECODE('\u0417\u0430\u043a\u0430\u0437\u0430\u0442\u044c \u043e\u0431\u0440\u0430\u0442\u043d\u044b\u0439 \u0437\u0432\u043e\u043d\u043e\u043a'), 6, NULL, FALSE),
(37, STRINGDECODE('\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0437\u0430\u043a\u0430\u0437\u0430\u043d\u043d\u044b\u0445 \u0437\u0432\u043e\u043d\u043a\u043e\u0432'), 7, NULL, FALSE),
(38, STRINGDECODE('\u0417\u0432\u043e\u043d\u043e\u043a \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d'), 0, NULL, FALSE),
(39, STRINGDECODE('\u0414\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043d\u0430\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0435'), 8, NULL, FALSE);
CREATE CACHED TABLE PUBLIC.COMMAND(
    ID INT NOT NULL,
    COMMAND_TYPE_ID INT,
    MESSAGE_ID INT
);
ALTER TABLE PUBLIC.COMMAND ADD CONSTRAINT PUBLIC.CONSTRAINT_6 PRIMARY KEY(ID);
-- 8 +/- SELECT COUNT(*) FROM PUBLIC.COMMAND;
INSERT INTO PUBLIC.COMMAND(ID, COMMAND_TYPE_ID, MESSAGE_ID) VALUES
(1, 1, 0),
(2, 2, 0),
(3, 3, 0),
(4, 4, 0),
(5, 5, 0),
(6, 6, 0),
(7, 7, 0),
(8, 8, 0);
CREATE CACHED TABLE PUBLIC.KEYBOARD(
    ID INT NOT NULL,
    BUTTON_IDS TEXT,
    INLINE BOOLEAN,
    COMMENT TEXT
);
ALTER TABLE PUBLIC.KEYBOARD ADD CONSTRAINT PUBLIC.CONSTRAINT_4 PRIMARY KEY(ID);
-- 12 +/- SELECT COUNT(*) FROM PUBLIC.KEYBOARD;
INSERT INTO PUBLIC.KEYBOARD(ID, BUTTON_IDS, INLINE, COMMENT) VALUES
(1, '3', FALSE, 'Sign up'),
(2, '11,12', TRUE, 'man or woman'),
(3, '5,36', FALSE, 'main menu'),
(4, '22;10', FALSE, 'Personal Area menu'),
(5, '4;10', FALSE, 'RequestContact'),
(6, '23,24;25;10', FALSE, ''),
(7, '26,37;39', FALSE, 'admin menu'),
(8, '27,28;10', FALSE, 'edit questions menu'),
(9, '29,30;31;10', FALSE, 'edit question menu'),
(10, '10', FALSE, 'back'),
(11, '32,33;34,35;10', FALSE, 'question type'),
(12, '38;10', FALSE, 'question menu');
CREATE CACHED TABLE PUBLIC.QUESTION(
    ID INT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_0ACF8A82_BEDE_43DF_9D6E_2E95D6D25D6D) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_0ACF8A82_BEDE_43DF_9D6E_2E95D6D25D6D,
    TEXT TEXT,
    TYPE INT,
    SHOW BOOLEAN DEFAULT TRUE
);
ALTER TABLE PUBLIC.QUESTION ADD CONSTRAINT PUBLIC.CONSTRAINT_E PRIMARY KEY(ID);
-- 5 +/- SELECT COUNT(*) FROM PUBLIC.QUESTION;
INSERT INTO PUBLIC.QUESTION(ID, TEXT, TYPE, SHOW) VALUES
(1, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441 1'), 0, TRUE),
(33, STRINGDECODE('\u0421\u043a\u043e\u043b\u044c\u043a\u043e \u0432\u0430\u043c \u043b\u0435\u0442?'), 0, TRUE),
(34, STRINGDECODE('\u041e\u0442\u043f\u0440\u0430\u0432\u044c\u0442\u0435 \u0432\u0430\u0448\u0435 \u0444\u043e\u0442\u043e'), 1, TRUE),
(35, STRINGDECODE('\u0417\u0430\u043f\u0438\u0448\u0438\u0442\u0435 \u0430\u0443\u0434\u0438\u043e'), 0, TRUE),
(36, STRINGDECODE('\u041e\u0442\u043f\u0440\u0430\u0432\u044c\u0442\u0435 \u0441\u0432\u043e\u0439 \u043a\u043e\u043d\u0442\u0430\u043a\u0442'), 0, TRUE);
CREATE CACHED TABLE PUBLIC.ANSWER(
    ID INT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_2629ED1B_3BBE_4D7C_9742_32024CE36709) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_2629ED1B_3BBE_4D7C_9742_32024CE36709,
    QUESTION_ID INT NOT NULL,
    USER_ID INT NOT NULL,
    TEXT TEXT,
    PHOTO TEXT,
    AUDIO TEXT,
    CONTACT_USER_ID INT,
    CONTACT_FIRST_NAME TEXT,
    CONTACT_SECOND_NAME TEXT,
    CONTACT_PHONE_NUMBER TEXT
);
ALTER TABLE PUBLIC.ANSWER ADD CONSTRAINT PUBLIC.CONSTRAINT_73 PRIMARY KEY(ID);
-- 14 +/- SELECT COUNT(*) FROM PUBLIC.ANSWER;
INSERT INTO PUBLIC.ANSWER(ID, QUESTION_ID, USER_ID, TEXT, PHOTO, AUDIO, CONTACT_USER_ID, CONTACT_FIRST_NAME, CONTACT_SECOND_NAME, CONTACT_PHONE_NUMBER) VALUES
(1, 1, 338849363, STRINGDECODE('\u041e\u0442\u0432\u0435\u0442 1'), NULL, NULL, 0, NULL, NULL, NULL),
(2, 33, 338849363, '20', NULL, NULL, 0, NULL, NULL, NULL),
(3, 1, 338849363, STRINGDECODE('\u041e\u0442\u0432\u0435\u0442'), NULL, NULL, 0, NULL, NULL, NULL),
(4, 33, 338849363, '20,5', NULL, NULL, 0, NULL, NULL, NULL),
(5, 1, 338849363, STRINGDECODE('\u041e\u0442\u0432\u0435\u0442 1'), NULL, NULL, 0, NULL, NULL, NULL),
(6, 33, 338849363, '20', NULL, NULL, 0, NULL, NULL, NULL),
(7, 34, 338849363, NULL, 'AgADAgADwKcxG-EyOEuzL9vUWfCQ_Zs9Sw0ABCl2PDuqPE32sDULAAEC', NULL, 0, NULL, NULL, NULL),
(8, 35, 338849363, STRINGDECODE('\u043d\u0435\u0442\u0443'), NULL, NULL, 0, NULL, NULL, NULL),
(9, 36, 338849363, STRINGDECODE('\u041a\u043e\u043d\u0442\u0430\u043a\u0442'), NULL, NULL, 0, NULL, NULL, NULL),
(10, 1, 43394658, STRINGDECODE('\u041e\u0442\u0432\u0435\u0442 1'), NULL, NULL, 0, NULL, NULL, NULL),
(11, 33, 43394658, '20', NULL, NULL, 0, NULL, NULL, NULL),
(12, 34, 43394658, NULL, 'AgADAgADDqgxG_QlOEuBIRGUWClxr9AOSw0ABFiLwBMGg_sfliQLAAEC', NULL, 0, NULL, NULL, NULL),
(13, 35, 43394658, NULL, NULL, NULL, 0, NULL, NULL, NULL),
(14, 36, 43394658, '87057024808', NULL, NULL, 0, NULL, NULL, NULL);
CREATE CACHED TABLE PUBLIC.USER(
    ID INT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_96A1EE97_8FB7_4757_BD00_7852FDEAD834) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_96A1EE97_8FB7_4757_BD00_7852FDEAD834,
    CHAT_ID INT NOT NULL,
    RULES INT DEFAULT 1,
    NAME TEXT,
    PHONE_NUMBER TEXT,
    CITY TEXT,
    SEX BOOLEAN,
    BIRTHDAY TEXT
);
ALTER TABLE PUBLIC.USER ADD CONSTRAINT PUBLIC.CONSTRAINT_2 PRIMARY KEY(ID);
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.USER;
INSERT INTO PUBLIC.USER(ID, CHAT_ID, RULES, NAME, PHONE_NUMBER, CITY, SEX, BIRTHDAY) VALUES
(36, 43394658, 3, STRINGDECODE('\u0414\u0430\u043d\u0438\u044f\u0440'), '77011222339', STRINGDECODE('\u041a\u0430\u0440\u0430\u0433\u0430\u043d\u0434\u0430'), TRUE, '13.02.1997'),
(37, 43394658, 3, NULL, NULL, NULL, FALSE, NULL);
CREATE CACHED TABLE PUBLIC.REQUEST_CALL(
    ID INT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_797867CF_BBD0_4544_97A0_140F4BFDEFDD) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_797867CF_BBD0_4544_97A0_140F4BFDEFDD,
    TEXT TEXT,
    PHONE_NUMBER TEXT,
    CALLED BOOLEAN DEFAULT FALSE,
    NAME TEXT
);
ALTER TABLE PUBLIC.REQUEST_CALL ADD CONSTRAINT PUBLIC.CONSTRAINT_D PRIMARY KEY(ID);
-- 2 +/- SELECT COUNT(*) FROM PUBLIC.REQUEST_CALL;
INSERT INTO PUBLIC.REQUEST_CALL(ID, TEXT, PHONE_NUMBER, CALLED, NAME) VALUES
(1, STRINGDECODE('\u0412\u043e\u043f\u0440\u043e\u0441'), '87057024808', FALSE, STRINGDECODE('\u0414\u0430\u043d\u0438\u044f\u0440')),
(2, 'Djghjc', '87011222339', FALSE, 'Lfybzh');