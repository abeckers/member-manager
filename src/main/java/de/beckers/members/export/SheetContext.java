package de.beckers.members.export;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.joda.time.LocalDate;

import de.beckers.members.model.Person;
import de.beckers.members.repository.RelationRepository;
import lombok.Data;

@Data
public class SheetContext {
	private XSSFSheet sheet;

	private SheetDescriptor desc;

	private int currentRow = 0;

	private CreationHelper createHelper;

	private CellStyle style;

	private XSSFCell currentCell;
	
	private RelationRepository relRep;

	public XSSFRow createRow() {
		currentRow++;
		XSSFRow row = sheet.createRow(currentRow);
		row.createCell(0).setCellValue(currentRow);
		return row;
	}

	public void initSheet() {
		sheet.getHeader().setLeft(desc.getHeaderLeft());
		sheet.getHeader().setRight(desc.getHeaderRight());
		sheet.getFooter().setRight(desc.getFooterRight());

		XSSFRow header = sheet.createRow(0);
		AtomicInteger i = new AtomicInteger();
		desc.getColumns().forEach(c -> header.createCell(i.getAndIncrement()).setCellValue(c.getLabel()));
		i.set(0);
		desc.getColumns().forEach(c -> sheet.setColumnWidth(i.getAndIncrement(), c.getWidth()));
	}

	public XSSFRow createRow(Person person) {
		XSSFRow row = createRow();
		AtomicInteger i = new AtomicInteger(0);
		desc.getColumns().forEach(c -> addCellValue(c, person, row, i.getAndIncrement()));
		return row;
	}

	private void addCellValue(ColumnDescriptor c, Person person, XSSFRow row, int idx) {
		Function<Person, Object> value = c.getValue();
		if (value != null) {
			Object v = value.apply(person);
			currentCell = row.createCell(idx);
			BiConsumer<SheetContext, Object> decorator = c.getDecorator();
			if (decorator != null) {
				decorator.accept(this, v);
			} else if (v instanceof String) {
				currentCell.setCellValue(v.toString());
			} else if (v instanceof Date) {
				currentCell.setCellStyle(style);
				currentCell.setCellValue((Date) v);
			} else if (v instanceof LocalDate) {
				currentCell.setCellStyle(style);
				currentCell.setCellValue(((LocalDate) v).toDate());
			}
		}
	}
}
