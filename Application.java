import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Uaena
 * @date 2023/3/14 20:05
 */
public class Application {
    private static final Map<String, String> CONTENT_TYPE_TO_FILE_EXTENSION = new HashMap<>();

    static {
        CONTENT_TYPE_TO_FILE_EXTENSION.put("image/jpeg", ".jpg");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("image/png", ".png");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("image/gif", ".gif");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("audio/mpeg", ".mp3");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("video/mp4", ".mp4");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/vnd.ofd", ".fod");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/octet-stream", ".bin");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/json", ".json");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/xml", ".xml");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/pdf", ".pdf");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/msword", ".doc");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/vnd.ms-excel", ".xls");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/vnd.ms-powerpoint", ".ppt");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");
        CONTENT_TYPE_TO_FILE_EXTENSION.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx");

        // Add more content type to file extension mapping here
    }

//    private static final String domainName = "https://zijizhang.com/";
    /**
     * 存放下载链接的文件绝对路径
     */
    private static final String absolutePath = "C:\\Users\\ldx\\Desktop\\files.txt";
    private static final String savePath = "C:\\Users\\ldx\\Downloads\\files";

    public static void main(String[] args) {
        readTxtFile(absolutePath, savePath);
    }

    private static void readTxtFile(String absolutePath, String savePath) {
        if ("".equals(absolutePath)) {
            System.out.println("绝对路径不能为空");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(absolutePath))) {
            String line;
//            StringBuilder sb = new StringBuilder(domainName);
            while ((line = br.readLine()) != null) {
//                sb.append(line);
                downloadFile(line, savePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String url1, String savePath) {
        StringBuilder fileName = null;
        try {
            URL url = new URL(url1);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // 检查HTTP响应码是否是200
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();

                // 以当前系统时间作为文件名
                fileName = new StringBuilder(String.valueOf(System.currentTimeMillis()));
                // 输出文件信息
                System.out.println("Content-Type = " + contentType);
                System.out.println("Content-Disposition = " + disposition);
                System.out.println("Content-Length = " + contentLength);
                System.out.println("fileName = " + fileName);
                String extension = CONTENT_TYPE_TO_FILE_EXTENSION.get(contentType);
                if (Objects.isNull(extension)) {
                    throw new RuntimeException("所下载的文件格式暂不支持");
                }
                fileName.append(extension);
                // 打开网络连接的输入流
                InputStream inputStream = httpConn.getInputStream();
                // 保存文件的绝对路径
                String saveFilePath = savePath + File.separator + fileName;
                // 打开本地文件输出流
                FileOutputStream outputStream = new FileOutputStream(saveFilePath);
                // 读取数据并写入到本地文件
                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // 关闭连接和流
                outputStream.close();
                inputStream.close();
                System.out.println("文件已下载到: " + saveFilePath);
            } else {
                System.out.println("下载失败. 响应码: " + responseCode);
            }
            httpConn.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
