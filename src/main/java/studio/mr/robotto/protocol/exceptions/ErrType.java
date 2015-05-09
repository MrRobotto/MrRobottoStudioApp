/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.exceptions;

/**
 * Created by aaron on 24/02/2015.
 */
public enum ErrType {
    APPLICATION_ERROR,
    READ_ERROR,
    WRITE_ERROR,
    COMMAND_ERROR,
    PARSE_ERROR,
    STATE_ERROR,
    TIMEOUT_ERROR
}
