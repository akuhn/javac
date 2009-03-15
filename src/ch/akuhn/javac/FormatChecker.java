package ch.akuhn.javac;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.MissingFormatArgumentException;

import javax.lang.model.type.TypeMirror;

import ch.akuhn.javac.Printf.PrintfVisitor;

/**
 * Checks the format of format strings.
 * <p>
 * Reuses as much of java.util.Formatter as possible.
 * The private method Formatter#parse(String) is called using reflection,
 * and the returned FormatString instances are wrapped into FormatStringWrapper
 * instances that use reflection to access private members of FormatString.
 * This class's method #verify(String,TypeMirror...) is copypasta of Formatter#format(String,Object...),
 * in the same way the #verify* methods of the FormatStringWrapper are copypasta of the #print* methods
 * of Formatter's FormatSpecifier. All printing has been removed and instanceof checks been replaces with
 * assignability checks using type mirrors. 
 * 
 * @see Formatter
 * @see Printf
 * 
 * @author Adrian Kuhn, Mar 16, 2009
 *
 */
public class FormatChecker {

    private final PrintfVisitor printf;

    public FormatChecker(PrintfVisitor printf) {
        this.printf = printf;
    }

    public void verify(String format, TypeMirror... args) {
        int last = -1;
        int lasto = -1;
        List<FormatStringWrapper> list = formatterParse(format);
        for (FormatStringWrapper fs: list) {
            int index = fs.index();
            switch (index) {
            case -2: // fixed string, "%n", or "%%"
                break;
            case -1: // relative index
                if (last < 0 || (args != null && last > args.length - 1)) {
                    throw new MissingFormatArgumentException(fs.toString());
                }
                fs.verify((args == null ? null : args[last]));
                break;
            case 0: // ordinary index
                lasto++;
                last = lasto;
                if (args != null && lasto > args.length - 1) {
                    throw new MissingFormatArgumentException(fs.toString());
                }
                fs.verify((args == null ? null : args[lasto]));
                break;
            default: // explicit index
                last = index - 1;
                if (args != null && last > args.length - 1) {
                    throw new MissingFormatArgumentException(fs.toString());
                }
                fs.verify((args == null ? null : args[last]));
                break;
            }
        }
    }

    private List<FormatStringWrapper> formatterParse(String format) {
        Object[] array = AllYourPrivateAreBelongToUs.<Object[]>invokeMethod(new Formatter(), "parse", String.class,
                format);
        List<FormatStringWrapper> list = new ArrayList<FormatStringWrapper>();
        for (Object each: array)
            list.add(new FormatStringWrapper(each));
        return list;
    }

    public class FormatStringWrapper {

        private Object formatString;

        public FormatStringWrapper(Object formatString) {
            this.formatString = formatString;
        }

        @Override
        public String toString() {
            return formatString.toString();
        }

        public int index() {
            return AllYourPrivateAreBelongToUs.<Integer>invokeMethod(formatString, "index");
        }

        public Void verify(TypeMirror arg) {
            if (dt()) return verifyDateTime(arg);
            switch (c()) {
            case Conversion.DECIMAL_INTEGER:
            case Conversion.OCTAL_INTEGER:
            case Conversion.HEXADECIMAL_INTEGER:
                return verifyInteger(arg);
            case Conversion.SCIENTIFIC:
            case Conversion.GENERAL:
            case Conversion.DECIMAL_FLOAT:
            case Conversion.HEXADECIMAL_FLOAT:
                return verifyFloat(arg);
            case Conversion.CHARACTER:
            case Conversion.CHARACTER_UPPER:
                return verifyCharacter(arg);
            case Conversion.BOOLEAN:
                return verifyBoolean(arg);
            case Conversion.STRING:
                return verifyString(arg);
            case Conversion.HASHCODE:
                return verifyHashCode(arg);
            case Conversion.LINE_SEPARATOR:
                return null;
            case Conversion.PERCENT_SIGN:
                return null;
            default:
                throw new AssertionError();
            }
        }

