package se.sml.jaxrs.web;

import static se.sml.jaxrs.ContextLoader.getBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.sml.jaxrs.model.IssueWeb;
import se.sml.jaxrs.model.WorkItemWeb;
import se.sml.sdj.model.Issue;
import se.sml.sdj.model.WorkItem;
import se.sml.sdj.service.UserService;
import se.sml.sdj.service.WorkItemService;

@Path("/workItem")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public final class WorkItemWebService {

	@Context
	private UriInfo uriInfo;

	// Create
	// - Create workItem
	@POST
	public Response addWorkItem(WorkItemWeb workItemWeb) {

		WorkItem workItem = new WorkItem(workItemWeb.getLable(), workItemWeb.getDescription(), workItemWeb.getWorkItemNumber(), workItemWeb.getStatus());

		getBean(WorkItemService.class).save(workItem);

		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getByWorkItemNumberWeb").build(workItemWeb.getWorkItemNumber());

		return Response.created(location).build();
	}

	// - Create Issue
	@POST
	@Path("/addIssue/{workItemNumber}")
	public Response addIssue(@PathParam("workItemNumber") String workItemNumber, IssueWeb issueWeb) {
		Issue issue = new Issue(issueWeb.getIssue());

		getBean(WorkItemService.class).addIssue(workItemNumber, issue);

		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getByWorkItemNumberWeb").build(workItemNumber);

		return Response.created(location).build();
	}

	// Read
	// - Get by workItemNumber
	@GET
	@Path("/getByWorkItemNumberWeb/{workItemNumber}")
	public WorkItemWeb getByWorkItemNumberWeb(@PathParam("workItemNumber") String workItemNumber) {

		WorkItem workItem = getBean(WorkItemService.class).getByWorkItemNumber(workItemNumber);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return new WorkItemWeb(workItem);
	}

	// - Get by status
	@GET
	@Path("/getByStatusWeb/{status}")
	public Collection<WorkItemWeb> getByStatusWeb(@PathParam("status") String status) {

		Collection<WorkItem> workItem = getBean(WorkItemService.class).getByStatus(status);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<WorkItemWeb> workItemWeb = new ArrayList<WorkItemWeb>();

		workItem.forEach(wi -> workItemWeb.add(new WorkItemWeb(wi)));

		return workItemWeb;
	}

	// - Get all WorkItem for specified Team
	@GET
	@Path("/getByTeamWeb/{team}")
	public Collection<WorkItemWeb> getByTeamWeb(@PathParam("team") String team) {

		Collection<WorkItem> workItem = getBean(WorkItemService.class).getByTeam(team);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<WorkItemWeb> workItemWeb = new ArrayList<WorkItemWeb>();

		workItem.forEach(wi -> workItemWeb.add(new WorkItemWeb(wi)));

		return workItemWeb;
	}

	// - Get all WorkItem for specific User
	@GET
	@Path("/getByUserWeb/{username}")
	public Collection<WorkItemWeb> getByUserWeb(@PathParam("username") String username) {

		Collection<WorkItem> workItem = getBean(UserService.class).getWorkItemsByUser(username);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<WorkItemWeb> workItemWeb = new ArrayList<WorkItemWeb>();

		workItem.forEach(wi -> workItemWeb.add(new WorkItemWeb(wi)));

		return workItemWeb;
	}

	// - Get all WorkItem containing specified description
	@GET
	@Path("/getByDescriptionContainingWeb/{text}")
	public Collection<WorkItemWeb> getByDescriptionContainingWeb(@PathParam("text") String text) {

		Collection<WorkItem> workItem = getBean(WorkItemService.class).getByDescriptionContaining(text);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<WorkItemWeb> workItemWeb = new ArrayList<WorkItemWeb>();

		workItem.forEach(wi -> workItemWeb.add(new WorkItemWeb(wi)));

		return workItemWeb;
	}

	// Update
	// - Update Status
	@PUT
	@Path("/updateUser/{workItemNumber}")
	public Response updateUser(@PathParam("workItemNumber") String workItemNumber, WorkItemWeb workItemWeb) {

		WorkItem workItem = getBean(WorkItemService.class).getByWorkItemNumber(workItemNumber);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		workItem.setLable(workItemWeb.getLable()).setDescription(workItemWeb.getDescription()).setWorkItemNumber(workItemWeb.getWorkItemNumber())
				.setStatus(workItemWeb.getStatus());

		getBean(WorkItemService.class).save(workItem);

		return Response.noContent().build();
	}

	// - Update Issue
	@PUT
	@Path("/updateIssue/{workItemNumber}")
	public Response updateIssue(@PathParam("workItemNumber") String workItemNumber, IssueWeb issueWeb) {

		Issue issue = new Issue(issueWeb.getIssue());

		getBean(WorkItemService.class).updateIssue(workItemNumber, issue);

		return Response.noContent().build();
	}

	// Delete
	// - Delete WorkItem (update status to "Inactive")
	@DELETE
	@Path("/deleteUser/{workItemNumber}")
	public Response deleteUser(@PathParam("workItemNumber") String workItemNumber) {

		WorkItem workItem = getBean(WorkItemService.class).getByWorkItemNumber(workItemNumber);

		if (workItem == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		workItem.setStatus("Inactive");

		getBean(WorkItemService.class).save(workItem);

		return Response.noContent().build();
	}

}
