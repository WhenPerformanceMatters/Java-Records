package net.wpm.record.bytecode.template;

import java.util.Collections;

import net.wpm.codegen.AsmBuilder;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateGetBlueprintId extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetBlueprintId(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {
		builder.method(blueprintMethod.getName(), int.class, Collections.EMPTY_LIST, blueprintId());			
	}	
}
