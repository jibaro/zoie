/**
 *
 */
package proj.zoie.test.data;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import proj.zoie.api.indexing.AbstractZoieIndexable;
import proj.zoie.api.indexing.ZoieIndexable;
import proj.zoie.api.indexing.ZoieIndexableInterpreter;

/**
 * @author "Xiaoyang Gu<xgu@linkedin.com>"
 *
 */
public class InRangeDataInterpreterForTests implements ZoieIndexableInterpreter<String> {

  long _delay;
  final Analyzer _analyzer;

  public InRangeDataInterpreterForTests() {
    this(0, null);
  }

  public InRangeDataInterpreterForTests(long delay) {
    this(delay, null);
  }

  public InRangeDataInterpreterForTests(long delay, Analyzer analyzer) {
    _delay = delay;
    _analyzer = analyzer;
  }

  public ZoieIndexable interpret(final String src) {
    String[] parts = src.split(" ");
    final long id = Long.parseLong(parts[parts.length - 1]);
    return new AbstractZoieIndexable() {
      public Document buildDocument() {
        Document doc = new Document();
        doc.add(new TextField("contents", src, Store.NO));
        doc.add(new StringField("id", String.valueOf(id), Store.YES));
        try {
          Thread.sleep(_delay); // slow down indexing process
        } catch (InterruptedException e) {
        }
        return doc;
      }

      @Override
      public IndexingReq[] buildIndexingReqs() {
        return new IndexingReq[] { new IndexingReq(buildDocument(), getAnalyzer()) };
      }

      public Analyzer getAnalyzer() {
        return id % 2 == 0 ? null : _analyzer;
      }

      @Override
      public long getUID() {
        return id;
      }

      @Override
      public boolean isDeleted() {
        return false;
      }

      @Override
      public boolean isSkip() {
        return false;
      }
    };
  }

  @Override
  public ZoieIndexable convertAndInterpret(String src) {
    return interpret(src);
  }

}
