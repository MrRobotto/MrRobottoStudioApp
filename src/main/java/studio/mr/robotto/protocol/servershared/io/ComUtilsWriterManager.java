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
import studio.mr.robotto.protocol.constants.Commands;
import studio.mr.robotto.protocol.exceptions.ErrType;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.WriteException;


public class ComUtilsWriterManager implements WriterManager {

    private ComUtils.Writer mWriter;

    public ComUtilsWriterManager(ComUtils.Writer writer) {
        mWriter = writer;
    }

    @Override
    public void runWriteOperation(WriteOperation operation) throws WriteException, TimeOutException {
        try {
            operation.write(mWriter);
        } catch (SocketTimeoutException e) {
            throw new TimeOutException();
        } catch (IOException e) {
            throw new WriteException();
        } catch (RuntimeException e) {
            throw new WriteException();
        }
    }


    public void writeError(final ErrType errCode, final String message) throws WriteException, TimeOutException {
        runWriteOperation(new WriteOperation() {
            @Override
            public void write(ComUtils.Writer writer) throws IOException {
                writer.write_string(Commands.ERROR);
                writer.write_char(' ');
                String error = errCode.toString() + ' ' + message;
                if (error.length() < 10) {
                    writer.write_char('0');
                    writer.write_string(String.valueOf(error.length()));
                } else {
                    writer.write_string(String.valueOf(error.length()));
                }
                writer.write_string(error);
            }
        });
    }

    public void writeExceededErrors() throws WriteException, TimeOutException {
        runWriteOperation(new WriteOperation() {
            @Override
            public void write(ComUtils.Writer writer) throws IOException {
                writer.write_string(Commands.ERROR);
                writer.write_char(' ');
                String error = "Sorry your IQ is too low to play with me, GTFO!";
                if (error.length() < 10) {
                    writer.write_char('0');
                    writer.write_string(String.valueOf(error.length()));
                } else {
                    writer.write_string(String.valueOf(error.length()));
                }
                writer.write_string(error);
            }
        });
    }

}
