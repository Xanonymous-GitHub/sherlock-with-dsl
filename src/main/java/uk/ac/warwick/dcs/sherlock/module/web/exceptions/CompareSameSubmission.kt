package uk.ac.warwick.dcs.sherlock.module.web.exceptions

/**
 * Thrown if the user tries to compare a submission against itself
 */
class CompareSameSubmission(errorMessage: String) : RuntimeException(errorMessage)