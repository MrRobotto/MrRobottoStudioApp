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
import java.net.SocketTimeoutException;

import studio.mr.robotto.protocol.comutils.ComUtils;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;

/**
 * Created by aaron on 08/03/2015.
 */
public class ComUtilsReaderManager implements ReaderManager {

    private ComUtils.Reader mReader;

    public ComUtilsReaderManager(ComUtils.Reader reader) {
        mReader = reader;
    }

    @Override
    public Object runReadOperation(ReadOperation operation) throws ReadException, TimeOutException {
        try {
            return operation.read(mReader);
        } catch (SocketTimeoutException e) {
            throw new TimeOutException();
        } catch (IOException e) {
            throw new ReadException();
        } catch (IndexOutOfBoundsException e) {
            throw new ReadException();
        }
    }

    @Override
    public String readCommand() throws ReadException, TimeOutException {
        Object command = runReadOperation(new ReadOperation() {
            @Override
            public Object read(ComUtils.Reader reader) throws IOException, IndexOutOfBoundsException {
                return reader.read_string(4);
            }
        });
        return (String) command;
    }

    @Override
    public char readChar() throws ReadException, TimeOutException {
        Object ch = runReadOperation(new ReadOperation() {
            @Override
            public Object read(ComUtils.Reader reader) throws IOException, IndexOutOfBoundsException {
                return reader.read_char();
            }
        });
        return Character.toLowerCase((Character) ch);
    }

    @Override
    public int readInt32() throws ReadException, TimeOutException {
        Object i = runReadOperation(new ReadOperation() {
            @Override
            public Object read(ComUtils.Reader reader) throws IOException, IndexOutOfBoundsException {
                return reader.read_int32();
            }
        });
        return (Integer) i;
    }
}
