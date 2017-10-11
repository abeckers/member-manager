package de.beckers.members.export;

import java.util.function.BiConsumer;
import java.util.function.Function;

import de.beckers.members.model.Person;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ColumnDescriptor {
	private String label;
	
	private int width;
	
	private Function<Person, Object> value;
	
	private BiConsumer<SheetContext, Object> decorator;
}
