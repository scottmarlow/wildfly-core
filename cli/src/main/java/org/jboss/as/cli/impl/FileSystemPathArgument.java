/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.cli.impl;

import org.jboss.as.cli.CommandFormatException;
import org.jboss.as.cli.Util;
import org.jboss.as.cli.handlers.CommandHandlerWithArguments;
import org.jboss.as.cli.handlers.FilenameTabCompleter;
import org.jboss.as.cli.operation.ParsedCommandLine;
import org.jboss.as.cli.parsing.ExpressionBaseState;
import org.jboss.as.cli.parsing.ParsingState;
import org.jboss.as.cli.parsing.WordCharacterHandler;

/**
*
* @author Alexey Loubyansky
*/
public class FileSystemPathArgument extends ArgumentWithValue {

    private final FilenameTabCompleter completer;

    public FileSystemPathArgument(CommandHandlerWithArguments handler, FilenameTabCompleter completer, int index, String name) {
        super(handler, completer, index, name);
        this.completer = completer;
    }

    public FileSystemPathArgument(CommandHandlerWithArguments handler, FilenameTabCompleter completer, String name) {
        super(handler, completer, name);
        this.completer = completer;
    }

    @Override
    protected ParsingState initParsingState() {
        final ExpressionBaseState state = new ExpressionBaseState("EXPR", true, false);
        if(Util.isWindows()) {
            // to not require escaping FS name separator
            state.setDefaultHandler(WordCharacterHandler.IGNORE_LB_ESCAPE_OFF);
        } else {
            state.setDefaultHandler(WordCharacterHandler.IGNORE_LB_ESCAPE_ON);
        }
        return state;
    }

    @Override
    public String getValue(ParsedCommandLine args, boolean required) throws CommandFormatException {
        return translatePath(super.getValue(args, required));
    }

    private String translatePath(String value) {
        if(value != null) {
            if(value.length() >= 0 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                value = value.substring(1, value.length() - 1);
            }
            if(completer != null) {
                value = completer.translatePath(value);
            }
        }
        return value;
    }
}