        private char c() {
            return AllYourPrivateAreBelongToUs.<Character>getField(formatString, "c");
        }

        private boolean dt() {
            return AllYourPrivateAreBelongToUs.<Boolean>getField(formatString, "dt");
        }

        private Void verifyInteger(TypeMirror arg) {
            if (printf.isAssignable(arg, Byte.class)) {
                return null;
            } else if (printf.isAssignable(arg, Short.class)) {
                return null;
            } else if (printf.isAssignable(arg, Integer.class)) {
                return null;
            } else if (printf.isAssignable(arg, Long.class)) {
                return null;
            } else if (printf.isAssignable(arg, BigInteger.class)) {
                return null;
            } else return failConversion(c(), arg);
        }

        private Void verifyFloat(TypeMirror arg) {
            if (printf.isAssignable(arg, Float.class)) {
                return null;
            } else if (printf.isAssignable(arg, Double.class)) {
                return null;
            } else if (printf.isAssignable(arg, BigInteger.class)) {
                return null;
            } else return failConversion(c(), arg);
        }

        private Void verifyDateTime(TypeMirror arg) {
            if (printf.isAssignable(arg, Long.class)) {
                return null;
            } else if (printf.isAssignable(arg, Date.class)) {
                return null;
            } else if (printf.isAssignable(arg, Calendar.class)) {
                return null;
            } else {
                return failConversion(c(), arg);
            }
        }

        private Void verifyCharacter(TypeMirror arg) {
            if (printf.isAssignable(arg, Character.class)) {
                return null;
            } else if (printf.isAssignable(arg, Byte.class)) {
                return null;
            } else if (printf.isAssignable(arg, Short.class)) {
                return null;
            } else if (printf.isAssignable(arg, Integer.class)) {
                return null;
            } else {
                return failConversion(c(), arg);
            }
        }

        private Void verifyString(TypeMirror arg) {
            return null;
        }

        private Void verifyBoolean(TypeMirror arg) {
            return null;
        }

        private Void verifyHashCode(TypeMirror arg) {
            return null;
        }

        private Void failConversion(char c, TypeMirror arg) {
            throw new IllegalFormatConversionException(c, arg);
        }

    }

    private static class Conversion {
        // Byte, Short, Integer, Long, BigInteger
        // (and associated primitives due to autoboxing)
        static final char DECIMAL_INTEGER = 'd';
        static final char OCTAL_INTEGER = 'o';
        static final char HEXADECIMAL_INTEGER = 'x';
        static final char HEXADECIMAL_INTEGER_UPPER = 'X';

        // Float, Double, BigDecimal
        // (and associated primitives due to autoboxing)
        static final char SCIENTIFIC = 'e';
        static final char SCIENTIFIC_UPPER = 'E';
        static final char GENERAL = 'g';
        static final char GENERAL_UPPER = 'G';
        static final char DECIMAL_FLOAT = 'f';
        static final char HEXADECIMAL_FLOAT = 'a';
        static final char HEXADECIMAL_FLOAT_UPPER = 'A';

        // Character, Byte, Short, Integer
        // (and associated primitives due to autoboxing)
        static final char CHARACTER = 'c';
        static final char CHARACTER_UPPER = 'C';

        // java.util.Date, java.util.Calendar, long
        static final char DATE_TIME = 't';
        static final char DATE_TIME_UPPER = 'T';

        // if (arg.TYPE != boolean) return boolean
        // if (arg != null) return true; else return false;
        static final char BOOLEAN = 'b';
        static final char BOOLEAN_UPPER = 'B';
        // if (arg instanceof Formattable) arg.formatTo()
        // else arg.toString();
        static final char STRING = 's';
        static final char STRING_UPPER = 'S';
        // arg.hashCode()
        static final char HASHCODE = 'h';
        static final char HASHCODE_UPPER = 'H';

        static final char LINE_SEPARATOR = 'n';
        static final char PERCENT_SIGN = '%';

    }

}
