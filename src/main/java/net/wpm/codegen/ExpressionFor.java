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

import static net.wpm.codegen.Expressions.add;
import static net.wpm.codegen.Expressions.value;
import static net.wpm.codegen.Utils.newLocal;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import net.wpm.codegen.Context;
import net.wpm.codegen.Expression;
import net.wpm.codegen.ExpressionFor;
import net.wpm.codegen.ForVar;
import net.wpm.codegen.VarLocal;

public class ExpressionFor implements Expression {
	private final Expression length;
	private final Expression start;
	private final ForVar forVar;

	ExpressionFor(Expression length, ForVar forVar) {
		this.length = length;
		this.forVar = forVar;
		this.start = value(0);
	}

	ExpressionFor(Expression start, Expression length, ForVar forVar) {
		this.length = length;
		this.start = start;
		this.forVar = forVar;
	}

	@Override
	public Type type(Context ctx) {
		return Type.VOID_TYPE;
	}

	@Override
	public Type load(Context ctx) {
		GeneratorAdapter g = ctx.getGeneratorAdapter();
		Label labelLoop = new Label();
		Label labelExit = new Label();

		VarLocal len = newLocal(ctx, Type.INT_TYPE);
		add(length, start).load(ctx);
		len.store(ctx);

		start.load(ctx);
		VarLocal varPosition = newLocal(ctx, Type.INT_TYPE);
		varPosition.store(ctx);

		g.mark(labelLoop);

		varPosition.load(ctx);
		len.load(ctx);

		g.ifCmp(Type.INT_TYPE, GeneratorAdapter.GE, labelExit);

		this.forVar.forVar(varPosition).load(ctx);

		varPosition.load(ctx);
		g.push(1);
		g.math(GeneratorAdapter.ADD, Type.INT_TYPE);
		varPosition.store(ctx);

		g.goTo(labelLoop);
		g.mark(labelExit);

		return Type.VOID_TYPE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExpressionFor that = (ExpressionFor) o;

		if (length != null ? !length.equals(that.length) : that.length != null) return false;
		return !(start != null ? !start.equals(that.start) : that.start != null);

	}

	@Override
	public int hashCode() {
		int result = length != null ? length.hashCode() : 0;
		result = 31 * result + (start != null ? start.hashCode() : 0);
		return result;
	}
}