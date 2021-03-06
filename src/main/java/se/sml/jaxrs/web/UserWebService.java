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

import se.sml.jaxrs.model.UserWeb;
import se.sml.sdj.model.User;
import se.sml.sdj.model.WorkItem;
import se.sml.sdj.service.UserService;
import se.sml.sdj.service.WorkItemService;

@Path("/users")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public final class UserWebService {

	@Context
	private UriInfo uriInfo;

	// Create
	// - Create User
	@POST
	public Response addUser(UserWeb userWeb) {

		User user = new User(userWeb.getUsername(), userWeb.getFirstName(), userWeb.getLastName(), userWeb.getUserNumber(), userWeb.getStatus());

		getBean(UserService.class).save(user);

		URI location = uriInfo.getAbsolutePathBuilder().path(getClass(), "getUserByUserNumberWeb").build(userWeb.getUserNumber());

		return Response.created(location).build();
	}

	// Read
	// - Get User by userNumber ta bort getByUserNumberWeb
	@GET
	@Path("/getByUserNumberWeb/{userNumber}")
	public UserWeb getByUserNumberWeb(@PathParam("userNumber") String userNumber) {

		User user = getBean(UserService.class).getByUserNumber(userNumber);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return new UserWeb(user);
	}

	// - Get User by firstName
	@GET
	@Path("/getByFirstNameWeb/{firstName}")
	public Collection<UserWeb> getByFirstNameWeb(@PathParam("firstName") String firstName) {

		Collection<User> user = getBean(UserService.class).getByFirstName(firstName);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<UserWeb> userWeb = new ArrayList<UserWeb>();

		user.forEach(u -> userWeb.add(new UserWeb(u)));

		return userWeb;
	}

	// - Get User by lastName
	@GET
	@Path("/getByLastNameWeb/{lastName}")
	public Collection<UserWeb> getByLastNameWeb(@PathParam("lastName") String lastName) {

		Collection<User> user = getBean(UserService.class).getByLastName(lastName);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<UserWeb> userWeb = new ArrayList<UserWeb>();

		user.forEach(u -> userWeb.add(new UserWeb(u)));

		return userWeb;
	}

	// - Get User by username
	@GET
	@Path("/getByUsernameWeb/{username}")
	public UserWeb getByUsernameWeb(@PathParam("username") String username) {

		User user = getBean(UserService.class).getByUsername(username);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		return new UserWeb(user);
	}

	
	//Querry parameter
	// - Get all Users from specified Team
	@GET
	@Path("/getByTeamWeb/{team}")
	public Collection<UserWeb> getByTeamWeb(@PathParam("team") String team) {

		Collection<User> user = getBean(UserService.class).getUsersByTeam(team);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		Collection<UserWeb> userWeb = new ArrayList<UserWeb>();

		user.forEach(u -> userWeb.add(new UserWeb(u)));

		return userWeb;
	}

	// Update
	// - Update User  bort med /update/
	@PUT
	@Path("/update/{userNumber}")
	public Response update(@PathParam("userNumber") String userNumber, UserWeb userWeb) {

		User user = getBean(UserService.class).getByUserNumber(userNumber);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		user.setUsername(userWeb.getUsername()).setFirstName(userWeb.getFirstName()).setLastName(userWeb.getLastName()).setUserNumber(userWeb.getUserNumber())
				.setStatus(userWeb.getStatus());

		getBean(UserService.class).save(user);

		return Response.noContent().build();
	}

	// Add a workItem to a User /userNumber/workItems   restful urls
	@PUT
	@Path("addWorkItem/{username}/{workItemNumber}")
	public Response addWorkItem(@PathParam("username") String username, @PathParam("workItemNumber") String workItemNumber) {

		WorkItem workItem = getBean(WorkItemService.class).getByWorkItemNumber(workItemNumber);

		User user = getBean(UserService.class).getByUsername(username);

		// FRÅGA ANDERS: VILL EN HA SÅ ATT DET STÅR EX "User not found" isf
		// "NOT_FOUND"
		if (workItem == null || user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		else {
			getBean(UserService.class).addWorkItem(user, workItem);
		}
		return Response.noContent().build();
	}

	// Delete
	// - Delete User (update status to "Inactive")
	@DELETE
	@Path("/delete/{userNumber}")
	public Response delete(@PathParam("userNumber") String userNumber) {

		User user = getBean(UserService.class).getByUserNumber(userNumber);

		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}

		user.setStatus("Inactive");

		getBean(UserService.class).save(user);

		return Response.noContent().build();
	}

}
