//  Copyright (c) 2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of "fa".
//  
//  "fa" is free software: you can redistribute it and/or modify it under the
//  terms of the GNU Lesser General Public License as published by the Free
//  Software Foundation, either version 3 of the License, or (at your option)
//  any later version.
//  
//  "fa" is distributed in the hope that it will be useful, but WITHOUT ANY
//  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with "fa". If not, see <http://www.gnu.org/licenses/>.
//  

package ch.akuhn.analysis;

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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.tree.JCTree.JCAssert;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.util.List;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class InlineAssertions extends AbstractProcessor {

	private int tally = 0;
	
	private class Inliner extends TreeTranslator {

		@Override
		public void visitAssert(JCAssert tree) {
			super.visitAssert(tree);
			JCStatement newNode = makeIfAssertionThrowException(tree);
			//System.out.println(newNode);
			tally++;
			result = newNode;
		}

		private JCStatement makeIfAssertionThrowException(JCAssert node) {
			// if (!(%%cond%%) throw new AssertionError(%%detail%%);
			List<JCExpression> args = node.getDetail() == null ? List
					.<JCExpression> nil() : List.of(node.detail);
			JCExpression expr = make.NewClass(null, null, make
					.Ident((Symbol) getElement(AssertionError.class)), args, null);
			return make.If(make.Unary(JCTree.NOT, node.cond), make.Throw(expr), null);
		}

	}

	private TypeElement getElement(Class<?> javaClass) {
		for (Element each : elems.getPackageElement(javaClass.getPackage().getName())
				.getEnclosedElements()) {
			if (each.getSimpleName().toString().equals(javaClass.getSimpleName()))
				return (TypeElement) each;
		}
		throw new AssertionError();
	}

	private Trees trees;
	private TreeMaker make;
	private Elements elems;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		make = TreeMaker.instance(((JavacProcessingEnvironment) processingEnv)
				.getContext());
		elems = processingEnv.getElementUtils();
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
		}
		else processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
				tally + " assertions inlined.");
		return false;
	}

	public static void main(String[] args) {
		Runnable runner = new ProcRunner(new InlineAssertions());
		runner.run();
	}

}
