
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	String serverName = "";
	String dbName = "";
	String port = "";
	String userName = "";
	String pass = "";
	String location_log = "";
	Properties properties;
	Conect_DB conectDB;

	public void extract(int id) throws IOException {
		// 1.Load init cofig
		properties = new Properties();
		try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("Config.properties")) {
			if (inputStream == null) {
				throw new FileNotFoundException("config.properties not found in the classpath");
			}
			properties.load(inputStream);
			serverName = properties.getProperty("SERVER_NAME");
			port = properties.getProperty("PORT");
			userName = properties.getProperty("USER_NAME");
			pass = properties.getProperty("PASS");
			dbName = properties.getProperty("DB_NAME");
			location_log = properties.getProperty("LOCATION_LOG");
			System.out.println(pass);
		}
		// 2.Conect database control
		conectDB = new Conect_DB();
		try (Connection connection = conectDB.connection(serverName, port, dbName, userName, pass)) {

			// 3.Kết nối thành công: YES
			// 5.Load config có id tương ứng
			String callProcedureInsertData = "{CALL loadConfig(?)}";
			CallableStatement callableStatement2 = connection.prepareCall(callProcedureInsertData);
			callableStatement2.setInt(1, id);
			ResultSet resultSet = callableStatement2.executeQuery();

			// 6.Lấy ra 1 dòng dữ liệu tương ứng
			while (resultSet.next()) {
				String status = resultSet.getString("status");
				String name = resultSet.getString("name");
				String source_path = resultSet.getString("source_path");
				String location = resultSet.getString("location");
				String format = resultSet.getString("format");
				String server_name = resultSet.getString("server_name");
				String port = resultSet.getString("port");
				String username = resultSet.getString("username");
				String password = resultSet.getString("password");
				// 7.Kiểm tra status có bằng default hay BE
				if (status.equals("default") || status.equals("BE")) {
					// Yes: 8.Cập nhật status bằng BE
					String updateStatus = "{CALL UpdateStatusById(?,?)}";
					CallableStatement callableStatement = connection.prepareCall(updateStatus);
					callableStatement.setInt(1, id);
					callableStatement.setString(2, "BE");
					callableStatement.executeUpdate();
					// 9.Ghi dữ liệu vào file excel
					try {

						Document document = Jsoup.connect(source_path).get();

						String province = document.select(".tit-mien strong a").text();
						String date = document.select(".tit-mien h2").text();

						int dateEndIndex = date.length();

						String extractedDate = date.substring(dateEndIndex - 10, dateEndIndex);

						try (FileWriter writer = new FileWriter(location + name + "." + format)) {
							writer.write("Province,Date,Prize,Numbers\n");

							Elements prizeElements = document.select(".kqtinh tbody tr");
							for (Element prizeElement : prizeElements) {
								String prizeName = prizeElement.select(".txt-giai").text();
								String prizeNumbers = prizeElement.select(".v-giai.number span").text();

								// Write data to CSV
								writer.write(String.format("%s,%s,%s,%s\n", "Long An", extractedDate, prizeName,
										prizeNumbers));
							}
						}
						// 10.Ghi dữ liệu thành công? = yes
						// 12.Cập nhật status bằng CE
						CallableStatement callableStatement1 = connection.prepareCall(updateStatus);
						callableStatement1.setInt(1, id);
						callableStatement2.setString(2, "CE");
						callableStatement.executeUpdate();
						System.out.println("Data written to xoso_results.csv successfully.");

					} catch (IOException e) {
						// 11.Cập nhật status bằng FE
						CallableStatement callableStatement1 = connection.prepareCall(updateStatus);
						callableStatement1.setInt(1, id);
						callableStatement1.setString(2, "FE");
						callableStatement.executeUpdate();

					}
				}

			}
			// 13. Đóng connect
			connection.close();
		} catch (Exception e) {
			// 4.Ghi log vào file trong D://log
			Date currentDateTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			String formattedDateTime = dateFormat.format(currentDateTime);
			createFileWithIncrementedName(location_log, "Log_error_" + formattedDateTime + ".txt",
					"Modul : Extract /n Error : " + e.getMessage());
			// TODO: handle exception
			e.printStackTrace();
		}

		String url = "https://xoso.mobi/mien-nam/xsla-ket-qua-xo-so-long-an-p16.html";

	}

	private static void createFileWithIncrementedName(String filePath, String fileName, String data) {
		Path path = Paths.get(filePath, fileName);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
			// Ghi dữ liệu vào file
			writer.write(data);

			System.out.println("Dữ liệu đã được ghi vào file '" + fileName + "' thành công.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		new Main().extract(1);
	}

}
