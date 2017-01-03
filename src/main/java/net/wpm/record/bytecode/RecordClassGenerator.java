package net.wpm.record.bytecode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.wpm.codegen.ClassBuilder;
import net.wpm.codegen.utils.DefiningClassLoader;
import net.wpm.record.RecordView;
import net.wpm.record.blueprint.BlueprintClass;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;
import net.wpm.record.bytecode.template.ASMTemplate;
import net.wpm.record.bytecode.template.TemplateCopy;
import net.wpm.record.bytecode.template.TemplateCopyFrom;
import net.wpm.record.bytecode.template.TemplateDecreaseValue;
import net.wpm.record.bytecode.template.TemplateDecreaseValueBy;
import net.wpm.record.bytecode.template.TemplateGetArraySize;
import net.wpm.record.bytecode.template.TemplateGetBlueprintId;
import net.wpm.record.bytecode.template.TemplateGetRecordId;
import net.wpm.record.bytecode.template.TemplateGetRecordSize;
import net.wpm.record.bytecode.template.TemplateGetSequence;
import net.wpm.record.bytecode.template.TemplateGetValue;
import net.wpm.record.bytecode.template.TemplateGetValueAt;
import net.wpm.record.bytecode.template.TemplateGetValueWith;
import net.wpm.record.bytecode.template.TemplateGetValueWithAt;
import net.wpm.record.bytecode.template.TemplateIncreaseValue;
import net.wpm.record.bytecode.template.TemplateIncreaseValueBy;
import net.wpm.record.bytecode.template.TemplateRecord;
import net.wpm.record.bytecode.template.TemplateSetRecordId;
import net.wpm.record.bytecode.template.TemplateSetValue;
import net.wpm.record.bytecode.template.TemplateSetValueAt;
import net.wpm.record.bytecode.template.TemplateToString;
import net.wpm.record.bytecode.template.TemplateView;
import net.wpm.record.bytecode.template.TemplateViewAt;
import net.wpm.record.exception.RecordClassException;

/**
 * Bytecode Engineering
 * https://dzone.com/articles/byte-code-engineering-1
 * 
 * codegen 
 * http://datakernel.io/docs/codegen/dynamic-class-creation.html
 * 
 * asm
 * http://asm.ow2.org/
 * https://github.com/m2spring/asm-eval
 * 
 * javassist
 * http://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#limit
 * 
 * Commons BCEL
 * https://commons.apache.org/proper/commons-bcel/
 * https://www.ibm.com/developerworks/library/j-dyn0610/
 * 
 * @author Nico
 *
 */
public class RecordClassGenerator {
	
	// where to store the generated byte code
	protected static final Path byteCodePath = Paths.get("generated");
	protected static DefiningClassLoader classLoader = new DefiningClassLoader();
	
	static {
		try {
			if(Files.exists(byteCodePath) == false)
				Files.createDirectories(byteCodePath);
		} catch (IOException e) {
			throw new RuntimeException("Could not create 'generated' directory to store generated bytecode.", e);
		}
	}
	
	// contains information about the methods of the blueprint
	protected final BlueprintClass blueprintClass;

	public RecordClassGenerator(final BlueprintClass blueprintClass) {		
		this.blueprintClass = blueprintClass;
	}
	
	public Class<RecordView> construct() {
		
		// all blueprints for this record
		List<Class<?>> blueprints = new ArrayList<Class<?>>();
		blueprints.add(blueprintClass.getBlueprint());

		// construct a class that implements the blueprints
		ClassBuilder<RecordView> builder = new ClassBuilder<RecordView>(classLoader, RecordView.class, blueprints).setBytecodeSaveDir(byteCodePath);
		
		// all methods and fields necessary to work as a record
		addRecordViewTrait(builder, blueprintClass.getSizeInBytes());

		// implement the necessary and methods
		implementMethods(builder, blueprintClass.getBlueprint(), blueprintClass.getMethods());
				
		// to String method
		addToString(builder, blueprintClass.isCustomToString(), blueprintClass.getVariables());		
				
		String className = blueprintClass.getBlueprint().getName() + "RecordView";
		return builder.build(className);
	}	
	
	
	/**
	 * Implements methods and fields necessary for a record view 
	 * 
	 * @param builder
	 * @param sizeInBytes
	 */
	private static void addRecordViewTrait(ClassBuilder<RecordView> builder, int sizeInBytes) {
		ASMTemplate template = new TemplateRecord(sizeInBytes);
		template.addBytecode(builder);			
	}

	/**
	 * Implements the toString method of the new class
	 * 
	 * @param builder
	 * @param customToStringMethod
	 * @param variables
	 */
	protected static void addToString(ClassBuilder<RecordView> builder, boolean customToStringMethod, Collection<BlueprintVariable> variables) {
		ASMTemplate template = new TemplateToString(variables, customToStringMethod);
		template.addBytecode(builder);
	}
	
	
	/**
	 * Implements all the methods required by the blueprint.
	 *  
	 * @param builder
	 */
	protected static void implementMethods(ClassBuilder<RecordView> builder, Class<?> blueprintClass, Collection<BlueprintMethod> methods) {
		
		// all methods that need to be implemented
		for (BlueprintMethod method : methods) {
			ASMTemplate template = getASMTemplate(blueprintClass, method);
			if(template == null)
				throw new RecordClassException("Could not find template to implement "+method);
			
			// implement the method
			template.addBytecode(builder);
		}
	}
	
	/**
	 * Get a ASMTemplate to implement the BlueprintMethod.
	 * 
	 * @param method
	 * @return
	 */
	protected static ASMTemplate getASMTemplate(Class<?> blueprintClass, BlueprintMethod method) {
		switch (method.getActionType()) {
			case GetValue:
				return new TemplateGetValue(method);
			case GetValueAt:
				return new TemplateGetValueAt(method);
			case SetValue:
				return new TemplateSetValue(method);
			case SetValueAt:
				return new TemplateSetValueAt(method);
			case IncreaseValue:
				return new TemplateIncreaseValue(method);
			case IncreaseValueBy:
				return new TemplateIncreaseValueBy(method);
			case DecreaseValue:
				return new TemplateDecreaseValue(method);
			case DecreaseValueBy:
				return new TemplateDecreaseValueBy(method);
			case GetArraySize:
				return new TemplateGetArraySize(method);
			case GetSequence:
				return new TemplateGetSequence(method);
				
			case GetValueWith:
				return new TemplateGetValueWith(method);
			case GetValueWithAt:
				return new TemplateGetValueWithAt(method);		
				
			case GetRecordId:
				return new TemplateGetRecordId(method);
			case SetRecordId:
				return new TemplateSetRecordId(method);
			case GetBlueprintId:
				return new TemplateGetBlueprintId(method);
			case GetRecordSize:
				return new TemplateGetRecordSize(method);
			case Copy:
				return new TemplateCopy(method, blueprintClass);
			case CopyFrom:
				return new TemplateCopyFrom(method, blueprintClass);
			case View:
				return new TemplateView(method, blueprintClass);
			case ViewAt:
				return new TemplateViewAt(method, blueprintClass);
			default:
				return null;
		}
	}
	

	/**
	 * Converts the first letter to upper case.
	 * 
	 * @param text
	 * @return String
	 */
	public static String capitalize(final String text) {
	   return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}
}