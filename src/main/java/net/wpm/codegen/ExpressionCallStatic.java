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
import org.objectweb.asm.commons.GeneratorAdapter;

import net.wpm.codegen.Context;
import net.wpm.codegen.Expression;
import net.wpm.codegen.ExpressionCallStatic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static net.wpm.codegen.Utils.argsToString;
import static net.wpm.codegen.Utils.exceptionInGeneratedClass;
import static net.wpm.codegen.Utils.getJavaType;
import static org.objectweb.asm.Type.getType;

public class ExpressionCallStatic implements Expression {
	private final Class<?> owner;
	private final String name;
	private final List<Expression> arguments = new ArrayList<Expression>();

	ExpressionCallStatic(Class<?> owner, String name, Expression... arguments) {
		this.owner = owner;
		this.name = name;
		this.arguments.addAll(Arrays.asList(arguments));
	}

	@Override
	public Type type(Context ctx) {
		List<Class<?>> argumentClasses = new ArrayList<Class<?>>();
		List<Type> argumentTypes = new ArrayList<Type>();
		for (Expression argument : arguments) {
			argumentTypes.add(argument.type(ctx));
			argumentClasses.add(getJavaType(ctx.getClassLoader(), argument.type(ctx)));
		}

		Class<?>[] arguments = argumentClasses.toArray(new Class<?>[]{});

		Type returnType;
		Method method;
		try {
			// TODO support for methods in super class
			try {
				method = owner.getMethod(name, arguments);
			}catch (NoSuchMethodException e) {				
				method = owner.getDeclaredMethod(name, arguments);
			}
			Class<?> returnClass = method.getReturnType();
			returnType = getType(returnClass);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(format("No static method %s.%s(%s). %s",
					owner.getName(),
					name,
					(!argumentClasses.isEmpty() ? argsToString(argumentClasses) : ""),
					exceptionInGeneratedClass(ctx)));
		}

		return returnType;
	}

	@Override
	public Type load(Context ctx) {
		List<Class<?>> argumentClasses = new ArrayList<Class<?>>();
		List<Type> argumentTypes = new ArrayList<Type>();
		for (Expression argument : arguments) {
			argument.load(ctx);
			argumentTypes.add(argument.type(ctx));
			if (argument.type(ctx).equals(getType(Object[].class))) {
				argumentClasses.add(Object[].class);
			} else {
				argumentClasses.add(getJavaType(ctx.getClassLoader(), argument.type(ctx)));
			}
		}

		Class<?>[] arguments = argumentClasses.toArray(new Class<?>[]{});
		Type returnType;
		Method method;
		try {
			Class<?> ownerJavaType = getJavaType(ctx.getClassLoader(), Type.getType(owner));
			
			// TODO support for methods in super class
			try {
				method = ownerJavaType.getMethod(name, arguments);
			}catch (NoSuchMethodException e) {				
				method = ownerJavaType.getDeclaredMethod(name, arguments);
			}
			Class<?> returnClass = method.getReturnType();
			returnType = getType(returnClass);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(format("No static method %s.%s(%s). %s",
					owner.getName(),
					name,
					(!argumentClasses.isEmpty() ? argsToString(argumentClasses) : ""),
					exceptionInGeneratedClass(ctx)));

		}
		GeneratorAdapter g = ctx.getGeneratorAdapter();
		g.invokeStatic(Type.getType(owner), org.objectweb.asm.commons.Method.getMethod(method));
		return returnType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ExpressionCallStatic that = (ExpressionCallStatic) o;

		if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		return !(arguments != null ? !arguments.equals(that.arguments) : that.arguments != null);

	}

	@Override
	public int hashCode() {
		int result = owner != null ? owner.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
		return result;
	}
}