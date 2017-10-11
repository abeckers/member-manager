package de.beckers.members.export;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Builder
@Value
public class SheetDescriptor {
	private String sheetName;
	
	@Singular
	private List<ColumnDescriptor> columns;
	
	private String headerLeft;
	
	private String headerRight;
	
	private String footerLeft;
	
	private String footerRight;
}
