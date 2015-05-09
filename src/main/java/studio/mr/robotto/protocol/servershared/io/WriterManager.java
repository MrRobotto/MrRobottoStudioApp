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
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.WriteException;

/**
 * Created by aaron on 05/03/2015.
 */
public interface WriterManager {
    public void runWriteOperation(WriteOperation operation) throws WriteException, TimeOutException;

    public static interface WriteOperation {
        public void write(ComUtils.Writer writer) throws IOException;
    }
}
