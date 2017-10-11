package de.beckers.members.export;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.data.domain.Sort;

import de.beckers.members.model.Person;
import de.beckers.members.model.Relation;

public class ExporterBase {

	protected static final BiConsumer<SheetContext, Object> EMAIL_DECORATOR = new BiConsumer<SheetContext, Object>() {

		@Override
		public void accept(SheetContext ctx, Object u) {
			if (u == null) {
				return;
			}
			XSSFCell cell = ctx.getCurrentCell();
			String address = "mailto:" + u;
			cell.setCellValue(u.toString());
			try {
				Hyperlink link = ctx.getCreateHelper().createHyperlink(Hyperlink.LINK_EMAIL);
				link.setAddress(address);
				cell.setHyperlink(link);
			} catch (IllegalArgumentException e) {
				// log.severe(e.getMessage() + " " + address);
			}
		}
	};
	protected static final BiConsumer<SheetContext, Object> RELATION_DECORATOR = new BiConsumer<SheetContext, Object>() {

		@Override
		public void accept(SheetContext ctx, Object u) {
			if (u == null) {
				return;
			}
			String pid = (String) u;
			List<Relation> relations = ctx.getRelRep().findByPerson(pid);
			StringBuilder b = new StringBuilder();
			for (Relation relation : relations) {
				if (b.length() > 0)
					b.append(", ");
				if (relation.getPerson1().getId().equals(pid)) {
					b.append(relation.getPerson2().getFullName());
				} else {
					b.append(relation.getPerson1().getFullName());
				}
			}
			XSSFCell cell = ctx.getCurrentCell();
			cell.setCellValue(b.toString());
		}
	};
	protected static final BiConsumer<SheetContext, Object> BOOL_DECORATOR = new BiConsumer<SheetContext, Object>() {

		@Override
		public void accept(SheetContext ctx, Object u) {
			XSSFCell cell = ctx.getCurrentCell();
			if (((Boolean) u).booleanValue()) {
				cell.setCellValue("x");
			}
		}
	};
	protected static final ColumnDescriptor COLUMN_CERTIFICATE = ColumnDescriptor.builder().label("Attest")
			.value(Person::getCertificateFlag).width(1500).build();
	protected static final ColumnDescriptor COLUMN_ATTACHMENT = ColumnDescriptor.builder().label("Anlage")
			.value(Person::getAttachmentFlag).width(1500).build();
	protected static final ColumnDescriptor ID_COLUMN = ColumnDescriptor.builder().label("ID").value(Person::getIdState)
			.width(1500).build();
	protected static final ColumnDescriptor PASS_COLUMN = ColumnDescriptor.builder().label("Pass")
			.value(Person::getPassState).width(1500).build();
	protected static final ColumnDescriptor PHOTO_COLUMN = ColumnDescriptor.builder().label("Foto")
			.value(Person::hasPhoto).decorator(BOOL_DECORATOR).width(1500).build();
	protected static final ColumnDescriptor ACTIVE_COLUMN = ColumnDescriptor.builder().label("aktiv")
			.value(Person::getActive).width(1500).build();
	protected static final ColumnDescriptor ENTRY_COLUMN = ColumnDescriptor.builder().label("Eintritt")
			.value(Person::getEntry).width(2500).build();
	protected static final ColumnDescriptor BIRTH_DATE_COLUMN = ColumnDescriptor.builder().label("Geb.-Datum")
			.value(Person::getBirthDate).width(2500).build();
	protected static final ColumnDescriptor EMAIL_COLUMN = ColumnDescriptor.builder().label("Email")
			.value(p -> p.getContact() == null ? null : p.getContact().getEmail()).decorator(EMAIL_DECORATOR)
			.width(6000).build();
	protected static final ColumnDescriptor MOBILE_COLUMN = ColumnDescriptor.builder().label("Mobil")
			.value(p -> p.getContact() == null ? null : p.getContact().getMobile()).width(4000).build();
	protected static final ColumnDescriptor SERIAL_NUMBDER_COLUMN = ColumnDescriptor.builder().label("Lfd.-Nr.")
			.width(1500).build();
	protected static final ColumnDescriptor NOTE_COLUMN = ColumnDescriptor.builder().label("Notiz")
			.value(Person::getNote).width(4000).build();
	protected static final ColumnDescriptor NAME_COLUMN = ColumnDescriptor.builder().label("Name")
			.value(Person::getName).width(4000).build();
	protected static final ColumnDescriptor FIRST_NAME_COLUMN = ColumnDescriptor.builder().label("Vorname")
			.value(Person::getFirstName).width(3500).build();
	protected static final ColumnDescriptor ADDRESS_COLUMN = ColumnDescriptor.builder().label("Adresse")
			.value(Person::getFullAddress).width(8000).build();
	protected static final ColumnDescriptor STREET_COLUMN = ColumnDescriptor.builder().label("Straße")
			.value(Person::getStreet).width(4000).build();
	protected static final ColumnDescriptor ZIP_COLUMN = ColumnDescriptor.builder().label("PLZ").value(Person::getZip)
			.width(2000).build();
	protected static final ColumnDescriptor PLACE_COLUMN = ColumnDescriptor.builder().label("Ort")
			.value(Person::getPlace).width(4000).build();
	protected static final ColumnDescriptor BIRTH_PLACE_COLUMN = ColumnDescriptor.builder().label("Geburtsort")
			.value(Person::getBirthPlace).width(4000).build();
	protected static final ColumnDescriptor NATIONALITY_COLUMN = ColumnDescriptor.builder().label("Nat")
			.value(Person::getNationality).width(2000).build();
	protected static final ColumnDescriptor PHONE_COLUMN = ColumnDescriptor.builder().label("Telefon")
			.value(p -> p.getContact() == null ? null : p.getContact().getPhone()).width(4000).build();
	protected static final ColumnDescriptor CHILD_COLUMN = ColumnDescriptor.builder().label("Kind")
			.value(p -> p.getId() == null ? null : p.getId()).decorator(RELATION_DECORATOR).width(4000).build();
	protected static final SheetDescriptor COACHES_DESCRIPTOR = SheetDescriptor.builder().sheetName("Trainer")
			.headerLeft("Trainer").headerRight(new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
			.footerRight("Seite &P von &N").column(SERIAL_NUMBDER_COLUMN).column(NAME_COLUMN).column(FIRST_NAME_COLUMN)
			.column(STREET_COLUMN).column(ZIP_COLUMN).column(PLACE_COLUMN).column(PHONE_COLUMN).column(MOBILE_COLUMN)
			.column(EMAIL_COLUMN).build();
	protected static final SheetDescriptor STAFF_DESCRIPTOR = SheetDescriptor.builder().sheetName("Betreuer")
			.headerLeft("Trainer").headerRight(new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
			.footerRight("Seite &P von &N").column(SERIAL_NUMBDER_COLUMN).column(NAME_COLUMN).column(FIRST_NAME_COLUMN)
			.column(STREET_COLUMN).column(ZIP_COLUMN).column(PLACE_COLUMN).column(PHONE_COLUMN).column(MOBILE_COLUMN)
			.column(EMAIL_COLUMN).build();
	protected static final SheetDescriptor NO_ROLE_DESCRIPTOR = SheetDescriptor.builder().sheetName("Ohne Rolle")
			.headerLeft("Trainer").headerRight(new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
			.footerRight("Seite &P von &N").column(SERIAL_NUMBDER_COLUMN).column(NAME_COLUMN).column(FIRST_NAME_COLUMN)
			.column(STREET_COLUMN).column(ZIP_COLUMN).column(PLACE_COLUMN).column(PHONE_COLUMN).column(MOBILE_COLUMN)
			.column(EMAIL_COLUMN).build();
	protected static final SheetDescriptor PLAYER_DESCRIPTOR = SheetDescriptor.builder().sheetName("Spieler")
			.headerLeft("Spieler").headerRight(new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
			.footerRight("Seite &P von &N").column(SERIAL_NUMBDER_COLUMN).column(NAME_COLUMN).column(FIRST_NAME_COLUMN)
			.column(STREET_COLUMN).column(ZIP_COLUMN).column(PLACE_COLUMN).column(BIRTH_DATE_COLUMN)
			.column(BIRTH_PLACE_COLUMN).column(NATIONALITY_COLUMN).column(PHONE_COLUMN).column(MOBILE_COLUMN)
			.column(EMAIL_COLUMN).column(ENTRY_COLUMN).column(PHOTO_COLUMN).column(COLUMN_CERTIFICATE).column(ID_COLUMN)
			.column(PASS_COLUMN).column(COLUMN_ATTACHMENT).column(NOTE_COLUMN).build();
	protected static final SheetDescriptor INACTIVE_DESCRIPTOR = SheetDescriptor.builder().sheetName("Inaktiv")
			.headerLeft("Spieler").headerRight(new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
			.footerRight("Seite &P von &N").column(SERIAL_NUMBDER_COLUMN).column(NAME_COLUMN).column(FIRST_NAME_COLUMN)
			.column(STREET_COLUMN).column(ZIP_COLUMN).column(PLACE_COLUMN).column(BIRTH_DATE_COLUMN)
			.column(BIRTH_PLACE_COLUMN).column(NATIONALITY_COLUMN).column(PHONE_COLUMN).column(MOBILE_COLUMN)
			.column(EMAIL_COLUMN).column(ENTRY_COLUMN).column(PHOTO_COLUMN).column(COLUMN_CERTIFICATE).column(ID_COLUMN)
			.build();
	protected static final SheetDescriptor PARENT_DESCRIPTOR = SheetDescriptor.builder().sheetName("Eltern")
			.headerLeft("Spieler").headerRight(new SimpleDateFormat("dd.MM.yyyy").format(new Date()))
			.footerRight("Seite &P von &N").column(SERIAL_NUMBDER_COLUMN).column(NAME_COLUMN).column(FIRST_NAME_COLUMN)
			.column(STREET_COLUMN).column(ZIP_COLUMN).column(PLACE_COLUMN).column(PHONE_COLUMN).column(MOBILE_COLUMN)
			.column(EMAIL_COLUMN).column(CHILD_COLUMN).build();
	protected static final Sort MEMBER_SORT = new Sort("name", "firstName");
	protected static final int PAGE_SIZE = 100;

}
