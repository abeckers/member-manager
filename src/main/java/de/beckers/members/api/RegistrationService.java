package de.beckers.members.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface RegistrationService {
    @RequestMapping(method = RequestMethod.POST, value = "/registration/{id}/accept")
    StringValue acceptRegistration(@PathVariable("id") String id, @RequestBody RegistrationInfo info);

    @RequestMapping(method = RequestMethod.GET, value = "/registration/{id}/prepare")
    PrepareResult prepareRegistration(@PathVariable("id") String id);
}
