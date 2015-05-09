/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.socketlayer;

import studio.mr.robotto.protocol.constants.States;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.CommandException;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;
import studio.mr.robotto.protocol.servershared.statemachine.ProtocolParser;

/**
 * Created by aaron on 08/05/2015.
 */
public class StudioProtocolParser implements ProtocolParser {
    @Override
    public String getStateFromCommand(ReaderManager readerManager) throws CommandException, ReadException, TimeOutException {
        String command = readerManager.readCommand();
        switch (command) {
            case "UPDT":
                return States.UPDT_STATE;
            default:
                return "";
        }
    }
}
