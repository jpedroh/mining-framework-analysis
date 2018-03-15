package org.opentosca.container.api.legacy.resources.csar.servicetemplate.instances;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.opentosca.container.api.legacy.instancedata.LinkBuilder;
import org.opentosca.container.api.legacy.instancedata.exception.GenericRestException;
import org.opentosca.container.api.legacy.instancedata.model.SimpleXLink;
import org.opentosca.container.api.legacy.osgi.servicegetter.InstanceDataServiceHandler;
import org.opentosca.container.api.legacy.resources.utilities.JSONUtils;
import org.opentosca.container.core.common.ReferenceNotFoundException;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.model.instance.IdConverter;
import org.opentosca.container.core.service.IInstanceDataService;
import org.w3c.dom.Document;

/**
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class ServiceTemplateInstancePropertiesResource {

    private final CSARID csarId;
    private final QName serviceTemplateID;
    private final int serviceInstanceID;


    public ServiceTemplateInstancePropertiesResource(final CSARID csarId, final QName serviceTemplateID, final int id) {
        this.csarId = csarId;
        this.serviceTemplateID = serviceTemplateID;
        this.serviceInstanceID = id;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response doGetXML(@QueryParam("property") final List<String> propertiesList) {

        final Document idr = this.getRefs(propertiesList);

        return Response.ok(idr).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetJSON(@QueryParam("property") final List<String> propertiesList) {

        final Document idr = this.getRefs(propertiesList);

        return Response.ok(new JSONUtils().xmlToGenericJsonObject(idr.getChildNodes()).toString()).build();
    }

    public Document getRefs(final List<String> propertiesList) {
        final List<QName> qnameList = new ArrayList<>();

        // convert all String in propertyList to qnames
        try {
            if (propertiesList != null) {
                for (final String stringValue : propertiesList) {
                    qnameList.add(QName.valueOf(stringValue));
                }
            }
        }
        catch (final Exception e) {
            throw new GenericRestException(Status.BAD_REQUEST,
                "error converting one of the properties-parameters: " + e.getMessage());
        }

        final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();
        try {
            final Document properties =
                service.getServiceInstanceProperties(IdConverter.serviceInstanceIDtoURI(this.serviceInstanceID),
                                                     qnameList);
            return properties;
        }
        catch (final ReferenceNotFoundException e) {
            throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response setProperties(@Context final UriInfo uriInfo, final Document xml) {

        final IInstanceDataService service = InstanceDataServiceHandler.getInstanceDataService();

        try {
            service.setServiceInstanceProperties(IdConverter.serviceInstanceIDtoURI(this.serviceInstanceID), xml);
        }
        catch (final ReferenceNotFoundException e) {
            throw new GenericRestException(Status.NOT_FOUND, e.getMessage());
        }

        final SimpleXLink xLink =
            new SimpleXLink(LinkBuilder.linkToServiceInstanceProperties(uriInfo, this.serviceInstanceID),
                "ServiceInstance: " + this.serviceInstanceID + " Properties");
        return Response.ok(xLink).build();
    }

}
