/*
 * Copyright (C) 2015 SoftIndex LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wpm.codegen;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import net.wpm.codegen.Context;
import net.wpm.codegen.Expression;
import net.wpm.codegen.ExpressionComparatorNullable;
import net.wpm.codegen.VarLocal;
import net.wpm.codegen.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;

import static net.wpm.codegen.Utils.*;
import static org.objectweb.asm.Type.INT_TYPE;
import static org.objectweb.asm.commons.GeneratorAdapter.NE;

public final class ExpressionComparatorNullable implements Expression {
	private final List<Expression> left = new ArrayList<Expression>();
	private final List<Expression> right = new ArrayList<Expression>();

	ExpressionComparatorNullable() {
	}

	ExpressionComparatorNullable(List<Expression> left, List<Expression> right) {
		Preconditions.check(left.size() == right.size());
		this.left.addAll(left);
		this.right.addAll(right);
	}

	public ExpressionComparatorNullable add(Expression left, Expression right) {
		this.left.add(left);
		this.right.add(right);
		return this;
	}

	@Override
	public Type type(Context ctx) {
		return INT_TYPE;
	}

	@Override
	public Type load(Context ctx) {
		GeneratorAdapter g = ctx.getGeneratorAdapter();

		Label labelReturn = new Label();

		for (int i = 0; i < left.size(); i++) {
			Type leftFieldType = left.get(i).load(ctx); // [left]
			Type rightFieldType = right.get(i).load(ctx); // [left, right]

			Preconditions.check(leftFieldType.equals(rightFieldType));
			if (isPrimitiveType(leftFieldType)) {
				g.invokeStatic(wrap(leftFieldType), new Method("compare", INT_TYPE, new Type[]{leftFieldType, leftFieldType}));
				g.dup();
				g.ifZCmp(NE, labelReturn);
				g.pop();
			} else {
				VarLocal varRight = newLocal(ctx, rightFieldType);
				varRight.store(ctx);

				VarLocal varLeft = newLocal(ctx, leftFieldType);
				varLeft.store(ctx);

				Label continueLabel = new Label();
				Label nonNulls = new Label();
				Label leftNonNull = new Label();

				varLeft.load(ctx);
				g.ifNonNull(leftNonNull);

				varRight.load(ctx);
				g.ifNull(continueLabel);
				g.push(-1);
				g.returnValue();

				g.mark(leftNonNull);

				varRight.load(ctx);
				g.ifNonNull(nonNulls);
				g.push(1);
				g.returnValue();

				g.mark(nonNulls);

				varLeft.load(ctx);
				varRight.load(ctx);

				g.invokeVirtual(leftFieldType, new Method("compareTo", INT_TYPE, new Type[]{Type.getType(Object.class)}));
				g.dup();
				g.ifZCmp(NE, labelReturn);
				g.pop();

				g.mark(continueLabel);
			}
		}

		g.push(0);

		g.mark(labelReturn);

		return INT_TYPE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExpressionComparatorNullable that = (ExpressionComparatorNullable) o;

		if (left != null ? !left.equals(that.left) : that.left != null) return false;
		if (right != null ? !right.equals(that.right) : that.right != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = left != null ? left.hashCode() : 0;
		result = 31 * result + (right != null ? right.hashCode() : 0);
		return result;
	}
}
