package fr.isaaconnect.sweb.services.indexes;

import fr.isaaconnect.sweb.dto.evaluations.EvaluationIndexDTO;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexService {
    private static final String DIRECTORY_INDEX = "index";

    private DirectoryReader reader;
    private IndexSearcher searcer;
    private final IndexWriter writer;
    private final QueryParser parser;

    public IndexService() throws IOException {
        Path path = Paths.get(DIRECTORY_INDEX);
        Directory indexDir = FSDirectory.open(path);

        FrenchAnalyzer analyzer = new FrenchAnalyzer();
        this.parser = new QueryParser("content", analyzer);

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        this.writer = new IndexWriter(indexDir, iwc);

        if (!DirectoryReader.indexExists(indexDir)) this.writer.commit();

        this.reader = DirectoryReader.open(indexDir);
        this.searcer = new IndexSearcher(reader);
    }

    public void indexEvaluation(EvaluationIndexDTO index) {
        try
        {
            Document document = new Document();
            document.add(new StringField("restaurant_id",String.valueOf(index.getRestaurantId()),Field.Store.YES));
            document.add(new StringField("evaluation_id", String.valueOf(index.getId()), Field.Store.YES ));
            document.add(new StringField("author", index.getAuthor(), Field.Store.YES));
            document.add(new TextField("content", index.getText(), Field.Store.YES));
            document.add(new IntField( "note", index.getNote(), Field.Store.YES));

            this.writer.addDocument(document);
            this.writer.commit();

            DirectoryReader newDirReader = DirectoryReader.openIfChanged(this.reader);
            if (newDirReader != null) {
                this.reader.close();
                this.reader = newDirReader;
                this.searcer = new IndexSearcher(this.reader);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Document> getIndexEvaluation(String evaluation) {
        try {
            Query query = this.parser.parse(evaluation);

            TopDocs topDocs = this.searcer.search(query, 15);
            StoredFields storedFields = this.searcer.storedFields();
            return Arrays.stream(topDocs.scoreDocs)
                .map(document -> {
                    try {
                        return storedFields.document(document.doc);
                    } catch (Exception _) { return new Document(); }
                })
                .collect(Collectors.toList());
        } catch (Exception _)
        {}

        return null;
    }
}
