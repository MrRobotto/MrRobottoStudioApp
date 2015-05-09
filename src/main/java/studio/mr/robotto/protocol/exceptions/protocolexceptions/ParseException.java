/*
 * MrRobotto Engine
 * Copyright (c) 2015, Aarón Negrín, All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package studio.mr.robotto.protocol.exceptions.protocolexceptions;

import studio.mr.robotto.protocol.exceptions.ErrType;
import studio.mr.robotto.protocol.exceptions.ServerException;

/**
 * Created by aaron on 24/02/2015.
 */
public class ParseException extends ServerException {
    public ParseException(String message) {
        super(ErrType.PARSE_ERROR, message);
    }

    public ParseException() {
        super(ErrType.PARSE_ERROR, "Can't parse the element");
    }
}
