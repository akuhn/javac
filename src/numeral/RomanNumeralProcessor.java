//  Copyright (c) 2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This is free software: you can redistribute it and/or modify it under the
//  terms of the GNU Lesser General Public License as published by the Free
//  Software Foundation, either version 3 of the License, or (at your option)
//  any later version.
//  
//  This is distributed in the hope that it will be useful, but WITHOUT ANY
//  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with this. If not, see <http://www.gnu.org/licenses/>.
//  

package numeral;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RomanNumeralProcessor extends AbstractProcessor {

	private int tally = 0;

	private class Inliner extends TreeTranslator {

		@Override
		public void visitIdent(JCIdent tree) {
			String name = tree.getName().toString();
			if (isRoman(name)) {
				result = make.Literal(numberize(name));
				result.pos = tree.pos;
				tally++;
			} else {
				super.visitIdent(tree);
			}
		}

	}

	private Trees trees;
	private TreeMaker make;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		make = TreeMaker.instance(((JavacProcessingEnvironment) processingEnv)
				.getContext());
		trees = Trees.instance(processingEnv);
		super.init(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			Set<? extends Element> elements = roundEnv.getRootElements();
			for (Element each : elements) {
				if (each.getKind() == ElementKind.CLASS) {
					JCTree tree = (JCTree) trees.getTree(each);
					TreeTranslator visitor = new Inliner();
					tree.accept(visitor);
				}
			}
		} else
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
					tally + " roman numerals processed.");
		return false;
	}

	private final static String[] LETTERS = { "M", "CM", "D", "CD", "C", "XC",
			"L", "XL", "X", "IX", "V", "IV", "I" };
	private final static int[] VALUES = { 1000, 900, 500, 400, 100, 90, 50, 40,
			10, 9, 5, 4, 1 };

	public final static String romanize(int value) {
		String roman = "";
		int n = value;
		for (int i = 0; i < LETTERS.length; i++) {
			while (n >= VALUES[i]) {
				roman += LETTERS[i];
				n -= VALUES[i];
			}
		}
		return roman;
	}

	public final static int numberize(String roman) {
		int start = 0, value = 0;
		for (int i = 0; i < LETTERS.length; i++) {
			while (roman.startsWith(LETTERS[i], start)) {
				value += VALUES[i];
				start += LETTERS[i].length();
			}
		}
		return start == roman.length() ? value : -1;
	}

	public final static boolean isRoman(String roman) {
		return roman.equals(romanize(numberize(roman)));
	}

}
