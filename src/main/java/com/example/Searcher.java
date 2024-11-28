package com.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParser;
	Query query;

	public Searcher(String indexDirectoryPath) throws IOException {
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		// queryParser = new QueryParser(LuceneConstants.CONTENTS, new
		// StandardAnalyzer());
	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {
		query = queryParser.parse(searchQuery);
		// System.out.println("query: " + query.toString());
		return indexSearcher.search(query, 10); // K apotelesmata
	}

	public TopDocs search(Query searchQuery) throws IOException, ParseException {
		query = searchQuery;
		// System.out.println("query: " + query.toString());
		return indexSearcher.search(query, 100); // K apotelesmata
	}

	public TopDocs search(Query searchQuery, int k) throws IOException, ParseException {
		query = searchQuery;
		// System.out.println("query: " + query.toString());
		if (k > 0) {
			return indexSearcher.search(query, k);
		} else {
			return indexSearcher.search(query, 100); // K apotelesmata
		}
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}

	public Query buildQuery(Map<String, String> map, Occur occur) throws ParseException {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for (String field : map.keySet()) {
			String value = map.get(field);
			QueryParser queryParse = new QueryParser(field, new StandardAnalyzer());
			Query queryString = queryParse.parse(value);
			builder.add(queryString, occur);
		}
		return builder.build();
	}

	public Query buildQuery2(Document doc) throws ParseException {
		Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
				.parse("\"" + doc.get("singer_name") + "\"");
		Query songNameQuery = new QueryParser("song_name", new StandardAnalyzer())
				.parse("\"" + doc.get("song_name") + "\"");
		BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(singerNameQuery, Occur.MUST)
				.add(songNameQuery, Occur.MUST)
				.build();
		return booleanQuery;
	}

	public Query buildQuery3(Document doc) throws ParseException {
		Query singerNameQuery = new QueryParser("singer_name", new StandardAnalyzer())
				.parse("\"" + doc.get("singer_name") + "\"");
		Query albumNameQuery = new QueryParser("album_name", new StandardAnalyzer())
				.parse("\"" + doc.get("album_name") + "\"");
		BooleanQuery booleanQuery = new BooleanQuery.Builder()
				.add(singerNameQuery, Occur.MUST)
				.add(albumNameQuery, Occur.MUST)
				.build();
		return booleanQuery;
	}
}