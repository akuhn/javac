package ch.akuhn.javac;

import javax.lang.model.type.TypeMirror;

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
