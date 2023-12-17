package com.example.dtwh.Scheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XSLACrawler {

    public static void main(String[] args) {
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

                        // Lấy giá trị của cột số cuối và lưu vào biến soCuoi
                        String soCuoi = soCuoiElement.html().replace(",", ";");

                        Elements soTrungGiaiElements = cols.get(1).select("p, em");
                        for (Element soTrungGiaiElement : soTrungGiaiElements) {
                            String soTrungGiaiText = soTrungGiaiElement.text().trim();
                            String[] soTrungGiaiArr = soTrungGiaiText.split("\\s+");

                            // Nếu có nhiều số, xuống hàng và lặp lại các cột khác
                            for (String so : soTrungGiaiArr) {
                                // Thêm số 0 vào trước số nếu số bắt đầu từ số khác 0
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

                // Tạo tên file CSV với ngày và giờ hiện tại
                String fileName = "D:\\dtwh\\DTWH\\data_csv\\" + getCurrentDateTime("yyyyMMdd") + "_xsktLA_data.csv";
                String dateFile = "D:\\dtwh\\DTWH\\date_csv\\"+ getCurrentDateTime("yyyyMMdd") + "_date.csv";
                writeDataToCSV(data, fileName);
                writeLog(dateFile, getCurrentDateTime("yyyy/MM/dd"));
                System.out.println("Dữ liệu đã được lưu vào file " + fileName);
            } else {
                System.out.println("Không tìm thấy bảng có ID là 'LA0'.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void writeLog(String logFilePath, String logContent) {
        try (FileWriter writer = new FileWriter(logFilePath)) {
            writer.append("ThoiGianChay\n");
            writer.append(logContent).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentDateTime(String pattern) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return currentDateTime.format(formatter);
    }

    private static void writeDataToCSV(List<String[]> data, String filePath) {
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
