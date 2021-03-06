package org.kframework.kil.loader;

import org.kframework.backend.Backend;
import org.kframework.backend.java.kil.KLabel;
import org.kframework.backend.java.kil.Variable;
import org.kframework.compile.transformers.AddSymbolicK;
import org.kframework.compile.utils.MetaK;
import org.kframework.kil.*;
import org.kframework.kil.loader.Context;
import org.kframework.kil.visitors.CopyOnWriteTransformer;
import org.kframework.kil.visitors.exceptions.TransformerException;
import org.kframework.krun.K;

/**
 * Created with IntelliJ IDEA.
 * User: Andrei
 * Date: 14.11.2013
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
public class ResolveVariableAttribute extends CopyOnWriteTransformer {
    public ResolveVariableAttribute(Context context) {
        super("Resolve 'variable' attribute", context);
    }

    @java.lang.Override
    public org.kframework.kil.ASTNode transform(KApp kapp) throws TransformerException {
        if (kapp.getLabel() instanceof Token) {
            Token node = (Token) kapp.getLabel();

            /* this code needs to be refactored; this transformer is backend dependent
            so whenever somebody refactores the backends should split this transformer
            for each backend.
             */
            if (K.backend.equals("maude")) {
                if (context.variableTokenSorts.contains(node.tokenSort())) {
                    String sort = "K";
                    String name = node.value();
                    int index = node.value().lastIndexOf(":");
                    if (index > 0) {
                        sort = node.value().substring(index + 1);
                        name = node.value().substring(0, index);
                    }

                    if (MetaK.isDataSort("#" + sort)) {
                        return KApp.of(KLabelConstant.of(AddSymbolicK.symbolicConstructor(sort)), Token.kAppOf("#Id", name));
                    } else {
                        return KApp.of(KLabelConstant.of(AddSymbolicK.symbolicConstructor("K")), Token.kAppOf("#Id", node.value()));
                    }
                }
            }

            if (context.variableTokenSorts.contains(node.tokenSort()))
                return new org.kframework.kil.Variable(node.value(), node.tokenSort());
        }
        return super.transform(kapp);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
