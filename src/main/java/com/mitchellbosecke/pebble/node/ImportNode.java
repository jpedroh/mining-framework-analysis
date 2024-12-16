/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.node;

import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.MacroAttributeProvider;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import java.io.Writer;


public class ImportNode extends AbstractRenderableNode {
    private final Expression<?> importExpression;

    private final String alias;

    public ImportNode(int lineNumber, Expression<?> importExpression, String alias) {
        super(lineNumber);
        this.importExpression = importExpression;
        this.alias = alias;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) {
        self.importTemplate(context, ((String) (importExpression.evaluate(self, context))));
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Expression<?> getImportExpression() {
        return importExpression;
    }
}