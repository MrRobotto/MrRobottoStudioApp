/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.servershared.io;



import java.io.IOException;

import studio.mr.robotto.protocol.comutils.ComUtils;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;

/**
 * Created by aaron on 08/03/2015.
 */
public interface ReaderManager {
    public Object runReadOperation(ReadOperation operation) throws ReadException, TimeOutException;

    String readCommand() throws ReadException, TimeOutException;

    char readChar() throws ReadException, TimeOutException;

    int readInt32() throws ReadException, TimeOutException;

    public static interface ReadOperation {
        public Object read(ComUtils.Reader reader) throws IOException, IndexOutOfBoundsException;
    }
}
