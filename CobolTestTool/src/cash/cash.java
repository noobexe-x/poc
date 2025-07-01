package cash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class cash {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0); // 搭建本地服务器

        server.createContext("/run", (exchange -> {
            System.out.println("test");
            String query = exchange.getRequestURI().getQuery();
            String code = "";
            String amount = "";
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        String key = pair.substring(0, idx);
                        String value = pair.substring(idx + 1);
                        value = java.net.URLDecoder.decode(value, "UTF-8");
                        if (key.equals("code")) {
                            code = value;
                        } else if (key.equals("amount")) {
                            amount = value;
                        }
                    }
                }
            }
            String balance = "100"; // 测试参数写死100

            ProcessBuilder pb = new ProcessBuilder("C:\\hal\\JV15\\CobolTestTool\\bank.exe");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (OutputStream os = process.getOutputStream()) {
                String inputStr = code + "\n" + balance + "\n" + amount + "\n"; // 写入三个参数，每行一个
                os.write(inputStr.getBytes("UTF-8"));
                os.flush();
            }

            BufferedReader cobolOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = cobolOutput.readLine()) != null) {
                output.append(line).append("\n");
            }
            

            //System.out.println("exe输出:");
            //System.out.println(output.toString());

            byte[] response = output.toString().getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }));

        // 这两句必须放在这里，且只调用一次
        server.setExecutor(null); // 使用默认线程池
        server.start();           // 启动服务器
        System.out.println("服务器启动，访问：http://localhost:5000/run?code=1&amount=100");
    }
}
