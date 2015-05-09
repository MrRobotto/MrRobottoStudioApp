/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.exceptions.connectionexceptions;


import studio.mr.robotto.protocol.exceptions.ErrType;
import studio.mr.robotto.protocol.exceptions.ServerException;

/**
 * Created by aaron on 12/03/2015.
 */
public class TimeOutException extends ServerException {
    public TimeOutException() {
        super(ErrType.TIMEOUT_ERROR, "Connection without activity, close");
    }
}
