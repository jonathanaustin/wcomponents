define(["wc/i18n/i18n",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/ui/getFirstLabelForElement",
	"wc/ui/validation/isComplete",
	"wc/ui/validation/validationManager",
	"wc/ui/validation/required",
	"wc/ui/fieldset"],
	function(i18n, initialise, shed, getFirstLabelForElement, isComplete, validationManager, required, fieldset) {
		"use strict";
		/**
		 * @constructor
		 * @alias module:wc/ui/validation/fieldset~ValidationFieldset
		 * @private
		 */
		function ValidationFieldset() {
			var FIELDSET = fieldset.getWidget(),
				INVALID;

			/**
			 * This is an Array.filter filter function which should return true only if the fieldset is NOT in a
			 * successful state. A fieldset is successful if at least one interactive control within the fieldset is
			 * complete. Therefore it is not successful only if EVERY interactive control is not complete.
			 *
			 * @function
			 * @private
			 * @param {Element} element A FIELDSET element
			 * @returns {boolean} true if the fieldset is not complete.
			 */
			function filterFieldsets(element) {
				return !isComplete.isContainerComplete(element);
			}

			/**
			 * Array.forEach iteration function used to flag fieldsets which are not valid.
			 *
			 * @function
			 * @private
			 * @param {Element} fset The fieldset on which we want to flag a validation error.
			 */
			function flagError(fset) {
				var legend = getFirstLabelForElement(fset, true) || fset.title,
					message = i18n.get("validation_common_incompletegroup", legend);

				validationManager.flagError({element: fset,
											message: message,
											position: "beforeEnd"});
			}

			/**
			 * Fieldset required state validation a fieldset is successful if at least one interactive control within
			 * the fieldset is complete.
			 *
			 * @function
			 * @private
			 * @param {Element} container The DOM element being validated.
			 * @returns {boolean} true if container is valid.
			 */
			function validate(container) {
				var result = true,
					elements = required.getRequired(container, FIELDSET, required.CONSTRAINTS.CLASSNAME);
				// are any not complete?
				if (elements && (elements = elements.filter(filterFieldsets)) && elements.length) {
					result = false;
					elements.forEach(flagError);  // we have a real array after calling filter
				}
				return result;
			}

			/**
			 * This function determines if a fieldset needs to be revalidated and if it does then it resets the
			 * validation. *NOTE:* WFieldSet only needs validation if "required".
			 *
			 * * If something is shown or enabled inside an invalid fieldset it may be populated, making the fieldset valid;
			 * * if something is hidden or disabled inside an invalid fieldset it may make the fieldset 'empty' thereby making the fieldset valid.
			 *
			 * In both cases we need to revalidate to make sure.
			 *
			 * If something changes inside an invalid fieldset we also need to revalidate the fieldset. This is done by having this module subscribe
			 * to validationManager.
			 *
			 * @function
			 * @private
			 * @param {Element} element a control which may be inside an invalid fieldset.
			 */
			function revalidate(element) {
				var container, result = true, initiallyInvalid;

				INVALID = INVALID || FIELDSET.extend("wc_req", {"aria-invalid": "true"});
				container = INVALID.isOneOfMe(element) ? element : INVALID.findAncestor(element);

				while (container) {
					initiallyInvalid = validationManager.isInvalid(container);
					result = validate(container);

					if (result) {
						if (initiallyInvalid) {
							validationManager.setOK(container);
						}
						container = INVALID.findAncestor(container.parentNode);
					}
					else {
						break;  // if the innermost invalid fieldset is still invalid there is no point traversing
					}
				}
			}

			/**
			 * Subscriber for {@link module:wc/dom/shed} functions which affect the validity of fieldsets.
			 *
			 * @function
			 * @private
			 * @param {Element} element The element acted on by shed.
			 */
			function validationShedSubscriber(element) {
				var fieldset;
				if (element && (fieldset = FIELDSET.findAncestor(element))) {
					revalidate(fieldset);
				}
			}

			/**
			 * Initialise callback.
			 *
			 * @function module:wc/ui/validation/fieldset.postInit
			 */
			this.postInit = function() {
				validationManager.subscribe(validate);
				validationManager.subscribe(revalidate, true);
				shed.subscribe(shed.actions.SELECT, validationShedSubscriber);
				shed.subscribe(shed.actions.DESELECT, validationShedSubscriber);
				shed.subscribe(shed.actions.ENABLE, validationShedSubscriber);
				shed.subscribe(shed.actions.DISABLE, validationShedSubscriber);
				shed.subscribe(shed.actions.SHOW, validationShedSubscriber);
				shed.subscribe(shed.actions.HIDE, validationShedSubscriber);
			};
		}
		/**
		 * Provides functionality to undertake client validation for WFieldSet.
		 *
		 * @module
		 * @requires module:wc/i18n/i18n
		 * @requires module:wc/dom/initialise
		 * @requires module:wc/dom/shed
		 * @requires module:wc/ui/getFirstLabelForElement
		 * @requires module:wc/ui/validation/isComplete
		 * @requires module:wc/ui/validation/validationManager
		 * @requires module:wc/ui/validation/required
		 * @requires module:wc/ui/fieldset
		 */
		var instance = new ValidationFieldset();
		initialise.register(instance);
		return instance;
	});
