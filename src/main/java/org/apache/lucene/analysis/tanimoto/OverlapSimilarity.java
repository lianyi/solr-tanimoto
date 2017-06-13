package org.apache.lucene.analysis.tanimoto;

/**
 * Created by hanl.
 */
/*
 *  Similarity: http://www.daylight.com/dayhtml/doc/theory/theory.finger.html
 */

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/*
 * (1 / size(q)) * ∑ (1 / size(d))
 */

/**
 * Expert: Default scoring implementation.
 */
public class OverlapSimilarity extends TFIDFSimilarity {

    /**
     * Sole constructor: parameter-free
     */
    public OverlapSimilarity() {
    }

    /**
     * Computes a score factor based on the fraction of all query terms that a
     * document contains. This value is multiplied into scores.
     *
     * <p>
     * The presence of a large portion of the query terms indicates a better
     * match with the query, so implementations of this method usually return
     * larger values when the ratio between these parameters is large and
     * smaller values when the ratio between them is small.
     *
     * @param overlap
     *            the number of query terms matched in the document
     * @param maxOverlap
     *            the total number of terms in the query
     * @return a score factor based on term overlap with the query
     */
    /** Implemented as <code>overlap / maxOverlap</code>. in default similarity. */
    /**
     * 1.0f*overlap /maxOverlap , is actually calc overlap*overlap/maxoverlap
     */
    @Override
    public float coord(int overlap, int maxOverlap) {
        //System.out.println("overlap:" + overlap);
        //System.out.println("maxOverlap:" + maxOverlap);
        //http: // www.daylight.com/dayhtml/doc/theory/theory.finger.html
        return (float) 1.0f;// (1.0f / Math.sqrt(maxOverlap)); // is actually calc
        // 1.0f*overlap
        // /maxOverlap
    }

    /**
     * Computes the normalization value for a query given the sum of the squared
     * weights of each of the query terms. This value is multiplied into the
     * weight of each query term. While the classic query normalization factor
     * is computed as 1/sqrt(sumOfSquaredWeights), other implementations might
     * completely ignore sumOfSquaredWeights (ie return 1).
     *
     * <p>
     * This does not affect ranking, but the default implementation does make
     * scores from different queries more comparable than they would be by
     * eliminating the magnitude of the Query vector as a factor in the score.
     *
     * @param sumOfSquaredWeights
     *            the sum of the squares of query term weights
     * @return a normalization factor for query weights
     */
    /**
     * DONE Implemented as <code>1/sqrt(sumOfSquaredWeights)</code>.
     */
    @Override
    public float queryNorm(float sumOfSquaredWeights) {
        return 1.0f; // (float)(1.0 / Math.sqrt(sumOfSquaredWeights));
    }

    /**
     * return the total number of terms in this field this is the number of
     * temrs in document and it will be kept in norm
     *
     * @lucene.experimental
     */
    @Override
    public float lengthNorm(FieldInvertState state) {
        //it's not efficient to calculate this value in score from ExactTFIDFDocScorer1
        return (float) (1.0f / Math.sqrt(state.getLength()));
    }

    /**
     * DONE Implemented as <code>sqrt(freq)</code>. in default similarity. for
     * tanimoto similarity search, each term is unique and the tf will be 1 by
     * this nature
     */
    @Override
    public float tf(float freq) {
        return 1.0f;
    }

    /**
     * Implemented as <code>1 / (distance + 1)</code>.
     */
    @Override
    public float sloppyFreq(int distance) {
        return 1.0f;
    }

    /**
     * The default implementation returns <code>1</code>
     */
    @Override
    public float scorePayload(int doc, int start, int end, BytesRef payload) {
        return 1.0f;
    }

    /**
     * DONE Implemented as <code>log(numDocs/(docFreq+1)) + 1</code>. in default
     * similarity. for tanimoto similarity search, this will be 1
     */
    @Override
    public float idf(long docFreq, long numDocs) {
        return 1.0f;
    }

    public final OverlapSimScorer simScorer(SimWeight stats,
                                            LeafReaderContext context) throws IOException {
        IDFStats idfstats = (IDFStats) stats;
        return new OverlapSimScorer(idfstats, context.reader().getNormValues(
                idfstats.field));
    }

    @Override
    public String toString() {
        return "OverlapSimilarity";
    }

    @Override
    public float decodeNormValue(long norm) {
        return 1;
    }

    @Override
    public long encodeNormValue(float f) {
        return 1;
    }

    private final class OverlapSimScorer extends TFIDFSimScorer {
        OverlapSimScorer(IDFStats stats, NumericDocValues norms)
                throws IOException {
            super(stats, norms);
        }

        @Override
        public float score(int doc, float freq) {
            // final float raw = tf(freq) * weightValue; // compute tf(f)*weight

            // return norms == null ? raw : raw *
            // decodeNormValue(norms.get(doc)); // normalize for field

			/*
             * float raw = tf(freq)*weightValue; // compute tf(f)*weight
			 * System.out.println( "stats.queryNorm="+stats.queryNorm);
			 * System.out.println( "doc="+doc+" raw = tf(freq)*weightValue " +
			 * raw); System.out.println( "freq  " + freq); System.out.println(
			 * "weightValue  " + weightValue); System.out.println( "tf(freq)" +
			 * tf(freq)); System.out.println(
			 * "decodeNormValue((byte)norms.get(doc))" +
			 * decodeNormValue((byte)norms.get(doc)));
			 */

            // return (float) (1.0f / Math.sqrt(decodeNormValue((byte)
            // norms.get(doc)))); // || decodeNormValue((byte)norms.get(doc));
            return 1;//(float) (decodeNormValue((byte) norms.get(doc)));
            // return norms == null ? raw : raw /
        }

    }
}