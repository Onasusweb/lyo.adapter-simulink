/*********************************************************************************************
 * Copyright (c) 2014 Model-Based Systems Engineering Center, Georgia Institute of Technology.
 *                         http://www.mbse.gatech.edu/
 *                  http://www.mbsec.gatech.edu/research/oslc
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  Contributors:
 *
 *	   Axel Reichwein, Koneksys (axel.reichwein@koneksys.com)		
 *******************************************************************************************/
package org.eclipse.lyo.adapter.simulink.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.lyo.adapter.simulink.application.SimulinkManager;
import org.eclipse.lyo.adapter.simulink.resources.Constants;
import org.eclipse.lyo.adapter.simulink.resources.SimulinkBlock;
import org.eclipse.lyo.adapter.simulink.resources.SimulinkElementsToCreate;
import org.eclipse.lyo.adapter.simulink.resources.SimulinkInputPort;
import org.eclipse.lyo.adapter.simulink.resources.SimulinkModel;
import org.eclipse.lyo.adapter.simulink.serviceproviders.ServiceProviderCatalogSingleton;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;

/**
 * This servlet contains the implementation of OSLC RESTful web services for Simulink Model resources.
 * 
 * The servlet contains web services for returning specific Simulink Model resources 
 * in HTML and other formats.
 * 
 * @author Axel Reichwein (axel.reichwein@koneksys.com)
 */
@OslcService(Constants.SIMULINK_MODEL_DOMAIN)
@Path("{modelName}/model")
public class SimulinkModelService {
	
	@Context
	private HttpServletRequest httpServletRequest;
	@Context
	private HttpServletResponse httpServletResponse;
	@Context
	private UriInfo uriInfo;
	
	@OslcQueryCapability(title = "Simulink Model Query Capability", label = "Simulink Model Catalog Query", resourceShape = OslcConstants.PATH_RESOURCE_SHAPES
			+ "/" + Constants.PATH_SIMULINK_MODEL, resourceTypes = { Constants.TYPE_SIMULINK_MODEL }, usages = { OslcConstants.OSLC_USAGE_DEFAULT })
	@GET
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public SimulinkModel getModel(
			@PathParam("modelName") final String modelName,
			@QueryParam("oslc.where") final String where,
			@QueryParam("oslc.select") final String select,
			@QueryParam("oslc.prefix") final String prefix,
			@QueryParam("page") final String pageString,
			@QueryParam("oslc.orderBy") final String orderBy,
			@QueryParam("oslc.searchTerms") final String searchTerms,
			@QueryParam("oslc.paging") final String paging,
			@QueryParam("oslc.pageSize") final String pageSize)
			throws IOException, ServletException {
		SimulinkManager.loadSimulinkWorkingDirectory();
		SimulinkModel simulinkModel = SimulinkManager
				.getModelByName(modelName);
		return simulinkModel;
	}
	
	@GET	
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
			OslcMediaType.APPLICATION_JSON })
	public org.eclipse.lyo.adapter.simulink.resources.SimulinkModel getModel(
			@PathParam("modelName") final String modelName)
			throws URISyntaxException {
		SimulinkManager.loadSimulinkWorkingDirectory();
		SimulinkModel simulinkModel = SimulinkManager
				.getModelByName(modelName);
		return simulinkModel;
	}
	
	@GET
    @Produces(MediaType.TEXT_HTML)
    public void getHtmlModel(@PathParam("modelName") final String modelName)
    {
		ServiceProviderCatalog catalog = ServiceProviderCatalogSingleton.getServiceProviderCatalog(httpServletRequest);
		SimulinkManager.loadSimulinkWorkingDirectory();
		SimulinkModel simulinkModel = SimulinkManager
				.getModelByName(modelName);
		
		String requestURL = httpServletRequest.getRequestURL().toString();

    	if (simulinkModel !=null )
    	{	        
	        httpServletRequest.setAttribute("model", simulinkModel);
	        httpServletRequest.setAttribute("catalog",catalog);
	        httpServletRequest.setAttribute("requestURL",requestURL);

	        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/simulink/model_html.jsp");
    		try {
				rd.forward(httpServletRequest, httpServletResponse);
			} catch (Exception e) {				
				e.printStackTrace();
				throw new WebApplicationException(e);
			} 
    	}
    }
	
	@POST	
	@Consumes({ OslcMediaType.APPLICATION_RDF_XML,
		OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	@Produces({ OslcMediaType.APPLICATION_RDF_XML,
		OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
	public Response addModelElements(@PathParam("modelName") final String modelName,
			final SimulinkElementsToCreate newElements) throws IOException, ServletException {
		SimulinkManager.createSimulinkElements(newElements, modelName);		
		URI about = newElements.getAbout();
		return Response.created(about).entity(newElements).build();
	}

}
