
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.service.Service;
import acme.entities.service.ServiceRepository;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Autowired
	private ServiceRepository repository;


	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;

		if (service == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		String promotionCode = service.getPromotionCode();

		if (promotionCode != null) {
			if (promotionCode.isBlank()) {
				super.state(context, false, "promotionCode", "javax.validation.constraints.NotBlank.message");
				return false;
			}
			boolean containsYear;
			String currentYear = String.valueOf(MomentHelper.getCurrentMoment().getYear());
			String year = currentYear.substring(currentYear.length() - 2);
			containsYear = StringHelper.endsWith(promotionCode, year, false); // Comprueba que termine con los 2 dígitos del año
			super.state(context, containsYear, "promotionCode", "acme.validation.service.promotioncode.year.message");

			boolean uniquePromotionCode;
			Service existingService;

			existingService = this.repository.findServiceByPromotionCode(promotionCode);
			uniquePromotionCode = existingService == null || existingService.equals(service);

			super.state(context, uniquePromotionCode, "promotionCode", "acme.validation.service.duplicated-promotionCode.message");
		}

		return !super.hasErrors(context);
	}
}
