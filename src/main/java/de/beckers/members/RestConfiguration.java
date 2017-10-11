package de.beckers.members;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import de.beckers.members.listerners.EntityListener;
import de.beckers.members.model.Club;
import de.beckers.members.model.Document;
import de.beckers.members.model.Person;
import de.beckers.members.model.Registration;
import de.beckers.members.model.Team;
import de.beckers.members.model.Training;
import de.beckers.members.model.TrainingGroup;

@Configuration
public class RestConfiguration extends RepositoryRestMvcConfiguration {
	@Bean
	EntityListener personEventHandler() {
		return new EntityListener();
	}
	
	@Override
	public RepositoryRestConfiguration config() {
		RepositoryRestConfiguration config = super.config();
		config.setBasePath("/api");
		config.exposeIdsFor(Registration.class, Person.class, Club.class, Team.class, TrainingGroup.class,
				Training.class, Document.class);
		return config;
	}

	@Bean
	public ResourceProcessor<Resource<Registration>> registrationProcessor() {
		return new ResourceProcessor<Resource<Registration>>() {
			@Override
			public Resource<Registration> process(Resource<Registration> resource) {
				resource.add(new Link(resource.getId().getHref() + "/accept", "accept"));
				resource.add(new Link(resource.getId().getHref() + "/prepare", "prepare"));
				return resource;
			}
		};
	}

	@Bean
	public ResourceProcessor<Resource<Person>> personProcessor() {
		return new ResourceProcessor<Resource<Person>>() {
			@Override
			public Resource<Person> process(Resource<Person> resource) {
				resource.add(new Link(resource.getId().getHref() + "/relations", "relations"));
				return resource;
			}
		};
	}

}