
	import org.jsoup.Jsoup;
	import org.jsoup.nodes.Document;
	import org.jsoup.nodes.Element;
	import org.jsoup.select.Elements;

	import java.io.FileWriter;
	import java.io.IOException;

	public class Main {

	    public static void main(String[] args) {
	        String url = "https://xoso.mobi/mien-nam/xsla-ket-qua-xo-so-long-an-p16.html";

	        try {
	            // Connect to the website and get the HTML document
	            Document document = Jsoup.connect(url).get();

	            // Extract information from the HTML structure
	            String province = document.select(".tit-mien strong a").text();
	            String date = document.select(".tit-mien").text().replaceAll(".*ngày (\\d{2}/\\d{2}/\\d{4}).*", "$1");

	            // Create CSV file and write header
	            try (FileWriter writer = new FileWriter("D://xoso_results.csv")) {
	                writer.write("Province,Date,Prize,Numbers\n");

	                Elements prizeElements = document.select(".kqtinh tbody tr");
	                for (Element prizeElement : prizeElements) {
	                    String prizeName = prizeElement.select(".txt-giai").text();
	                    String prizeNumbers = prizeElement.select(".v-giai.number span").text();

	                    // Write data to CSV
	                    writer.write(String.format("%s,%s,%s,%s\n", province, date, prizeName, prizeNumbers));
	                }
	            }

	            System.out.println("Data written to xoso_results.csv successfully.");

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}


