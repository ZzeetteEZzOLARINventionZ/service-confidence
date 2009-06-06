package pku.sei.webservice.searchEngine;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jeasy.analysis.MMAnalyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import pku.sei.webservice.wsdl.WsdlInfo;

public class searchHandler {
	private String[] STOPWORDS={"messge","wsdl","binding","operation","type","service","<",">","/","=","xmlns",":","xs"};
	
	Hits hits = null;
	Query q = null;

	SimpleHTMLFormatter sHtmlF = new SimpleHTMLFormatter(
			"<b><font color='red'>", "</font></b>");

	String sf = "topic";
	
	String kw = "";

	public List<WsdlInfo> searcher(String query, String indexPath, String searchfield) {

		sf = searchfield;
		
		kw = query;

		List<WsdlInfo> result = new ArrayList<WsdlInfo>();

		try {
			IndexSearcher searcher = new IndexSearcher(indexPath);
			StandardAnalyzer analyzer = new StandardAnalyzer(STOPWORDS);
			QueryParser parser = new QueryParser(searchfield, analyzer);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			
			
			if(query.indexOf(' ')!=-1){//���Ӳ�ѯ���ʽ: <AB C>,<A.B C>,<A B>,<A_B C>,<AB.C D> 
				String[] words=query.split(" ");
				if(words.length>1)//AB,A.B,A_B
				{
					BooleanQuery boolquery= new BooleanQuery();
					for(int i=0;i<words.length;i++){
						String[] phrase=WordParser.wordParser(words[i]);//��AB,A.B,A_B ��ʽ�Ĳ�ѯ����ϲ�
						if(phrase.length>1)//
						{
							PhraseQuery phrasequery = new PhraseQuery();
							phrasequery.setSlop(0);//�����ݶȣ�������Щ��ѯ��֮���ԭ���б���Ҫ���ڡ�
							for(int j=0;j<phrase.length;j++)//��������ѯ
							{				
								phrasequery.add(new Term(searchfield,phrase[j].toLowerCase()));
							}
							boolquery.add(phrasequery,BooleanClause.Occur.MUST);
						}//���һ�ζ����ѯ���Ĺ���
						
						else//�򵥵ĵ���
						{
							Term t = new Term(searchfield, words[i].toLowerCase());
							Query termquery= new TermQuery(t);
							boolquery.add(termquery,BooleanClause.Occur.MUST);
						}
					}
					this.hits= searcher.search(boolquery);	
				}
			}
			else//��ѯ��֮��û�пո񣬿���ΪA.B or A_B or AB
			{
				String[] phrase=WordParser.wordParser(query);//�����ܵذѵ��ʲ�ֿ�
				BooleanQuery boolquery= new BooleanQuery();
				if(phrase.length>1)
				{
							PhraseQuery phrasequery = new PhraseQuery();
							phrasequery.setSlop(0);//�����ݶȣ�������Щ��ѯ��֮���ԭ���б���Ҫ���ڡ�
							for(int j=0;j<phrase.length;j++)//��������ѯ
							{				
								phrasequery.add(new Term(searchfield,phrase[j].toLowerCase()));
							}
							boolquery.add(phrasequery,BooleanClause.Occur.MUST);
							this.hits= searcher.search(boolquery);	
				}//���һ�ζ����ѯ���Ĺ���
				else
				{
					Query simplequery = parser.parse(query);
					System.out.println(simplequery.toString());
					this.hits= searcher.search(simplequery);
				}
			}
			System.out.println(q.toString());
			/*******************
			 * ���˲�ѯ���������¿�ʼ������
			 * 
			 *******************/
			for (int i = 0; i < hits.length() && i < 10; i++) {
				Document doc = hits.doc(i);
				WsdlInfo wsdlInfo = new WsdlInfo();
				wsdlInfo.setName(doc.getField("ws_name").stringValue());
				wsdlInfo.serId(doc.getField("ws_id").stringValue());
				wsdlInfo.setDescription(doc.getField("ws_description").stringValue());
				wsdlInfo.setUrl(doc.getField("ws_url").stringValue());

					/**
					 * ������ʾ
					 */
					Highlighter highlighter = new Highlighter(sHtmlF,
							new QueryScorer(q));
					highlighter.setTextFragmenter(new SimpleFragmenter(200));
					TokenStream tokenStream = null;
					String str = "";


					tokenStream = analyzer.tokenStream(sf,	new StringReader(wsdlInfo.getDescription()));
					str = highlighter.getBestFragment(tokenStream, wsdlInfo.getDescription());
						

					/***/
				result.add(wsdlInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<WsdlInfo>();
		} catch (ParseException e) {
			e.printStackTrace();
			return new ArrayList<WsdlInfo>();
		}

		return result;

	}

	public List<WsdlInfo> getMoreResults(int index, int numPerPage) {
		List<WsdlInfo> result = new ArrayList<WsdlInfo>();
		if (hits == null) {
			return result;
		}
		StandardAnalyzer analyzer = new StandardAnalyzer();
		for (int i = index; i < index + 10 && i < hits.length(); i++) {
			try {
				Document doc = hits.doc(i);
				WsdlInfo wsdlInfo = new WsdlInfo();
				wsdlInfo.setName(doc.getField("ws_name").stringValue());
				wsdlInfo.serId(doc.getField("ws_id").stringValue());
				wsdlInfo.setDescription(doc.getField("ws_description").stringValue());
				wsdlInfo.setUrl(doc.getField("ws_url").stringValue());

					/**
					 * ������ʾ
					 */
					Highlighter highlighter = new Highlighter(sHtmlF,
							new QueryScorer(q));
					highlighter.setTextFragmenter(new SimpleFragmenter(200));
					TokenStream tokenStream = null;
					String str = "";

					tokenStream = analyzer.tokenStream(sf,	new StringReader(wsdlInfo.getDescription()));
					str = highlighter.getBestFragment(tokenStream, wsdlInfo.getDescription());
						

					/***/
				result.add(wsdlInfo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("result size is:"+result.size());
		return result;
	}

	public List<WsdlInfo> getMoreResults(int index) {//ÿ�η���ʮ�������indexΪ�±�
		return getMoreResults(index, 10);
	}

	public int resultTotal() {
		if (hits != null) {
			return hits.length();
		} else {
			return 0;
		}
	}

	public String getSummary(String str, String q) throws IOException {
		String fieldName = "text";
		String text = str; //��������    

		MMAnalyzer analyzer = new MMAnalyzer();

		Directory directory = new RAMDirectory();

		String rs = "";
		
		q = analyzer.segment(q, " ");

		try {
			//����    
			IndexWriter iwriter = new IndexWriter(directory, analyzer, true);
			iwriter.setMaxFieldLength(2500);
			Document doc = new Document();
			doc.add(new Field(fieldName, text, Field.Store.YES,
					Field.Index.TOKENIZED,
					Field.TermVector.WITH_POSITIONS_OFFSETS));
			iwriter.addDocument(doc);
			iwriter.close();

			IndexSearcher isearcher = new IndexSearcher(directory);

			QueryParser queryParse = new QueryParser(fieldName, analyzer);
			Query query = queryParse.parse(q);
			Hits hitsx = isearcher.search(query);
			for (int i = 0; i < hitsx.length(); i++) {
				Document docTemp = hitsx.doc(i);
				String value = docTemp.get(fieldName);
				//      ��Ҫ������ʾ���ֶθ�ʽ��������ֻ�ǼӺ�ɫ��ʾ�ͼӴ�    
				SimpleHTMLFormatter sHtmlF = new SimpleHTMLFormatter(
						"<b><font color='red'>", "</font></b>");
				Highlighter highlighter = new Highlighter(sHtmlF,
						new QueryScorer(query));
				highlighter.setTextFragmenter(new SimpleFragmenter(100));    

				if (value != null) {
					TokenStream tokenStream = analyzer.tokenStream(fieldName,
							new StringReader(value));
					String s = highlighter.getBestFragment(tokenStream, value);

					rs += s + "...";
				}
			}

			isearcher.close();
			directory.close();
			if(rs==null)
				rs = str;
			
			System.out.println("��ѯ��:"+q+"\n���:"+rs);
			
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}
	public void indexSearch(String searchType, String searchKey, String indexPath) throws ParseException{

		try{
			IndexSearcher searcher = new IndexSearcher(indexPath);
			Term t = new Term(searchType, searchKey);
			QueryParser parser= new QueryParser(searchType, new StandardAnalyzer());
			
			Query q =parser.parse(searchKey);
			Date beginTime = new Date();
			//TermDocs termDocs = searcher.getIndexReader().termDocs(t);
			Hits hits = searcher.search(q);
			for(int i = 0; i<hits.length();i++){
				Document doc = hits.doc(i);
			
				System.out.println(doc.getField("ws_name"));
				System.out.println(hits.score(i));
			
			}
			Date endTime = new Date();
			long timeOfSearch = endTime.getTime() - beginTime.getTime();
			System.out.println("timeOfSearch = "+timeOfSearch);
			
		}catch (IOException e){
			e.printStackTrace();
			
		}
	}
	public static void main(String[] args) {

		searchHandler handler = new searchHandler();
		try {
			handler.indexSearch("ws_content","CreateOutput", "E:/work/WebServiceSE/IndexStore");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
