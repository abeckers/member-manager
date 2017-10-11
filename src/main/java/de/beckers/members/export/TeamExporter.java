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
import de.beckers.members.model.Team;
import de.beckers.members.repository.PersonRepository;
import de.beckers.members.repository.RelationRepository;

@Component
@Transactional
public class TeamExporter extends ExporterBase {
	@Autowired
	private PersonRepository perRep;
	
	@Autowired
	private RelationRepository relRep;

	public void export(OutputStream out, Team team, boolean player, boolean coach, boolean staff, boolean inactive,
			boolean parents) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		exportPlayers(wb, team, player);
		exportCoached(wb, team, coach);
		exportStaff(wb, team, staff);
		exportInactive(wb, team, inactive);
		exportParents(wb, team, parents);

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
			ctx.setRelRep(relRep);
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

	private void exportCoached(XSSFWorkbook wb, Team team, boolean coach) {
		exportPersons(wb, coach, COACHES_DESCRIPTOR, p -> perRep.findInTeamByRole(team.getId(), MemberRole.COACH, p));
	}

	private void exportStaff(XSSFWorkbook wb, Team team, boolean enabled) {
		exportPersons(wb, enabled, STAFF_DESCRIPTOR, p -> perRep.findInTeamByRole(team.getId(), MemberRole.STAFF, p));
	}

	private void exportPlayers(XSSFWorkbook wb, Team team, boolean enabled) {
		exportPersons(wb, enabled, PLAYER_DESCRIPTOR, p -> perRep.findActiveInTeam(team.getId(), p));
	}

	private void exportParents(XSSFWorkbook wb, Team team, boolean enabled) {
		exportPersons(wb, enabled, PARENT_DESCRIPTOR, p -> perRep.findParentsInTeam(team.getId(), p));
	}

	private void exportInactive(XSSFWorkbook wb, Team team, boolean enabled) {
		exportPersons(wb, enabled, INACTIVE_DESCRIPTOR, p -> perRep.findInActiveInTeam(team.getId(), p));
	}
}
