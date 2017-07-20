package com.github.bordertech.wcomponents;

import com.github.bordertech.wcomponents.container.ValidateXMLInterceptor;
import com.github.bordertech.wcomponents.util.DebugUtil;
import com.github.bordertech.wcomponents.util.XMLUtil;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A class used to make sure the HTML/XML generated by each {@link WComponent} is well formed. {@link DebugUtil} is used
 * to determine if XML Validation is enabled. As each WComponent paints its output, and if XML validation is enabled,
 * WComponent calls this class to verify the HTML/XML. If a WComponent has badly formed HTML/XML, then an error message,
 * along with the component details are stored in a framework attribute to be retrieved later by the
 * {@link ValidateXMLInterceptor} so they can be reported in the response HTML.
 *
 * @author Jonathan Austin
 * @since 1.0.0
 */
public final class DebugValidateXML {

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(DebugValidateXML.class);

	/**
	 * Hide the constructor as there are no instance methods.
	 */
	private DebugValidateXML() {
	}

	/**
	 * Check if Validate XML is enabled.
	 *
	 * @return true or false
	 */
	public static boolean isEnabled() {
		return DebugUtil.isValidateXMLEnabled();
	}

	/**
	 * Validate the component to make sure the generated XML is schema compliant.
	 *
	 * @param xml the xml to validate
	 * @return Any errors found, or null if the XML is valid.
	 */
	public static String validateXMLAgainstSchema(final String xml) {
		// Validate XML against schema
		if (xml != null && !xml.equals("")) {
			// Wrap XML with a root element (if required)
			String testXML = wrapXMLInRootElement(xml);
			try {
				// Create SAX Parser Factory
				SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setNamespaceAware(true);
				spf.setValidating(true);

				// Create SAX Parser
				SAXParser parser = spf.newSAXParser();
				parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
						XMLConstants.W3C_XML_SCHEMA_NS_URI);

				// Set schema location
				Object schema = DebugValidateXML.class.getResource(getSchemaPath()).toString();
				parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schema);

				// Setup the handler to throw an exception when an error occurs
				DefaultHandler handler = new DefaultHandler() {
					@Override
					public void warning(final SAXParseException e) throws SAXException {
						LOG.warn("XML Schema warning: " + e.getMessage(), e);
						super.warning(e);
					}

					@Override
					public void fatalError(final SAXParseException e) throws SAXException {
						throw e;
					}

					@Override
					public void error(final SAXParseException e) throws SAXException {
						throw e;
					}
				};

				// Validate the XML
				InputSource xmlSource = new InputSource(new StringReader(testXML));
				parser.parse(xmlSource, handler);

			} catch (SAXParseException e) {
				return "At line " + e.getLineNumber() + ", column: " + e.getColumnNumber()
						+ " ==> " + e.getMessage();
			} catch (Exception e) {
				return e.getMessage();
			}
		}

		return null;
	}

	/**
	 * Wrap the XML in a root element before validating.
	 *
	 * @param xml the XML to wrap
	 * @return the XML wrapped in a root element
	 */
	public static String wrapXMLInRootElement(final String xml) {
		// The XML may not need to be wrapped.
		if (xml.startsWith("<?xml") || xml.startsWith("<!DOCTYPE")) {
			return xml;
		} else {
			// ENTITY definition required for NBSP.
			// ui namepsace required for xml theme.
			return XMLUtil.XML_DECLARATION + "<ui:root" + XMLUtil.STANDARD_NAMESPACES + ">" + xml + "</ui:root>";
		}
	}

	/**
	 * Return the path to the schema to test against. This can be overridden if client applications wish to validate
	 * their own components.
	 *
	 * @return the schema path for the current theme in use.
	 */
	protected static String getSchemaPath() {
		return "/schema/ui/v1/schema.xsd";
	}
}
