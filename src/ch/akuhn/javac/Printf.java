package ch.akuhn.javac;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.MissingFormatArgumentException;
import java.util.Set;
import java.util.UnknownFormatConversionException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol.ClassSymbol;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Printf extends AbstractProcessor {

    public class PrintfVisitor extends TreeScanner<Void,Void> {

        private Name printf = elements.getName("printf");
        private Name format = elements.getName("format");
        
        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree0, Void unused) {
            JCMethodInvocation tree = (JCMethodInvocation) tree0;
            if (isPrintf(tree)) verifyPrintf(tree);
            return super.visitMethodInvocation(tree, unused);
        }

        public boolean isAssignable(TypeMirror type0, Class<?> jClass) {
            TypeMirror type = elements.getTypeElement(jClass.getName()).asType();
            if (!types.isAssignable(type0, type)) return false;
            return true;
        }
        
        private void verifyPrintf(JCMethodInvocation tree) {
            String format = (String) tree.args.head.type.constValue();
            try {
                new FormatChecker(this).verify(format, types(tree.args.tail));
            } 
            catch (IllegalFormatException ex) {
                log.rawWarning(tree.pos, ex.toString());
            }
        }

        private TypeMirror[] types(List<JCExpression> tail) {
            ArrayList<TypeMirror> types = new ArrayList<TypeMirror>();
            for (JCExpression each: tail) types.add(each.type);
            return types.toArray(new TypeMirror[types.size()]);
        }

        private boolean isPrintf(JCMethodInvocation tree) {
            Name name = TreeInfo.name(tree.meth);
            return (name == printf || name == format)
                    && tree.args.size() > 1
                    && tree.args.head.type.constValue() instanceof String;
        }

    }

    private Trees trees;
    private Attr attr;
    private Elements elements;
    private Types types;
    private Log log;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        attr = Attr.instance(((JavacProcessingEnvironment) processingEnv).getContext());
        log = Log.instance(((JavacProcessingEnvironment) processingEnv).getContext());
        trees = Trees.instance(processingEnv);
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            Set<? extends Element> elements = roundEnv.getRootElements();
            for (Element each: elements) {
                if (each.getKind() == ElementKind.CLASS) {
                    setCurrentSource(each);
                    attr.attribClass(null, (ClassSymbol) each);
                    Tree tree = trees.getTree(each);
                    verify(tree);
                }
            }
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Printf done.");
        }
        return false;
    }

    private void setCurrentSource(Element each) {
        log.useSource(((JCCompilationUnit) trees.getPath(each).getCompilationUnit()).sourcefile);
    }

    private void verify(Tree tree) {
        TreeVisitor<Void,Void> visitor = new PrintfVisitor();
        tree.accept(visitor, null);
    }
    
}
