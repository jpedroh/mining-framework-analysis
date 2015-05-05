package com.webcohesion.enunciate.examples.genealogy.services.impl;

import com.webcohesion.enunciate.examples.genealogy.data.PersonAdmin;
import com.webcohesion.enunciate.examples.genealogy.services.AdminService;

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path ("/admin")
@WebService(endpointInterface = "com.webcohesion.enunciate.examples.genealogy.services.AdminService" )
public class AdminServiceImpl implements AdminService {

  @Path("/admin/person/{id}")
  public PersonAdmin readAdminPerson(@PathParam("id") String id) {
    return new PersonAdmin();
  }

}
