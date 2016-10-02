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
import net.wpm.codegen.Expression;

/**
 * Defines methods to access a type.
 * 
 * Example
 * VarType(Type.getType(net.wpm.codegen.VarType))
 * writes byte code similar to the java code
 * "net.wpm.codegen.VarType"
 * 
 * Usecase
 *  - access static fields and methods
 *  - access fields of other class
 * 
 */
public final class VarType implements Expression {
		
	private final Type type;
	
	public VarType(Type type) {
		this.type = type;
	}	
	
	@Override
	public Type type(Context ctx) {		
		return type;
	}

	@Override
	public Type load(Context ctx) {
		return type(ctx);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
