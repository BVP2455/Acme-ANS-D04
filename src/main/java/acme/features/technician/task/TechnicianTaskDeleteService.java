
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceTask;
import acme.entities.maintenance.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Task task;
		Technician technician;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);
		technician = task == null ? null : task.getTechnician();
		status = task != null && task.isDraftMode() && super.getRequest().getPrincipal().hasRealm(technician);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		super.bindObject(task, "type", "description", "priority", "estimatedDuration");

	}

	@Override
	public void validate(final Task task) {
		;
	}

	@Override
	public void perform(final Task t) {
		Collection<MaintenanceTask> maintenanceTasks;

		maintenanceTasks = this.repository.findMaintenanceTasksInTask(t.getId());
		this.repository.deleteAll(maintenanceTasks);
		this.repository.delete(t);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;
		dataset = super.unbindObject(task);
		super.getResponse().addData(dataset);
	}

}
