package uk.ac.warwick.dcs.sherlock.engine.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.component.*;
import uk.ac.warwick.dcs.sherlock.api.report.IReportGenerator;
import uk.ac.warwick.dcs.sherlock.api.report.IReportManager;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.util.*;

/**
 * A class to handle report generation in general (does not generate fileReportMap itself).
 * <p>
 * It takes all possible inputs that may be relevant from postprocessing, and handles requests for fileReportMap; sending the relevant information to the actual report generator in use.
 */
public class ReportManager implements IReportManager<SubmissionMatchGroup, SubmissionSummary> {

    /**
     * Logger instance
     */
    private final Logger logger = LoggerFactory.getLogger(ReportManager.class);

    /**
     * Contains all the information needed to generate reports from.
     */
    private final IResultJob results;

    /**
     * A map of all the individual files according to their persitent ids.
     */
    private final Map<Long, ISourceFile> fileMap;

    /**
     * Maps submission ids to a list of file ids contained by that submission.
     */
    private final Map<Long, List<Long>> submissionFileMap;

    /**
     * Maps submission ids to their overall scores
     */
    private final Map<Long, Float> submissionScores;

    /**
     * Maps pairs of file ids to the relative scores between them
     */
    private final Map<List<Long>, Float> relativeFileScores;

    /**
     * The object used to generate the report information.
     */
    private final IReportGenerator reportGenerator;

    /**
     * Initialises the report manager with a set of results.
     *
     * @param results Contains a full set of results from postprocessing; all useful info is extracted from here
     */
    public ReportManager(IResultJob results) {
        this.reportGenerator = new ReportGenerator();

        this.submissionFileMap = new HashMap<>();
        this.fileMap = new HashMap<>();
        this.results = results;
        this.submissionScores = new HashMap<>();
        this.relativeFileScores = new HashMap<>();
        this.results.getFileResults().forEach(resultFile -> this.fileMap.put(resultFile.getFile().getPersistentId(), resultFile.getFile()));
        this.results.getFileResults().forEach(resultFile -> resultFile.getFileScores().keySet().forEach(file2 -> this.relativeFileScores.put(new ArrayList<>(Arrays.asList(resultFile.getFile().getPersistentId(), file2.getPersistentId())), resultFile.getFileScore(file2))));
        this.results.getFileResults().forEach(resultFile -> this.submissionScores.put(resultFile.getFile().getSubmission().getId(), resultFile.getOverallScore()));

        FillSubmissionFileMap();
    }

    /**
     * Retrieve every ICodeBlockGroup stored in results.
     *
     * @return a list of ICodeBlockGroups.
     */
    private List<ICodeBlockGroup> GetCodeBlockGroups() {
        List<ICodeBlockGroup> codeBlockGroups = new ArrayList<>();
        this.results.getFileResults().stream().flatMap(file -> file.getTaskResults().stream()).filter(task -> task.getContainingBlocks() != null)
                .forEach(task -> codeBlockGroups.addAll(task.getContainingBlocks()));
        return codeBlockGroups;
    }

    /**
     * Retrieve every ICodeBlockGroup with a file from the specified submission.
     *
     * @param submission the submission to find relevant ICodeBlockGroups for.
     * @return a list of relevant ICodeBlockGroups.
     */
    private List<ICodeBlockGroup> GetCodeBlockGroups(ISubmission submission) {
        List<ICodeBlockGroup> codeBlockGroups = new ArrayList<>();
        this.results.getFileResults().stream().flatMap(file -> file.getTaskResults().stream()).filter(task -> task.getContainingBlocks() != null)
                .flatMap(task -> task.getContainingBlocks().stream().filter(group -> group.submissionIdPresent(submission.getId()))).forEach(group -> codeBlockGroups.add(group));
        return codeBlockGroups;
    }

    /**
     * Retrieve every ICodeBlockGroup with a file from both of the specified submissions.
     *
     * @param submissions the submissions to find relevant ICodeBlockGroups for.
     * @return a list of relevant ICodeBlockGroups.
     */
    private List<ICodeBlockGroup> GetCodeBlockGroups(List<ISubmission> submissions) {
        List<ICodeBlockGroup> codeBlockGroups = new ArrayList<>();
        this.results.getFileResults().stream().flatMap(file -> file.getTaskResults().stream()).filter(task -> task.getContainingBlocks() != null)
                .flatMap(task -> task.getContainingBlocks().stream().filter(group -> group.submissionIdPresent(submissions.getFirst().getId()) && group.submissionIdPresent(submissions.getFirst().getId()))).forEach(group -> codeBlockGroups.add(group));
        return codeBlockGroups;
    }

