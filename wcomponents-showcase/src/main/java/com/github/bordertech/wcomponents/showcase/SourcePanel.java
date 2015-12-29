package com.github.bordertech.wcomponents.showcase;

import com.github.bordertech.wcomponents.InternalResource;
import com.github.bordertech.wcomponents.RenderContext;
import com.github.bordertech.wcomponents.Request;
import com.github.bordertech.wcomponents.UIContext;
import com.github.bordertech.wcomponents.UIContextHolder;
import com.github.bordertech.wcomponents.WContent;
import com.github.bordertech.wcomponents.WPanel;
import com.github.bordertech.wcomponents.WText;
import com.github.bordertech.wcomponents.WebUtilities;
import com.github.bordertech.wcomponents.servlet.WebXmlRenderContext;
import com.github.bordertech.wcomponents.showcase.util.SourceUtil;
import com.github.bordertech.wcomponents.showcase.widgets.Showcase;
import com.github.bordertech.wcomponents.util.Config;
import java.io.PrintWriter;

/**
 * This component displays java source code.
 *
 * @author Jonathan Austin
 */
public class SourcePanel extends WPanel {

	/**
	 * The source code.
	 */
	private final WText source = new WText();

	/**
	 * Additional Javascript used to provide syntax-highlighting client-side.
	 */
	private final WContent javascript = new WContent();

	/**
	 * Additional CSS used to provide syntax-highlighting client-side.
	 */
	private final WContent css = new WContent();

	/**
	 * Creates a SourcePanel.
	 */
	public SourcePanel() {

		setBeanProperty(".");

		source.setEncodeText(false);
		add(source);

		String version = Config.getInstance().getString("wcomponents-examples.version");
		javascript.setCacheKey("wc.showcase.js." + version);
		css.setCacheKey("wc.showcase.css." + version);

		javascript.setContentAccess(new InternalResource("/js/syntaxHighlight.js", "syntaxHighlight.js"));
		css.setContentAccess(new InternalResource("/css/syntaxHighlight.css", "syntaxHighlight.css"));
		add(javascript);
		add(css);

	}

	/**
	 * Sets the source code to be displayed in the panel.
	 *
	 * @param sourceText the source code to display.
	 */
	public void setSource(final String sourceText) {
		String formatted = SourceUtil.formatSource(sourceText);
		source.setText(formatted);
	}

	/**
	 * Override preparePaint in order to set up the resources on first access by a user.
	 *
	 * @param request the request being responded to.
	 */
	@Override
	protected void preparePaintComponent(final Request request) {
		super.preparePaintComponent(request);

		if (!isInitialised()) {
			Showcase item = getShowcase();
			String formatted = SourceUtil.formatSource(item.getPseudoCode());
			source.setText(formatted);
		}

		UIContext uic = UIContextHolder.getCurrent();
		uic.getHeaders().addUniqueHeadLine("<script type='text/javascript' src='" + WebUtilities.
				encode(javascript.getUrl()) + "'></script>");
		uic.getHeaders().addUniqueHeadLine(
				"<link type='text/css' rel='stylesheet' href='" + WebUtilities.encode(css.getUrl()) + "'></link>");
	}

	/**
	 * Override afterPaint in order to render the additional mark-up required for client-side syntax highlighting.
	 *
	 * @param renderContext the renderContext to send output to.
	 */
	@Override
	protected void afterPaint(final RenderContext renderContext) {
		super.afterPaint(renderContext);

		PrintWriter writer = ((WebXmlRenderContext) renderContext).getWriter();
		// Kick of the syntax highlighting
		writer.write(
				"<script type='text/javascript'>if (window.doHighlighting) doHighlighting('" + getId() + "');</script>");
	}

	private Showcase getShowcase() {
		return (Showcase) getBean();
	}

}
