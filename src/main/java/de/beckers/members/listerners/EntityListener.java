package de.beckers.members.listerners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import de.beckers.members.api.PersonService;
import de.beckers.members.model.TeamMembership;

@RepositoryEventHandler
public class EntityListener {
	@Autowired
	private PersonService ps;
	
	@HandleAfterSave()
	public void handleTeamMEmbershipSave(TeamMembership p) {
		ps.fixRoles(p.getPerson());
	}

	@HandleAfterDelete()
	public void handleTeamMEmbershipDelete(TeamMembership p) {
		ps.fixRoles(p.getPerson());
	}
}
