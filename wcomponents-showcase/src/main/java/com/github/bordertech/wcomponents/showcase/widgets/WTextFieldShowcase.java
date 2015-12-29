package com.github.bordertech.wcomponents.showcase.widgets;

import com.github.bordertech.wcomponents.Action;
import com.github.bordertech.wcomponents.ActionEvent;
import com.github.bordertech.wcomponents.WAjaxControl;
import com.github.bordertech.wcomponents.WCheckBox;
import com.github.bordertech.wcomponents.WContainer;
import com.github.bordertech.wcomponents.WFieldLayout;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WTextField;

/**
 *
 * @author jonathan
 */
public class WTextFieldShowcase extends AbstractShowcase<WTextField> {

	public WTextFieldShowcase() {
		super(WTextField.class);
	}

	@Override
	public WidgetContainer getWidgetContainerInstance() {
		return new WidgetPanel();
	}

	@Override
	public PropertyContainer getPropertyContainerInstance(final WidgetContainer<WTextField> itemPanel) {
		return new PropertiesPanel(itemPanel.getWidget());
	}

	@Override
	public String getPseudoCode() {
		return "package my.sample;";
	}

	public static class WidgetPanel extends WPanel implements WidgetContainer<WTextField> {

		private final WTextField widget = new WTextField();

		public WidgetPanel() {
			WFieldLayout layout = new WFieldLayout();
			add(layout);
			layout.addField("Textfield", widget);
		}

		@Override
		public WTextField getWidget() {
			return widget;
		}
	}

	public static class PropertiesPanel extends WPanel implements PropertyContainer {

		private final WFieldLayout layout = new WFieldLayout();

		private final WContainer ajaxContainer = new WContainer();

		public PropertiesPanel(final WTextField widget) {

			add(layout);
			add(ajaxContainer);

			final WCheckBox chb = new WCheckBox();
			layout.addField("Readonly", chb);
			ajaxContainer.add(new WAjaxControl(chb, widget));
			chb.setActionOnChange(new Action() {
				@Override
				public void execute(ActionEvent event) {
					widget.setReadOnly(chb.isSelected());
				}
			});

			final WCheckBox chb2 = new WCheckBox();
			layout.addField("Disabled", chb2);
			ajaxContainer.add(new WAjaxControl(chb2, widget));
			chb2.setActionOnChange(new Action() {
				@Override
				public void execute(ActionEvent event) {
					widget.setDisabled(chb2.isSelected());
				}
			});
		}

	}

}
