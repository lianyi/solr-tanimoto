package org.apache.lucene.analysis.tanimoto;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreCachingWrappingScorer;
import org.apache.lucene.search.Scorer;
import org.apache.solr.search.DelegatingCollector;

import java.io.IOException;

/**
 * Created by hanl on 3/20/2015.
 */
public class TanimotoCollector extends DelegatingCollector /*Collector*/ {


    private float threshold;
    private int maxdoc;

    public TanimotoCollector(float threshold) {
        this.threshold = threshold;
    }

    public TanimotoCollector(Collector delegate) {
        this.setDelegate(delegate);
        this.threshold = 0.0f;
    }

    public TanimotoCollector(Collector delegate, float threshold) {
        this.setDelegate(delegate);
        this.threshold = threshold;
    }

    @Override
    public void collect(int doc) throws IOException {
        if (doc< this.maxdoc && this.scorer.score() >= this.threshold) {
            this.collect(doc);
        }
    }

    @Override
    public void doSetNextReader(LeafReaderContext context) throws IOException {
        this.maxdoc = context.reader().maxDoc();
        this.doSetNextReader(context);
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        // Set a ScoreCachingWrappingScorer in case the wrapped Collector will call
        // score() also.
        this.scorer = new ScoreCachingWrappingScorer(scorer);
        this.setScorer(this.scorer);
    }

}
