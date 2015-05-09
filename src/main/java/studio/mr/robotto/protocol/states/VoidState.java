/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.states;

import studio.mr.robotto.protocol.constants.States;
import studio.mr.robotto.protocol.exceptions.applicationexceptions.ApplicationException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.WriteException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.ParseException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.StateException;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;
import studio.mr.robotto.protocol.servershared.io.WriterManager;
import studio.mr.robotto.protocol.servershared.statemachine.StateNode;

/**
 * Created by aaron on 08/05/2015.
 */
public class VoidState implements StateNode {

    @Override
    public String getState() {
        return States.VOID_STATE;
    }

    @Override
    public boolean isFinalState() {
        return false;
    }

    @Override
    public Object parseRequestBody(ReaderManager readerManager) throws ParseException, ReadException {
        return null;
    }

    @Override
    public void checkPreviousState(String previousState) throws StateException {

    }

    @Override
    public void process(WriterManager writerManager, Object controller, Object parsedMessage) throws ApplicationException, WriteException, TimeOutException {

    }

}
