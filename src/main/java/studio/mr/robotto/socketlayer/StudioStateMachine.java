/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.socketlayer;

import java.util.Map;

import studio.mr.robotto.protocol.constants.States;
import studio.mr.robotto.protocol.servershared.statemachine.ProtocolParser;
import studio.mr.robotto.protocol.servershared.statemachine.StateMachine;
import studio.mr.robotto.protocol.servershared.statemachine.StateNode;
import studio.mr.robotto.protocol.states.UpdateState;
import studio.mr.robotto.protocol.states.VoidState;

/**
 * Created by aaron on 08/05/2015.
 */
public class StudioStateMachine extends StateMachine {
    protected StudioStateMachine(String initialState, ProtocolParser parser) {
        super(initialState, parser);
    }

    @Override
    protected void initializeControllers(Map<String, Object> controllers) {
        controllers.put(States.VOID_STATE, null);
        controllers.put(States.UPDT_STATE, null);
    }

    @Override
    protected void initializeStates(Map<String, StateNode> states) {
        states.put(States.VOID_STATE, new VoidState());
        states.put(States.UPDT_STATE, new UpdateState());
    }
}
