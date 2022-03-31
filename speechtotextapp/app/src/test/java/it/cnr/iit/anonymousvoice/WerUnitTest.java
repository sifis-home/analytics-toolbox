package it.cnr.iit.anonymousvoice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.anonymousvoice.util.WordSequenceAligner;

/**
 * Unit test for word error rate calculation
 * based on Romanows wordAligner
 */
public class WerUnitTest {

    DecimalFormat df;

    WordSequenceAligner werEval = new WordSequenceAligner();

    @Before
    public void init(){
        df = new DecimalFormat("###.###");
        df.setRoundingMode(RoundingMode.DOWN);
    }

    @Test
    public void testSameSentences(){
        String reference = "The pen is on the table";
        String hypothesis = "the pen is on the table";

        List<WordSequenceAligner.Alignment> alignments = new ArrayList<>();

        alignments.add(werEval.align(reference.split(" "), hypothesis.split(" ")));

        WordSequenceAligner.SummaryStatistics ss = werEval.new SummaryStatistics(alignments);

        float wer = Float.parseFloat(df.format(ss.getWordErrorRate()).replace(",", "."));

        Assert.assertEquals(0, wer, 0.001);
    }

    @Test
    public void testDifferentSentences(){
        String reference = "The pen is on the table";
        String hypothesis = "the is on the table";

        List<WordSequenceAligner.Alignment> alignments = new ArrayList<>();

        alignments.add(werEval.align(reference.split(" "), hypothesis.split(" ")));

        WordSequenceAligner.SummaryStatistics ss = werEval.new SummaryStatistics(alignments);

        float wer = Float.parseFloat(df.format(ss.getWordErrorRate()).replace(",", "."));

        Assert.assertEquals(0.166, wer, 0.001);
    }

    @Test
    public void testDifferentSentencesWithAnInsertion(){
        String reference = "The pen is on the table";
        String hypothesis = "the pen nn is on the table";

        List<WordSequenceAligner.Alignment> alignments = new ArrayList<>();

        alignments.add(werEval.align(reference.split(" "), hypothesis.split(" ")));

        WordSequenceAligner.SummaryStatistics ss = werEval.new SummaryStatistics(alignments);

        float wer = Float.parseFloat(df.format(ss.getWordErrorRate()).replace(",", "."));

        Assert.assertEquals(0.166, wer, 0.001);
    }

}
