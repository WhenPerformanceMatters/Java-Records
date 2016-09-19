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

import org.objectweb.asm.Type;

import net.wpm.codegen.Context;
import net.wpm.codegen.VarArg;
import net.wpm.codegen.Variable;

/**
 * Defines method which allow to take argument according to their ordinal number
 */
public final class VarArg implements Variable {
	private final int argument;

	VarArg(int argument) {
		this.argument = argument;
	}

	@Override
	public Type type(Context ctx) {
		return ctx.getArgumentType(argument);
	}

	@Override
	public Type load(Context ctx) {
		ctx.getGeneratorAdapter().loadArg(argument);
		return type(ctx);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VarArg varArg = (VarArg) o;

		return argument == varArg.argument;
	}

	@Override
	public int hashCode() {
		return argument;
	}

	@Override
	public Object beginStore(Context ctx) {
		return null;
	}

	@Override
	public void store(Context ctx, Object storeContext, Type type) {
		ctx.getGeneratorAdapter().storeArg(argument);
	}
}
