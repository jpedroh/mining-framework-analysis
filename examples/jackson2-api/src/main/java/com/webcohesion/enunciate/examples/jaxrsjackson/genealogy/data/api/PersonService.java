/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.examples.jaxrsjackson.genealogy.data.api;

import com.webcohesion.enunciate.examples.jaxrsjackson.genealogy.data.Page;
import com.webcohesion.enunciate.examples.jaxrsjackson.genealogy.data.Person;
import com.webcohesion.enunciate.examples.jaxrsjackson.genealogy.data.PersonResults;
import com.webcohesion.enunciate.examples.jaxrsjackson.genealogy.data.Sample;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.List;

/**
 * @author Ryan Heaton
 */
@Path ( "/persons" )
public class PersonService {

  @GET
  @Path ( "{id}" )
  public Person getPerson(@PathParam ( "id" ) String id) {
    return new Person();
  }

  @GET
  @Path ( "/multiple/{ids}" )
  public List<Person> getMultiplePersons(@PathParam ( "ids" ) String ids) {
    return null;
  }

  @GET
  @Path ( "page" )
  public Page<Person> getPersonPage(@QueryParam ( "start" ) int start, @QueryParam ( "count" ) int count) {
    return null;
  }

  @GET
  @Path ( "search" )
  public PersonResults searchPersons(@QueryParam ( "q" ) String query) {
    return null;
  }

  @GET
  @Path ( "sample" )
  public Sample getSample(@QueryParam ( "type" ) String type) {
    return null;
  }

}
