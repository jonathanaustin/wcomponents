package com.github.bordertech.wcomponents.test.selenium;

import com.github.bordertech.wcomponents.test.selenium.element.WDialogWebElement;
import com.github.bordertech.wcomponents.util.Config;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.BooleanUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * <p>
 * Utility class containing convenience methods for testing WComponents with
 * Selenium.</p>
 * <p>
 * Logic has been extracted into this utility class for any consumers who cannot
 * extend WComponentSeleniumTestCase due to a different test class
 * hierarchy.</p>
 *
 * @author Joshua Barclay
 * @since 1.2.0
 */
public final class WComponentSelenium {

	/**
	 * Prefix for parameters used by this class.
	 */
	private static final String PARAM_PREFIX = "com.github.bordertech.wcomponents.test.selenium.";
	/**
	 * The body tag indicating the page is ready.
	 */
	private static final String DATA_READY_TAG = Config.getInstance().getString(PARAM_PREFIX + "page_ready_attribute", "data-wc-domready");

	/**
	 * The default page-ready timeout duration.
	 */
	private static final int PAGE_READY_WAIT_TIMEOUT = Config.getInstance().getInt(PARAM_PREFIX + "page_ready_timeout", 5);

	/**
	 * The default page-ready poll interval (milliseconds).
	 */
	private static final long PAGE_READY_POLL_INTERVAL = Config.getInstance().getLong(PARAM_PREFIX + "page_ready_poll_interval", 50);

	/**
	 * The number of seconds to wait for an element to be available.
	 */
	private static final long IMPLICIT_WAIT_SECONDS = Config.getInstance().getLong(PARAM_PREFIX + "implicit_wait", 5);

	/**
	 * The screen width in pixels.
	 */
	private static final int SCREEN_WIDTH = Config.getInstance().getInt(PARAM_PREFIX + "screen_width", 1920);

	/**
	 * The screen height in pixels.
	 */
	private static final int SCREEN_HEIGHT = Config.getInstance().getInt(PARAM_PREFIX + "screen_height", 1080);

	/**
	 * The expected condition for a page being ready.
	 */
	private static final ExpectedCondition<Boolean> PAGE_WAIT_CONDITION = new ExpectedCondition<Boolean>() {
		/**
		 * Wait for the WComponents page to be ready.
		 *
		 * @param driver - the web driver
		 * @return true when the page is ready, false otherwise.
		 */
		@Override
		public Boolean apply(final WebDriver driver) {

			if (driver == null) {
				throw new IllegalArgumentException("a driver must be provided.");
			}

			WebElement body = driver.findElement(By.tagName("body"));
			String domReadyAttr = body.getAttribute(DATA_READY_TAG);
			final boolean domReady = BooleanUtils.isTrue(BooleanUtils.toBooleanObject(domReadyAttr));

			return domReady;

		}
	};

	/**
	 * Configure the WebDriver with the standard WComponents configuration.
	 *
	 * @param driver the WebDriver to configure.
	 */
	public static void configureDriver(final WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_SECONDS, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		driver.manage().window().fullscreen();
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript. Uses
	 * default values for timeout and polling interval.
	 *
	 * @param driver the WebDriver.
	 */
	public static void waitForPageReady(final WebDriver driver) {

		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		WComponentSelenium.waitForPageReady(driver, PAGE_READY_WAIT_TIMEOUT, PAGE_READY_POLL_INTERVAL);
	}

	/**
	 * Wait for the page to have loaded, including all AJAX and JavaScript.
	 *
	 * @param driver the WebDriver.
	 * @param timeoutSeconds - the number of seconds after which the 'wait' will
	 * time out.
	 * @param pollingMilliseconds - the number of milliseconds to wait between
	 * each poll attempt.
	 */
	public static void waitForPageReady(final WebDriver driver, final int timeoutSeconds, final long pollingMilliseconds) {

		if (driver == null) {
			throw new IllegalArgumentException("a driver must be provided.");
		}

		WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
		wait.pollingEvery(pollingMilliseconds, TimeUnit.MILLISECONDS);
		wait.until(getPageReadyCondition());
	}

	/**
	 * Is there an open dialog on the screen?
	 *
	 * @param driver the WebDriver.
	 * @return true if an open dialog exists, else false.
	 */
	public static boolean isOpenDialog(final WebDriver driver) {
		try {
			driver.findElement(By.cssSelector(WDialogWebElement.getOpenDialogCssSelector()));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Get the screen's dialog, whether it is open or not.
	 *
	 * @param driver the WebDriver.
	 *
	 * @return a WDialogWebElement for the dialog.
	 */
	public static WDialogWebElement getDialog(final WebDriver driver) {
		WebElement dialog = driver.findElement(By.cssSelector(WDialogWebElement.getDialogCssSelector()));
		return new WDialogWebElement(dialog);
	}

	/**
	 * Get the ExpectedCondition for waiting for the WComponents page to be
	 * ready.
	 *
	 * @return the WaitCondition for page ready.
	 */
	public static ExpectedCondition<Boolean> getPageReadyCondition() {
		return PAGE_WAIT_CONDITION;
	}

	/**
	 * Default constructor is hidden. Static utility class.
	 */
	private WComponentSelenium() {
		//No impl
	}

}
