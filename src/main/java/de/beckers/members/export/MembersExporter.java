package de.beckers.members.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.beckers.members.model.MemberRole;
import de.beckers.members.model.Person;
import de.beckers.members.repository.PersonRepository;

@Component
@Transactional
public class MembersExporter extends ExporterBase {
	@Autowired
	private PersonRepository perRep;

	public void export(OutputStream out, boolean player, boolean coach, boolean staff, boolean inactive, boolean noRole,
			boolean parents) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		exportPlayers(wb, player);
		exportCoached(wb, coach);
		exportStaff(wb, staff);
		exportInactive(wb, inactive);
		exportNoRole(wb, noRole);
		exportParents(wb, parents);

		wb.write(out);
	}

	private void exportPersons(XSSFWorkbook wb, boolean enabled, SheetDescriptor desc,
			Function<Pageable, Page<Person>> supplier) {
		if (enabled) {
			XSSFSheet sheet = wb.createSheet(desc.getSheetName());
			SheetContext ctx = new SheetContext();
			ctx.setDesc(desc);
			ctx.setSheet(sheet);
			ctx.setCreateHelper(wb.getCreationHelper());
			ctx.setStyle(wb.createCellStyle());
			ctx.initSheet();

			Page<Person> page = null;
			ctx.getStyle().setDataFormat(ctx.getCreateHelper().createDataFormat().getFormat("dd.mm.yyyy"));
			int p = 0;
			Pageable pageRequest = new PageRequest(p, PAGE_SIZE, MEMBER_SORT);
			do {
				page = supplier.apply(pageRequest);
				for (Person person : page.getContent()) {
					ctx.createRow(person);
				}
				pageRequest = page.nextPageable();
			} while (page.hasNext());
		}
	}

	private void exportCoached(XSSFWorkbook wb, boolean coach) {
		exportPersons(wb, coach, COACHES_DESCRIPTOR, p -> perRep.findByRole(MemberRole.COACH, p));
	}

	private void exportStaff(XSSFWorkbook wb, boolean enabled) {
		exportPersons(wb, enabled, STAFF_DESCRIPTOR, p -> perRep.findByRole(MemberRole.STAFF, p));
	}

	private void exportPlayers(XSSFWorkbook wb, boolean player) {
		exportPersons(wb, player, PLAYER_DESCRIPTOR, p -> perRep.findActive(p));
	}

	private void exportParents(XSSFWorkbook wb, boolean player) {
		exportPersons(wb, player, PARENT_DESCRIPTOR, p -> perRep.findParents(p));
	}

	private void exportInactive(XSSFWorkbook wb, boolean player) {
		exportPersons(wb, player, INACTIVE_DESCRIPTOR, p -> perRep.findInActive(p));
	}

	private void exportNoRole(XSSFWorkbook wb, boolean player) {
		exportPersons(wb, player, NO_ROLE_DESCRIPTOR, p -> perRep.findNoRole(p));
	}
}
