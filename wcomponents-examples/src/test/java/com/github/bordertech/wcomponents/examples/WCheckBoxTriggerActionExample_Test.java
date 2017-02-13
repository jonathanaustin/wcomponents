package com.github.bordertech.wcomponents.examples;

import com.github.bordertech.wcomponents.test.selenium.MultiBrowserRunner;
import com.github.bordertech.wcomponents.test.selenium.driver.SeleniumWComponentsWebDriver;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * Selenium unit tests for {@link WCheckBoxTriggerActionExample}.
 *
 * @author Yiannis Paschalidis
 * @author Mark Reeves
 * @since 1.0.0
 */
@Category(SeleniumTests.class)
@RunWith(MultiBrowserRunner.class)
public class WCheckBoxTriggerActionExample_Test extends WComponentExamplesTestCase {

	/**
	 * Creates a new WCheckBoxTriggerActionExample_Test.
	 */
	public WCheckBoxTriggerActionExample_Test() {
		super(new WCheckBoxTriggerActionExample());
	}

	@Test
	public void testExample() {
		// Launch the web browser to the LDE
		SeleniumWComponentsWebDriver driver = getDriver();

		WCheckBoxTriggerActionExample ui = (WCheckBoxTriggerActionExample) getUi();

		// Select "Breakfast"
		driver.findWCheckBox(byWComponent(ui.getBreakfastCheckBox())).click();
		Assert.assertEquals("Should have submitted 'Breakfast' to server", "Breakfast selected", getMessageText());

		// Select "Lunch"
		driver.findWCheckBox(byWComponent(ui.getLunchCheckBox())).click();
		Assert.assertEquals("Should have submitted 'Lunch' to server", "Lunch selected", getMessageText());

		// Select "Dinner"
		driver.findWCheckBox(byWComponent(ui.getDinnerCheckBox())).click();
		Assert.assertEquals("Should have submitted 'Dinner' to server", "Dinner selected", getMessageText());

		// De-select "Lunch"
		driver.findWCheckBox(byWComponent(ui.getLunchCheckBox())).click();
		Assert.assertEquals("Should have submitted 'Lunch' to server", "Lunch unselected", getMessageText());
	}

	/**
	 * @return the message currently being displayed by the example.
	 */
	private String getMessageText() {
		WebDriver driver = getDriver();
		WCheckBoxTriggerActionExample ui = (WCheckBoxTriggerActionExample) getUi();

		return driver.findElement(byWComponent(ui.getInformationTextBox())).getText().trim();
	}
}
