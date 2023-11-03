/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.services;

import com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.cite.Source;
import com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.exceptions.EisAccountException;
import com.webcohesion.enunciate.metadata.Facet;
import com.webcohesion.enunciate.metadata.rs.*;
import com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.Person;
import com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.PersonExt;
import com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.RootElementMapWrapper;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Collection;

/**
 * The person service is used to access {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.Person persons} in the database.
 *
 * Also take a look at {@link AdminService} and {@link SourceService#addSource(Source)}.
 *
 * You can also see the {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.services.impl.PersonServiceImpl REST API for this service interface}. Look how awesome is {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.services.impl.PersonServiceImpl#deletePerson(String, String)}.
 * 
 * @author Ryan Heaton
 */
@WebService (
  targetNamespace = "http://enunciate.webcohesion.com/samples/full"
)
public interface PersonService {

  /**
   * Stores a person in the database.
<pre>
   {
      "some" : "example",
      "json" : "values"
   }
</pre>
   *
   * @since Version E
   * @param person The person to store in the database.
   *               <pre><code>&lt;codes>
   *                 &lt;code>This&lt;/code>
   *                 &lt;code>is&lt;/code>
   *                 &lt;/codes></code></pre>
   * @return The {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.Person} that was stored (presumably modified for storage).
   * @HTTP 333 if something weird happens.
   * @ResponseHeader Blah If blah blah {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.Name} blah.
   */
  @SOAPBinding (
    parameterStyle = SOAPBinding.ParameterStyle.BARE
  )
  @PUT
  @Path ("/pedigree/person")
  @ResponseHeaders (
    @ResponseHeader( name = "Location", description = "The location of the person stored.")
  )
  @WebResult(name = "stored")
  Person storePerson(Person person);

  /**
   * Search for a person.
   *
   * @param query the query.
   * @return The {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.Person}.
   */
  @Path("/search")
  @GET
  @WebResult(name = "searchResults")
  Person search(@WebParam ( name = "query" ) @BeanParam PersonQuery query);

  @GET
  @Path("/pedigree/personext/{id}")
  @StatusCodes ({
    @ResponseCode ( code = 404, condition = "The person is not found.", type = @TypeHint(PersonExt.class))
  })
  @Warnings ({
    @ResponseCode ( code = 299, condition = "The reason the person wasn't found.")
  })
  @Deprecated
  @WebResult(name = "extPerson")
  PersonExt readExtPerson(@WebParam ( name = "extId" ) @PathParam("id") String id);

  @GET
  @Path("/pedigree/admin/persons/{id}")
  @Produces({"application/xml;qs=1.0", "application/json;qs=0.9"})
  @StatusCodes ({
    @ResponseCode ( code = 404, condition = "The person is not found.")
  })
  @Warnings ({
    @ResponseCode ( code = 299, condition = "The reason the person wasn't found.")
  })
  @Facet ( "http://enunciate.webcohesion.com/samples/full#admin" )
  @WebResult(name = "extPersonAdmin")
  PersonExt readPersonAdmin(@WebParam ( name = "idAdmin" ) @PathParam("id") String id);

  /**
   * Reads a set of persons from the database.  Intended as an example of
   * collections as SOAP parameters.
   * @param personIds The ids of the {@link com.webcohesion.enunciate.examples.jaxwsrijersey.genealogy.data.Person}s to read.
   * @return The persons that were read.
   * @throws ServiceException
   *         If the read of one or more of the people failed.
   */
  @WebResult(name = "persons")
  Collection<Person> readPersons(@WebParam ( name = "pids" ) Collection<String> personIds) throws ServiceException, EisAccountException;

  /**
   * Deletes a person from the database.  Not a one-way method, but still void.
   *
   * @param PErsonId The id of the person.
   * @param message The message about the delete.
   * @throws ServiceException If some problem occurred when deleting the person.
   */
  @DELETE
  @Path("/remover/pedigree/person/{id}")
  void deletePerson(@WebParam ( name = "dpid" ) @PathParam ("id") String PErsonId, @WebParam ( name = "dmesg" ) @HeaderParam("X-Message") String message) throws ServiceException;

  /**
   * Increment one of the person counters.
   *
   * @param amount The amount.
   * @param counterType The counter type.
   * @param factor The factor.
   */
  @POST
  @Path("/person/counter/increment")
  void incrementCount(@WebParam ( name = "amount" ) @Max( 4 ) @Min( 1 ) @QueryParam ( "amount" ) int amount, @WebParam ( name = "counter" ) @QueryParam ( "counter" ) CounterType counterType, @WebParam ( name = "factor" ) @QueryParam ( "factor" ) Double factor);

  /**
   * Store some generic properties.
   *
   * @param map The map of generic properties.
   * @return The generic properties.
   * @throws ServiceException Upon a problem.
   */
  @PUT
  @Path("/properties/generic")
  @WebResult(name = "storedProps")
  RootElementMapWrapper storeGenericProperties(@WebParam ( name = "genericProps" ) RootElementMapWrapper map) throws ServiceException;

  @WebMethod (exclude = true)
  @POST
  @Path("/multipart")
  @Consumes( MediaType.MULTIPART_FORM_DATA )
  void postMultipart(@FormDataParam ("file1") InputStream file1, @FormDataParam("file2") InputStream file2);

  @WebMethod (exclude = true)
  @POST
  @Path("/single")
  @Consumes( "image/jpeg")
  void postSingle(InputStream bytes);

// todo: uncomment when wanting to spend time investigating why jaxb doesn't work with the JAX-WS types the same way it does its own.
//  /**
//   * Reads the family of a given person.  Tests out maps.
//   *
//   * @param personId The id of the person for which to read the family.
//   * @return The persons in the family by relationship type.
//   * @throws ServiceException If some problem occurred.
//   */
//  Map<RelationshipType, Person> readFamily(String personId) throws ServiceException;
}