    /**
     * Calculates the relative score between two submissions, by taking the average of the maximum relative scores between each individual file within them.
     * NOTE FOR FUTURE DEVS: This is not necessarily the best way to calculate this score, and it should probably be handled elsewhere, so please consider changing it.
     *
     * @param sub_id1 the id of the first submission.
     * @param sub_id2 the id of the second submission.
     * @return a float relative score between the two submissions.
     */
    private float GetSubmissionRelativeScore(long sub_id1, long sub_id2) {
        //use to calculate the average of the max relative file scores
        float total_max_score_tally = 0f;
        int file_count = 0;

        //Go through all the files in the first submission
        for (Long id1 : submissionFileMap.get(sub_id1)) {
            //For each file, look at its relative score with each file in the other submission and get the largest
            float max_score = 0f;
            for (Long id2 : submissionFileMap.get(sub_id2)) {
                List<Long> id_tuple = new ArrayList<>(Arrays.asList(id1, id2));

                //If there's a relative score between these files, retrieve it, otherwise set it to 0.
                float score = 0f;
                if (relativeFileScores.containsKey(id_tuple))
                    score = relativeFileScores.get(id_tuple);

                if (score > max_score)
                    max_score = score;
            }

            total_max_score_tally += max_score;
            file_count++;
        }

        //Avoid division by zero though this shouldn't happen unless a submission doesn't have anything in it
        if (file_count == 0)
            return 0f;

        //Return the average maximum score.
        return total_max_score_tally / (float) file_count;
    }

    /**
     * Fill in submissionFileMap based off of the contents of fileMap.
     */
    private void FillSubmissionFileMap() {
        for (ISourceFile file : this.fileMap.values()) {
            //If this is the first file of this submission seen, create a new list and put it into submissionFileMap.
            //otherwise add to the existing list.
            if (submissionFileMap.get(file.getSubmission().getId()) == null) {
                ArrayList<Long> idList = new ArrayList<>();
                idList.add(file.getPersistentId());
                submissionFileMap.put(file.getSubmission().getId(), idList);
            } else {
                submissionFileMap.get(file.getSubmission().getId()).add(file.getPersistentId());
            }
        }
    }

    /**
     * To be called by the web report pages. Gets a list of submission summaries.
     *
     * @return a list of the matching SubmissionSummaries, each containing their ids, overall scores, and a list of the submissions that they were matched with.
     */
    @Override
    public List<SubmissionSummary> GetMatchingSubmissions() {
        ArrayList<SubmissionSummary> output = new ArrayList<>();

        for (Long submissionId : submissionFileMap.keySet()) {
            float overallScore = submissionScores.get(submissionId);
            SubmissionSummary submissionSummary = new SubmissionSummary(submissionId, overallScore);
            ArrayList<Tuple<Long, Float>> matchingSubs = new ArrayList<>();
            ArrayList<Long> matchingSubIds = new ArrayList<>();

            //Look through all the code block groups to determine which submissions have been matched with which other submissions.
            for (ICodeBlockGroup codeBlockGroup : GetCodeBlockGroups()) {
                //If this group contains a file for the current submission, add all other submissions in the group to the list, if they aren't already added
                if (codeBlockGroup.submissionIdPresent(submissionId)) {
                    for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
                        long currentId = codeBlock.getFile().getSubmission().getId();
                        if (currentId != submissionId && !matchingSubIds.contains(currentId)) {
                            //Get the relative submission score
                            float relativeScore = GetSubmissionRelativeScore(submissionId, currentId);

                            matchingSubs.add(new Tuple<>(currentId, relativeScore));
                            matchingSubIds.add(currentId);
                        }
                    }
                }

            }
            submissionSummary.AddMatchingSubmissions(matchingSubs);
            output.add(submissionSummary);
        }

        return output;
    }

    /**
     * Compares two submissions, finds all the matches in files they contain between them, and returns all relevant information about them.
     *
     * @param submissions The submissions to compare (should be a list of two submissions only; any submissions beyond the first two are ignored)
     * @return A list of SubmissionMatchGroup objects which contain lists of SubmissionMatch objects; each have ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs.
     */
    @Override
    public List<SubmissionMatchGroup> GetSubmissionComparison(List<ISubmission> submissions) {
        if (submissions.stream().anyMatch(ISubmission::hasParent)) {
            // one of the submissions is not top level, don't make reports on non-top level submission.

            //Either just return null with an error msg
            logger.error("Cannot generate comparison on a submission which has a parent");
            return null;

            // or get the parent recursively and generate on that!
			/*ISubmission sub = ...
			while (sub.hasParent()) {
				sub = sub.getParent();
			}*/
        }

        if (submissions.size() < 2)
            return null;


        List<ICodeBlockGroup> relevantGroups = GetCodeBlockGroups(submissions);

        return reportGenerator.generateSubmissionComparison(submissions, relevantGroups);
    }

    /**
     * Generate a report for a single submission, containing all matches for all files within it, and a summary of the report as a string.
     *
     * @param submission The submission to generate the report for.
     * @return A tuple. The key contains a list of SubmissionMatchGroup objects which contain lists of SubmissionMatch objects; each have objects which contain ids of the two matching files, a score for the match, a reason from the DetectionType, and the line numbers in each file where the match occurs. The value is the report summary.
     */
    @Override
    public ITuple<List<SubmissionMatchGroup>, String> GetSubmissionReport(ISubmission submission) {

        if (submission.hasParent()) {
            // submission is not top level, don't make reports on non-top level submission.

            //Either just return null with an error msg
            logger.error("Cannot generate report on a submission which has a parent");
            return null;

            // or get the parent recursively
			/*ISubmission sub = ...
			while (sub.hasParent()) {
				sub = sub.getParent();
			}*/
        }

        List<ICodeBlockGroup> relevantGroups = GetCodeBlockGroups(submission);

        //Generate and return the report.
        return reportGenerator.generateSubmissionReport(submission, relevantGroups, submissionScores.get(submission.getId()));
    }

}
