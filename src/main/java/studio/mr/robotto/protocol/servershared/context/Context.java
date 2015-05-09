/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.servershared.context;


import java.io.IOException;
import java.net.SocketException;

import studio.mr.robotto.protocol.exceptions.ErrType;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;
import studio.mr.robotto.protocol.servershared.io.WriterManager;
import studio.mr.robotto.protocol.servershared.statemachine.StateMachine;

/**
 * Created by aaron on 08/03/2015.
 */
public interface Context {
    StateMachine getStateMachine();

    ReaderManager getReader() throws IOException;

    WriterManager getWriter() throws IOException;

    void initContext() throws SocketException;

    void processInputData();

    void closeConnection();

    boolean isValidContext();

    void onError(WriterManager writerManager, ErrType errType, String message);

    void disposeContext();
}
