/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.servershared.statemachine;


import studio.mr.robotto.protocol.exceptions.applicationexceptions.ApplicationException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.WriteException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.ParseException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.StateException;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;
import studio.mr.robotto.protocol.servershared.io.WriterManager;

/**
 * Created by aaron on 24/02/2015.
 */
public interface StateNode {
    public String getState();

    public boolean isFinalState();

    Object parseRequestBody(ReaderManager readerManager) throws ParseException, ReadException, TimeOutException;

    void checkPreviousState(String previousState) throws StateException;

    void process(WriterManager writerManager, Object controller, Object parsedMessage) throws ApplicationException, WriteException, TimeOutException;
}
