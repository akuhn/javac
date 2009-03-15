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
