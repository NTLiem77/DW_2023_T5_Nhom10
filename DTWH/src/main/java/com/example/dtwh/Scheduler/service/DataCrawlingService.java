package com.example.dtwh.Scheduler.service;
import  com.example.dtwh.Scheduler.model.CrawlingResult;
import jakarta.persistence.EntityManagerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DataCrawlingService {
    @Autowired
    @Qualifier("stagingEntityManagerFactory")
    private EntityManagerFactory stagingEntityManagerFactory;

    @Autowired
    @Qualifier("stagingJdbcTemplate")
    private JdbcTemplate stagingJdbcTemplate;


    @Autowired
    @Qualifier("dwhJdbcTemplate")
    private JdbcTemplate dwhJdbcTemplate;

    public void processDateData() {
        String selectSql = "SELECT * FROM staging_kqxs.date_dim";

        List<Map<String, Object>> result = stagingJdbcTemplate.queryForList(selectSql);

        for (Map<String, Object> row : result) {
            Long idDate = (Long) row.get("id_date");
            String checkExistSql = "SELECT COUNT(*) FROM dwh_kqxs.date WHERE id_date = ?";
            int count = Objects.requireNonNull(dwhJdbcTemplate.queryForObject(checkExistSql, Integer.class, idDate));

            if (count == 0) {
                String insertSql = "INSERT INTO dwh_kqxs.date (id_date, date) VALUES (?, ?)";
                dwhJdbcTemplate.update(insertSql, idDate, row.get("date"));
            }
        }
    }

    public void stagingToDwh() {
        String selectSql = "SELECT sd.*, dd.id_date " +
                "FROM staging_kqxs.staging_fact sd " +
                "JOIN dwh_kqxs.date_dim dd ON sd.date = dd.date";

        List<Map<String, Object>> result = stagingJdbcTemplate.queryForList(selectSql);

        for (Map<String, Object> row : result) {
            Long idDate = (Long) row.get("id_date");
            String checkExistSql = "SELECT COUNT(*) FROM dwh_kqxs.fact_kqxs WHERE id_date = ?";
            int count = Objects.requireNonNull(dwhJdbcTemplate.queryForObject(checkExistSql, Integer.class, idDate));

            if (count > 0) {
                String updateSql = "UPDATE dwh_kqxs.fact_kqxs SET status_data = FALSE WHERE id_date = ?";
                dwhJdbcTemplate.update(updateSql, idDate);
            }

            String insertSql = "INSERT INTO dwh_kqxs.fact_kqxs (city, id_date, prize_type, winning_number, number_start, number_end, status_data) " +
                    "VALUES (?, ?, ?, ?, ?, ?, TRUE)";
            dwhJdbcTemplate.update(insertSql, row.get("city"), row.get("id_date"),
                    row.get("prize_type"), row.get("winning_number"),
                    row.get("number_start"), row.get("number_end"));
        }
    }

    public void processStagingData() {
        // Truy vấn SQL để cập nhật dữ liệu từ bảng staging_dim sang staging_fact
        String sql = "INSERT INTO staging_fact (city, id_date, prize_type, winning_number, number_start, number_end) " +
                "SELECT sd.city, dd.id_date, sd.prize_type, sd.winning_number, sd.number_start, sd.number_end " +
                "FROM staging_dim sd JOIN date_dim dd ON sd.date = dd.date";

        // Thực hiện truy vấn SQL bằng JdbcTemplate
        stagingJdbcTemplate.update(sql);
    }

    public void loadCSVDataToStagingDim(String filePath) {
        String sql = "LOAD DATA INFILE '" + filePath + "' " +
                "INTO TABLE staging_dim " +
                "FIELDS TERMINATED BY ',' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(city, date, prize_type, winning_number, number_start, number_end)";

        stagingJdbcTemplate.execute(sql);
    }

    public void loadCSVDataToDategDim(String filePath) {
        String sql = "LOAD DATA INFILE '" + filePath + "' " +
                "INTO TABLE dateg_dim " +
                "FIELDS TERMINATED BY ',' " +
                "LINES TERMINATED BY '\\n' " +
                "IGNORE 1 LINES " +
                "(date)";

        stagingJdbcTemplate.execute(sql);
    }

    public CrawlingResult crawlAndSaveData() {
        String url = "https://xskt.com.vn/xsla";

        try {
            Document document = Jsoup.connect(url).get();

            Element table = document.getElementById("LA0");

            if (table != null) {
                Elements rows = table.select("tbody tr");

                List<String[]> data = new ArrayList<>();

                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);

                    String tinh = "Long An";
                    String ngayCao = getCurrentDateTime("yyyy/MM/dd");

                    Elements cols = row.select("td");

                    if (cols.size() >= 4) {
                        String tenGiai = cols.get(0).text().trim();
                        String soDau = cols.get(2).text().trim();
                        Element soCuoiElement = cols.get(3);
                        String soCuoi = soCuoiElement.html().replace(",", ";");

                        Elements soTrungGiaiElements = cols.get(1).select("p, em");
                        for (Element soTrungGiaiElement : soTrungGiaiElements) {
                            String soTrungGiaiText = soTrungGiaiElement.text().trim();
                            String[] soTrungGiaiArr = soTrungGiaiText.split("\\s+");

                            for (String so : soTrungGiaiArr) {
                                if (so.startsWith("0")) {
                                    so = "0" + so;
                                }

                                String[] rowData = {tinh, ngayCao, tenGiai, so, soDau, soCuoi};
                                data.add(rowData);
                            }
                        }
                    } else {
                        System.out.println("Không đủ cột trong hàng.");
                    }
                }

                String fileName = "D:\\dtwh\\DTWH\\data_csv\\" + getCurrentDateTime("yyyyMMdd") + "_xsktLA_data.csv";
                String dateFile = "D:\\dtwh\\DTWH\\date_csv\\" + getCurrentDateTime("yyyyMMdd") + "_date.csv";

                writeDataToCSV(data, fileName);
                writeLog(dateFile, getCurrentDateTime("yyyy/MM/dd"));

                System.out.println("Dữ liệu đã được lưu vào file " + fileName);

                CrawlingResult result = new CrawlingResult();
                result.setFileName(fileName);
                result.setDateFile(dateFile);

                return result;
            } else {
                System.out.println("Không tìm thấy bảng có ID là 'LA0'.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void writeLog(String logFilePath, String logContent) {
        try (FileWriter writer = new FileWriter(logFilePath)) {
            writer.append("ThoiGianChay\n");
            writer.append(logContent).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDateTime(String pattern) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return currentDateTime.format(formatter);
    }

    private void writeDataToCSV(List<String[]> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Tinh,ThoiGian,TenGiai,SoTrungGiai,SoDau,SoCuoi\n");

            for (String[] row : data) {
                writer.append(String.join(",", row)).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
