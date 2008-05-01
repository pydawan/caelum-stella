package br.com.caelum.stella.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import br.com.caelum.stella.MessageProducer;

public class InscricaoEstatudalDeRioDeJaneiroValidator extends
		AbstractValidator<String> {

	private static final int MOD = 11;

	// TAMANHO = 8;
	private static final String MISSING_LEFT_SIDE_ZEROS = "000000";

	private static final int DVX_POSITION = MISSING_LEFT_SIDE_ZEROS.length() + 8;
	private static final Integer[] DVX_MULTIPLIERS = IEConstraints.P8;

	private static final RotinaDeDigitoVerificador[] rotinas = {
			IEConstraints.Rotina.E, IEConstraints.Rotina.POS_IE };

	private static final DigitoVerificadorInfo DVX_INFO = new DigitoVerificadorInfo(
			0, rotinas, MOD, DVX_MULTIPLIERS, DVX_POSITION);

	private static final ValidadorDeDV DVX_CHECKER = new ValidadorDeDV(DVX_INFO);

	private final boolean isFormatted;

	public static final Pattern FORMATED = Pattern
			.compile("([1789]\\d{1})[.](\\d{3})[.](\\d{3})");
	public static final Pattern UNFORMATED = Pattern
			.compile("([1789]\\d{1})(\\d{3})(\\d{3})");

	public InscricaoEstatudalDeRioDeJaneiroValidator(
			MessageProducer messageProducer, boolean isFormatted) {
		super(messageProducer);
		this.isFormatted = isFormatted;
	}

	@Override
	protected List<InvalidValue> getInvalidValues(String IE) {
		List<InvalidValue> errors = new ArrayList<InvalidValue>();
		errors.clear();
		if (IE != null) {
			String unformatedIE = checkForCorrectFormat(IE, errors);
			if (errors.isEmpty()) {
				if (!hasValidCheckDigits(unformatedIE)) {
					errors.add(IEError.INVALID_CHECK_DIGITS);
				}
			}
		}
		return errors;
	}

	private String checkForCorrectFormat(String ie, List<InvalidValue> errors) {
		String unformatedIE = null;
		if (isFormatted) {
			if (!(FORMATED.matcher(ie).matches())) {
				errors.add(IEError.INVALID_FORMAT);
			}
			unformatedIE = ie.replaceAll("\\D", "");
		} else {
			if (!UNFORMATED.matcher(ie).matches()) {
				errors.add(IEError.INVALID_DIGITS);
			}
			unformatedIE = ie;
		}
		return unformatedIE;
	}

	private boolean hasValidCheckDigits(String value) {
		String testedValue = MISSING_LEFT_SIDE_ZEROS + value;
		return DVX_CHECKER.DVisValid(testedValue);
	}

}
