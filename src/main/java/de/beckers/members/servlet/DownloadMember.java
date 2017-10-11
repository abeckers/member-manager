package de.beckers.members.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import de.beckers.members.export.MembersExporter;

@WebServlet("/api/download/member")
public class DownloadMember extends HttpServlet {
	@Autowired
	private MembersExporter exporter;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2258403270414361735L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean coach = Boolean.parseBoolean(req.getParameter("coach"));
		boolean inactive = Boolean.parseBoolean(req.getParameter("inactive"));
		boolean noRole = Boolean.parseBoolean(req.getParameter("noRole"));
		boolean player = Boolean.parseBoolean(req.getParameter("player"));
		boolean staff = Boolean.parseBoolean(req.getParameter("staff"));
		boolean parents = Boolean.parseBoolean(req.getParameter("parents"));

		resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		resp.setHeader("Content-Disposition", "attachment; filename=\"Mitgliederliste_"
				+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xlsx\"");

		OutputStream output = resp.getOutputStream();
		exporter.export(output, player, coach, staff, inactive, noRole, parents);
		output.flush();
	}
}
