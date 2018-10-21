package uk.ac.warwick.dcs.sherlock.engine.request;

import uk.ac.warwick.dcs.sherlock.api.annotations.RequestProcessor;
import uk.ac.warwick.dcs.sherlock.api.request.IRequestReference;
import uk.ac.warwick.dcs.sherlock.api.request.RequestInvocation;

/**
 * The idea is to integrate the request processor stuff into classes which handle data, and create an indirect access of them
 */
@RequestProcessor(apiFieldName = "dataRequestProcessor")
public class DataRequestProcessor {

	@RequestProcessor.Instance
	static DataRequestProcessor instance;

	public DataRequestProcessor() {

	}

	@RequestProcessor.PostHandler
	public void handlePost(IRequestReference reference, RequestInvocation source, Object payload) {
		System.out.println("hi: " + payload.toString());

		if (source != null) {
			source.respond(reference, "successful");
		}
	}

}
