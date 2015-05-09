/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.servershared.statemachine;

import java.util.HashMap;
import java.util.Map;

import studio.mr.robotto.protocol.exceptions.applicationexceptions.ApplicationException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.ReadException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.TimeOutException;
import studio.mr.robotto.protocol.exceptions.connectionexceptions.WriteException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.CommandException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.ParseException;
import studio.mr.robotto.protocol.exceptions.protocolexceptions.StateException;
import studio.mr.robotto.protocol.servershared.io.ReaderManager;
import studio.mr.robotto.protocol.servershared.io.WriterManager;

/**
 *
 * Created by aaron on 24/02/2015.
 */
public abstract class StateMachine {

    private StateNode mCurrentStateNode;
    private String mCurrentState;
    private String mPreviousState;
    private ProtocolParser mParser;
    private HashMap<String, StateNode> mStateNodes;
    private HashMap<String, Object> mControllers;
    private boolean mParseError;
    private String mCandidateState;
    private StateNode mCandidateStateNode;

    protected StateMachine(String initialState, ProtocolParser parser) {
        mCurrentState = initialState;
        mParser = parser;
        mPreviousState = null;
        mCurrentStateNode = null;
        mStateNodes = new HashMap<String, StateNode>();
        mControllers = new HashMap<String, Object>();
        mParseError = false;
    }

    protected abstract void initializeControllers(final Map<String, Object> controllers);

    protected abstract void initializeStates(final Map<String, StateNode> states);

    public void initialize() {
        initializeControllers(mControllers);
        initializeStates(mStateNodes);
        mCurrentStateNode = getStateNode(mCurrentState);
    }

    public boolean isInFinalState() {
        return mCurrentStateNode.isFinalState();
    }

    public Object getControllerOf(String state) {
        return mControllers.get(state);
    }

    public StateNode getStateNode(String state) {
        return mStateNodes.get(state);
    }

    private void setCurrentNode(StateNode newNode, String newState) {
        mCurrentStateNode = newNode;
        mPreviousState = mCurrentState;
        mCurrentState = newState;
    }

    public void processNext(ReaderManager readerManager, WriterManager writerManager) throws ParseException, ReadException, TimeOutException, CommandException, StateException, WriteException, ApplicationException {
        if (mParseError) {
            Object parsed;
            try {
                parsed = mCandidateStateNode.parseRequestBody(readerManager);
                mParseError = false;
            } catch (ParseException e) {
                throw new TimeOutException();
            }

            mCandidateStateNode.checkPreviousState(mCurrentState);
            setCurrentNode(mCandidateStateNode, mCandidateState);

            Object controller = getControllerOf(mCurrentState);
            mCurrentStateNode.process(writerManager, controller, parsed);
        } else {
            mCandidateState = mParser.getStateFromCommand(readerManager);
            mCandidateStateNode = mStateNodes.get(mCandidateState);
            Object parsed = null;
            try {
                parsed = mCandidateStateNode.parseRequestBody(readerManager);
            } catch (ParseException e) {
                mParseError = true;
                throw new TimeOutException();
            }


            mCandidateStateNode.checkPreviousState(mCurrentState);
            setCurrentNode(mCandidateStateNode, mCandidateState);

            Object controller = getControllerOf(mCurrentState);
            mCurrentStateNode.process(writerManager, controller, parsed);
        }
    }

}
