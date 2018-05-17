package example;

import clustering.DocumentCluster;
import clustering.SingleLinkHAC;
import core.DocId;
import core.query.parser.StandardQueryParser;
import core.Record;
import core.zones.textzone.StandardTextParser;
import core.zones.textzone.TextZone;
import core.zones.textzone.positionalindex.InMemoryPositionalIndex;
import core.Indexer;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WikipediaClusterExample {
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

        while (scanner.hasNextLine()) {
            String content = scanner.nextLine();
            if (content.startsWith("@@")) {
                DocId docId = new DocId(Integer.parseInt(content.substring(2, content.indexOf(" "))));

                Map<String, String> recordData = new HashMap<>();
                recordData.put("content", content);
                Record record = new Record(docId, recordData);

                indexer.add(record);

                contentToDocId.put(docId, content);
            }
        }


        /***********************
         * BUILD CLUSTERS
         ***********************/
        System.out.printf("Building clusters...\n\n");
        SingleLinkHAC clusterer = SingleLinkHAC.fromIndexer(indexer);

        Set<DocumentCluster> clusters = clusterer.buildCluster(0.6f);


        /***************************
         * DISPLAY CLUSTERS
         ***************************/
        PriorityQueue<DocumentCluster> clustersBySize = new PriorityQueue<>(clusters.size(), new Comparator<DocumentCluster>() {
            @Override
            public int compare(DocumentCluster o1, DocumentCluster o2) {
                return Integer.compare(o2.getDocIds().size(), o1.getDocIds().size());
            }
        });
        clustersBySize.addAll(clusters);

        int limit = 10;
        int count = 0;
        while (!clustersBySize.isEmpty()) {
            if (count >= limit) break;
            count++;

            DocumentCluster documentCluster = clustersBySize.poll();

            System.out.printf("-----------------------------------\n");
            System.out.printf("Cluster size: %d\n", documentCluster.getDocIds().size());
            for (DocId clusterDocId : documentCluster.getDocIds()) {
                System.out.printf("%d\n", clusterDocId.getId());
            }
            System.out.printf("-----------------------------------\n\n");
        }


        /***********************
         * QUERY REPL
         ***********************/
        Scanner stdin = new Scanner(System.in);
        while (true) {
            System.out.printf("DocId? ");
            int documentId = stdin.nextInt();

            System.out.printf("\n%s----------------------------------\n\n", contentToDocId.get(new DocId(documentId)));
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
