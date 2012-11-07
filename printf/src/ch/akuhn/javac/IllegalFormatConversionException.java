/* This file is part of "Printf.jar".
 *
 * "Printf.jar" is free software: you can redistribute it and/or modify
 * it under the terms of the Affero GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Printf.jar" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public License
 * along with "Printf.jar".  If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 */
package ch.akuhn.javac;

import javax.lang.model.type.TypeMirror;

/**
 * Specialized IllegalFormatConversionException that acepts TypeMirrors instead of Classes.
 * 
 * @see FormatChecker
 * @see java.util.IllegalFormatConversionException
 * 
 * @author Adrian Kuhn, Mar 16, 2009
 */
@SuppressWarnings("serial")
public class IllegalFormatConversionException extends java.util.IllegalFormatConversionException {

    private TypeMirror arg;

    public IllegalFormatConversionException(char c, TypeMirror arg) {
        super (c, Void.TYPE);
        this.arg = arg;
    }

    @Override
    public String getMessage() {
        return String.format("%c != %s", getConversion(), arg.toString());
    }
}
