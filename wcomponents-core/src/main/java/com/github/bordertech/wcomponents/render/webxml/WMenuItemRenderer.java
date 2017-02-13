package com.github.bordertech.wcomponents.render.webxml;

import com.github.bordertech.wcomponents.MenuSelectContainer;
import com.github.bordertech.wcomponents.WComponent;
import com.github.bordertech.wcomponents.WMenuItem;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.XmlStringBuilder;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;

/**
 * The Renderer for {@link WMenuItem}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
final class WMenuItemRenderer extends AbstractWebXmlRenderer {

	/**
	 * The selection mode of the menu item.
	 * @param item the WMenuItem to test
	 * @return the selection mode if any
	 */
	private String getRole(final WMenuItem item) {
		if (!item.isSelectAllowed()) {
			return "menuitem";
		}
		MenuSelectContainer selectContainer = WebUtilities.getAncestorOfClass(MenuSelectContainer.class, item);
		if (selectContainer == null) {
			return "menuitem";
		}
		return selectContainer.getSelectionMode() == MenuSelectContainer.SelectionMode.MULTIPLE ? "menuitemcheckbox" : "menuitemradio";
	}

	/**
	 * Paints the given WMenuItem.
	 *
	 * @param component the WMenuItem to paint.
	 * @param renderContext the RenderContext to paint to.
	 */
	@Override
	public void doRender(final WComponent component, final WebXmlRenderContext renderContext) {
		WMenuItem item = (WMenuItem) component;
		XmlStringBuilder xml = renderContext.getWriter();

		xml.appendTagOpen("ui:menuitem");
		xml.appendAttribute("id", component.getId());
		xml.appendOptionalAttribute("class", component.getHtmlClass());
		xml.appendOptionalAttribute("track", component.isTracking(), "true");

		if (item.isSubmit()) {
			xml.appendAttribute("submit", "true");
		} else {
			xml.appendOptionalAttribute("url", item.getUrl());
			xml.appendOptionalAttribute("targetWindow", item.getTargetWindow());
		}

		xml.appendOptionalAttribute("disabled", item.isDisabled(), "true");
		xml.appendOptionalAttribute("hidden", item.isHidden(), "true");
		xml.appendOptionalAttribute("selected", item.isSelected(), "true");
		xml.appendOptionalAttribute("selectable", item.isSelectAllowed(), "true");
		xml.appendOptionalAttribute("role", getRole(item));
		xml.appendOptionalAttribute("cancel", item.isCancel(), "true");
		xml.appendOptionalAttribute("msg", item.getMessage());
		xml.appendOptionalAttribute("toolTip", item.getToolTip());

		if (item.isTopLevelItem()) {
			xml.appendOptionalAttribute("accessKey", item.getAccessKeyAsString());
		}

		xml.appendClose();

		item.getDecoratedLabel().paint(renderContext);

		xml.appendEndTag("ui:menuitem");
	}
}
