define(["wc/dom/group",
	"wc/dom/initialise",
	"wc/dom/shed",
	"wc/dom/Widget",
	"wc/dom/getFilteredGroup",
	"wc/dom/cbrShedPublisher",
	"wc/ui/fieldset"],
function(group, initialise, shed, Widget, getFilteredGroup, cbrShedPublisher) {
	"use strict";

	// Note `wc/ui/fieldset` is implicitly required to handle various aspects of managing the wrapper element.

	/**
	 * @constructor
	 * @alias module:wc/ui/radioButtonSelect~RadioButtonGroup
	 * @private
	 */
	function RadioButtonGroup() {
		var RADIO = cbrShedPublisher.getWidget("r"),
			RADIO_BUTTON_SELECT = new Widget("fieldset", "wc-radiobuttonselect");

		/**
		 * Listen for mandatory/optional and set the group's radio buttons.
		 * @function
		 * @private
		 * @param {Element} element The element being acted upon.
		 * @param {String} action One of the {@link module:wc/dom/shed~actions}: MANDATORY or OPTIONAL
		 */
		function shedSubscriber(element, action) {
			if (element && RADIO_BUTTON_SELECT.isOneOfMe(element)) {
				group.getGroup(element, RADIO).forEach(function (next) {
					shed[action](next);
				});
			}
		}

		/**
		 * Get the widget which describes a radioButtonSelect.
		 * @function
		 * @public
		 * @returns {module:wc/dom/Widget}
		 */
		this.getWidget = function() {
			return RADIO_BUTTON_SELECT;
		};

		/**
		 * Get the widget which describes a single control in a radioButtonSelect.
		 * @function module:wc/ui/radioButtonSelect.getInputWidget
		 * @returns {module:wc/dom/Widget}
		 */
		this.getInputWidget = function() {
			return RADIO;
		};

		/**
		 * Allow an external component to set selection of a radio button in a RadioButtonSelect using a value.
		 * @function module:wc/ui/radioButtonSelect.setSelectionByValue
		 * @public
		 * @param {HTMLElement} element The radioButtonSelect.
		 * @param {String} value the value of the radio button to select.
		 */
		this.setSelectionByValue = function(element, value) {
			var option, _group, i;
			if (RADIO_BUTTON_SELECT.isOneOfMe(element)) {
				if (!value) {
					// deselectAll
					getFilteredGroup(element).forEach(function(next) {
						// should be only one
						shed.deselect(next);
					});
				} else {
					_group = group.getGroup(element, RADIO);
					for (i = 0; i < _group.length; ++i) {
						option = _group[i];
						if (option.value === value) {
							shed.select(option);
							break;
						}
					}
				}
			}
		};

		/**
		 * Late setup - subscribers to {@link module:wc/dom/shed}.
		 * @function module:wc/ui/radioButtonSelect.postInit
		 * @public
		 */
		this.postInit = function () {
			shed.subscribe(shed.actions.MANDATORY, shedSubscriber);
			shed.subscribe(shed.actions.OPTIONAL, shedSubscriber);
		};
	}

	/**
	 * Provides functionality for groups of radio buttons generated by a WRadioButtonSelect.
	 *
	 * @module
	 * @requires module:wc/dom/group
	 * @requires module:wc/dom/initialise
	 * @requires module:wc/dom/shed
	 * @requires module:wc/dom/Widget
	 * @requires module:wc/dom/getFilteredGroup
	 * @requires module:wc/dom/cbrShedPublisher
	 */
	return initialise.register(new RadioButtonGroup());
});
