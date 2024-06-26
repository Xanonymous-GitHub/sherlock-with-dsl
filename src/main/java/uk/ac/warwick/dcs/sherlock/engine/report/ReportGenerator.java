package uk.ac.warwick.dcs.sherlock.engine.report;

import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.report.IReportGenerator;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.util.*;

public class ReportGenerator implements IReportGenerator<SubmissionMatchGroup> {

    ReportGenerator() {
    }

    @Override
    public List<SubmissionMatchGroup> generateSubmissionComparison(List<ISubmission> submissions, List<? extends ICodeBlockGroup> codeBlockGroups) {
        Map<DetectionType, SubmissionMatchGroup> matchGroupMap = new HashMap<>();

        //Create a SubmissionMatch object for each CodeBlockGroup in the task that is relevant to either submission
        for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
            String reason;
            List<SubmissionMatchItem> items = new ArrayList<>();

            //Try to get the detection type and use it to get the string reason
            DetectionType detectionType = null;
            try {
                detectionType = codeBlockGroup.getDetectionType();
            } catch (UnknownDetectionTypeException e) {
                e.printStackTrace();
            }

            //If detectionType is null, something is wrong with the results; don't try anything further
            if (detectionType != null) {
                reason = detectionType.getReason();

                //Create SubmissionMatchItems for every codeBlock belonging to a file in the submissions being compared.
                for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
                    long subId = codeBlock.getFile().getSubmission().getId();
                    if (submissions.get(0).getId() == subId || submissions.get(1).getId() == subId) {
                        items.add(new SubmissionMatchItem(codeBlock.getFile(), codeBlock.getBlockScore(), codeBlock.getLineNumbers()));
                    }

                }

                //Add the SubmissionMatch to the appropriate SubmissionMatchGroup; create a new one if it doesn't exist.
                if (matchGroupMap.get(detectionType) != null)
                    matchGroupMap.get(detectionType).addMatch(new SubmissionMatch(reason, items));
                else {
                    List<SubmissionMatch> matchList = new ArrayList<>();
                    matchList.add(new SubmissionMatch(reason, items));
                    String group_reason = detectionType.getDisplayName();
                    matchGroupMap.put(detectionType, new SubmissionMatchGroup(matchList, group_reason));
                }
            }
        }

        //Convert the map into a list to return
        return new ArrayList<>(matchGroupMap.values());
    }

    @Override
    public ITuple<List<SubmissionMatchGroup>, String> generateSubmissionReport(ISubmission submission, List<? extends ICodeBlockGroup> codeBlockGroups, float subScore) {
        Map<DetectionType, SubmissionMatchGroup> matchGroupMap = new HashMap<>();

        //The report summary - if there was no plagiarism detected, codeBlockGroups will be empty, and this is the default result in that case.
        String summary = "";
        if (subScore < 0.01f)
            summary = summary + "No plagiarism was detected in this submission.";
        else if (subScore >= 0.01f && subScore < 0.05f)
            summary = summary + "Some potential plagiarism was detected, but it is small enough that it may be a false alarm or negligible.";
        else if (subScore >= 0.05f && subScore < 0.2f)
            summary = summary + "A small amount of plagiarism was detected in this submission.";
        else if (subScore >= 0.2f && subScore < 0.5f)
            summary = summary + "A significant amount of plagiarism was detected in this submission.";
        else if (subScore >= 0.5f)
            summary = summary + "A large portion of this submission contains plagiarism.";

        //A tally of how many of the codeBlockGroups are of each reason
        Map<DetectionType, Integer> reasonCounts = new HashMap<>();

        //Counts how many different submissions this submission is connected to through the given codeblockgroups
        Set<Long> subIdsConnected = new HashSet<>();

        //Track how many ICodeBlockGroups there are
        int group_count = 0;

        //Create a SubmissionMatch object for each CodeBlockGroup in the task that is relevant to this submission
        for (ICodeBlockGroup codeBlockGroup : codeBlockGroups) {
            group_count++;

            String reason;
            List<SubmissionMatchItem> items = new ArrayList<>();

            //Try to get the detection type and use it to get the string reason
            DetectionType detectionType = null;
            try {
                detectionType = codeBlockGroup.getDetectionType();
            } catch (UnknownDetectionTypeException e) {
                e.printStackTrace();
            }

            //If detectionType is null, something has gone wrong; don't try any further
            if (detectionType != null) {
                reason = detectionType.getReason();


                //Tally how many of each plagiarism type there is
                if (reasonCounts.get(detectionType) != null)
                    reasonCounts.put(detectionType, reasonCounts.get(detectionType) + 1);
                else
                    reasonCounts.put(detectionType, 1);

                //Create a SubmissionMatchItem for every CodeBlock, updating subIdsConnected along the way
                for (ICodeBlock codeBlock : codeBlockGroup.getCodeBlocks()) {
                    items.add(new SubmissionMatchItem(codeBlock.getFile(), codeBlock.getBlockScore(), codeBlock.getLineNumbers()));

                    //Don't add the id of the submissions the report is being generated for
                    if (!submission.getContainedFiles().contains(codeBlock.getFile()))
                        subIdsConnected.add(codeBlock.getFile().getSubmission().getId());
                }

                //Add the SubmissionMatch to the appropriate SubmissionMatchGroup; create a new one if it doesn't exist.
                if (matchGroupMap.get(detectionType) != null)
                    matchGroupMap.get(detectionType).addMatch(new SubmissionMatch(reason, items));
                else {
                    List<SubmissionMatch> matchList = new ArrayList<>();
                    matchList.add(new SubmissionMatch(reason, items));
                    String group_reason = detectionType.getDisplayName();
                    matchGroupMap.put(detectionType, new SubmissionMatchGroup(matchList, group_reason));
                }
            }
        }

        //Add to the summary strings showing the results from reasonCounts.
        StringBuilder builder = new StringBuilder();
        builder.append(summary);
        for (DetectionType detectionType : reasonCounts.keySet()) {
            Integer count = reasonCounts.get(detectionType);
            float percent = (float) count * 100f / (float) group_count;
            builder.append("\n").append(count).append(" (").append(percent).append("%) of the matches are of the type '").append(detectionType.getDisplayName()).append("'.");
        }

        if (!subIdsConnected.isEmpty())
            builder.append("\nIn total, this submission has content that may be plagiarised from up to ").append(subIdsConnected.size()).append(" other submissions.");
        summary = builder.toString();

        //Convert the map into a list to return
        List<SubmissionMatchGroup> matchGroups = new ArrayList<>(matchGroupMap.values());
        return new Tuple<>(matchGroups, summary);
    }
}
