package example;

import core.DocId;
import core.query.parser.StandardQueryParser;
import core.search.Indexer;
import core.search.Record;
import core.search.Search;
import core.search.zones.textzone.StandardTextParser;
import core.search.zones.textzone.TextZone;
import core.search.zones.textzone.positionalindex.InMemoryPositionalIndex;
import kotlin.Pair;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WikipediaSearchExample {
    private static final String CORPUS_SOURCE = "https://corpus.byu.edu/wikitext-samples/text.zip";
    private static final String ZIPPED_FILE_NAME = "text.zip";
    private static final String UNZIPPED_FILE_NAME = "text.txt";

    public static void main(String[] args) throws IOException {

        /***********************
         * FETCH SOURCES
         ***********************/
        File zippedFile = new File(ZIPPED_FILE_NAME);
        File unzippedFile = new File(UNZIPPED_FILE_NAME);

        if (!zippedFile.exists()) {
            downloadCorpus(zippedFile);
        }

        if (!unzippedFile.exists()) {
            unzipCorpus(zippedFile, unzippedFile);
        }


        /***********************
         * BUILD INDEX
         ***********************/
        System.out.printf("Building index...\n\n");
        Indexer indexer = new Indexer.Builder()
                .addZone(new TextZone("content", new InMemoryPositionalIndex(), new StandardQueryParser(), new StandardTextParser()))
                .build();
        Scanner scanner = new Scanner(new File(UNZIPPED_FILE_NAME));
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();

        Map<DocId, String> contentToDocId = new HashMap<>();

        DocId docId = null;
        while (scanner.hasNextLine()) {
            String content = scanner.nextLine();
            if (content.startsWith("@@")) {
                if (docId != null) {
                    Map<String, String> recordData = new HashMap<>();
                    recordData.put("content", content);
                    Record record = new Record(docId, recordData);

                    indexer.add(record);

                    contentToDocId.put(docId, content);
                }

                docId = new DocId(Integer.parseInt(content.substring(2, content.indexOf(" "))));
            }
        }


        /***********************
         * QUERY REPL
         ***********************/
        Scanner stdin = new Scanner(System.in);
        Search search = Search.Companion.fromIndexer(indexer);
        while (true) {
            System.out.printf("? ");
            String terms = stdin.nextLine();

            Map<String, String> query = new HashMap<>();
            query.put("content", terms);

            List<Pair<DocId, Double>> docIds = search.search(query, 3);
            for (Pair<DocId, Double> result : docIds) {
                System.out.printf("[%s - %f]\n%s\n---------------------\n\n", result.getFirst().getId(), result.getSecond().doubleValue(), contentToDocId.get(result.getFirst()));
            }
        }
    }

    private static void downloadCorpus(File out) throws IOException {
        System.out.printf("Downloading corpus from %s...\n", CORPUS_SOURCE);

        out.createNewFile();

        URL source = new URL(CORPUS_SOURCE);
        ReadableByteChannel rbc = Channels.newChannel(source.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(out);
        fileOutputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private static void unzipCorpus(File in, File out) throws IOException {
        System.out.printf("Unzipping corpus...\n", CORPUS_SOURCE);

        out.createNewFile();

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(in));
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        byte[] buffer = new byte[1024];
        while (zipEntry != null) {
            FileOutputStream fileOutputStream = new FileOutputStream(out);
            int len;
            while ((len = zipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
            }

            fileOutputStream.close();
            zipEntry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        zipInputStream.close();
    }
}
