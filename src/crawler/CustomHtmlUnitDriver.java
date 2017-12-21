package crawler;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;

public class CustomHtmlUnitDriver extends HtmlUnitDriver {
	public CustomHtmlUnitDriver() {
		super();
		this.getWebClient().setCssErrorHandler(new SilentCssErrorHandler());
	}

	@Override
	protected WebClient modifyWebClient(WebClient client) {
		WebClient modifiedClient = super.modifyWebClient(client);
		modifiedClient.getOptions().setThrowExceptionOnScriptError(false);
		return modifiedClient;
	}
}