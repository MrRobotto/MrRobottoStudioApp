/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.servershared.statemachine;


import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.CommandException;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;

/**
 * Created by aaron on 26/02/2015.
 */
public interface ProtocolParser {

    public String getStateFromCommand(ReaderManager readerManager) throws CommandException, ReadException, TimeOutException;

}
