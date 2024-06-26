package uk.ac.warwick.dcs.sherlock.module.web.exceptions

/**
 * Thrown if the user attempts to run a template when there are no detectors
 */
class TemplateContainsNoDetectors(errorMessage: String) : RuntimeException(errorMessage)