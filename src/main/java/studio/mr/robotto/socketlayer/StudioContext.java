/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.socketlayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import studio.mr.robotto.ConnectionManager;
import studio.mr.robotto.DebugActivity;
import studio.mr.robotto.protocol.comutils.ComUtils;
import studio.mr.robotto.protocol.constants.States;
import studio.mr.robotto.protocol.exceptions.ErrType;
import studio.mr.robotto.protocol.exceptions.applicationexceptions.ApplicationException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.WriteException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.CommandException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.ParseException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.StateException;
import studio.mr.robotto.protocol.servershared.context.Context;
import studio.mr.robotto.protocol.servershared.io.ComUtilsReaderManager;
import studio.mr.robotto.protocol.servershared.io.ComUtilsWriterManager;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;
import studio.mr.robotto.protocol.servershared.io.WriterManager;
import studio.mr.robotto.protocol.servershared.statemachine.StateMachine;
import studio.mr.robotto.protocol.servershared.statemachine.StateNode;
import studio.mr.robotto.protocol.states.UpdateState;
import studio.mr.robotto.protocol.states.VoidState;

/**
 * Created by aaron on 08/05/2015.
 */
public class StudioContext implements Context {

    private static final int sTimeOut = 500;
    private static final int sMaxConnectionErrors = 600;
    private static final int sMaxErrors = 15;
    private Socket mSocket;
    private DebugActivity mDebugActivity;
    private StateMachine mStateMachine;
    private int mConnectionErrCount = 0;
    private int mErrCount = 0;
    private boolean mClosed;

    public StudioContext(DebugActivity debugActivity, Socket socket) {
        mSocket = socket;
        mDebugActivity = debugActivity;
        //mStateMachine = new StudioStateMachine(States.VOID_STATE, new StudioProtocolParser());
        mStateMachine = new StateMachine(States.VOID_STATE, new StudioProtocolParser()) {
            @Override
            protected void initializeControllers(Map<String, Object> controllers) {
                controllers.put(States.VOID_STATE, null);
                controllers.put(States.UPDT_STATE, mDebugActivity);
            }

            @Override
            protected void initializeStates(Map<String, StateNode> states) {
                states.put(States.VOID_STATE, new VoidState());
                states.put(States.UPDT_STATE, new UpdateState());
            }
        };
        mClosed = false;
    }

    public synchronized void closeContext() {
        mClosed = true;
    }

    @Override
    public StateMachine getStateMachine() {
        return mStateMachine;
    }

    @Override
    public ReaderManager getReader() throws IOException {
        InputStream stream = mSocket.getInputStream();
        ComUtils.Reader reader = new ComUtils.Reader(stream);
        return new ComUtilsReaderManager(reader);
    }

    @Override
    public WriterManager getWriter() throws IOException {
        OutputStream stream = mSocket.getOutputStream();
        ComUtils.Writer writer = new ComUtils.Writer(stream);
        return new ComUtilsWriterManager(writer);
    }

    @Override
    public void initContext() throws SocketException {
        mSocket.setSoTimeout(sTimeOut);
        mStateMachine.initialize();
    }

    @Override
    public void processInputData() {
        try {
            innerProcessInputData(getReader(), getWriter());
        } catch (IOException e) {
            disposeContext();
        }
    }

    private void innerProcessInputData(ReaderManager readerManager, WriterManager writerManager) {
        StateNode node = null;
        String candidateState;
        //TODO: Ojo!! si hay un fallo de escritura no vuelves a intentarlo o que majete?!
        while (!mStateMachine.isInFinalState() && isValidContext()) {
            try {
                mStateMachine.processNext(readerManager, writerManager);
                mErrCount = 0;
                mConnectionErrCount = 0;
            }  catch (ApplicationException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            } catch (StateException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            } catch (ParseException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            }

            catch (CommandException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            }

            //Connection error
            catch (ReadException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            } catch (WriteException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            } catch (TimeOutException e) {
                onError(writerManager, e.getErrType(), e.getMessage());
            }
        }
        disposeContext();
    }

    @Override
    public void closeConnection() {
        try {
            mSocket.getOutputStream().flush();
            mSocket.getInputStream().close();
            mSocket.getOutputStream().close();
            mSocket.close();
        } catch (IOException e) {

        }
    }

    @Override
    public boolean isValidContext() {
        return !mClosed && !mSocket.isClosed() && mConnectionErrCount <= sMaxConnectionErrors && mErrCount <= sMaxErrors;
    }

    @Override
    public void onError(WriterManager writerManager, ErrType errType, String message) {
        if (errType == ErrType.TIMEOUT_ERROR) {
            if (!isValidContext()) {
                disposeContext();
            }
            mConnectionErrCount++;
        }
        else if (errType == ErrType.WRITE_ERROR || errType == ErrType.READ_ERROR) {
            if (!isValidContext()) {
                disposeContext();
            }
        } else if (errType == ErrType.COMMAND_ERROR) {
            if (isValidContext()) {
                //writerManager.writeError(errType, message);
                //writeErrorToLog(errType, message);
                //mErrCount++;
            } else {
                disposeContext();
            }
        } else if (errType == ErrType.PARSE_ERROR) {
            if (isValidContext()) {
                mErrCount++;
            } else {
                disposeContext();
            }
        } else {
            if (isValidContext()) {
                //mStateMachine.getCurrentStateNode().onError(writerManager, errType, message);
                mErrCount++;
            } else {
                disposeContext();
            }
        }
         /*catch (WriteException e) {
            disposeContext();
        } catch (TimeOutException e) {
            disposeContext();
        }*/
    }

    @Override
    public void disposeContext() {
        closeConnection();
    }

}
