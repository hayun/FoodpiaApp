package org.foodpia.foodpiaapp.regist;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * params[1] = title
 * params[2] = content
 * params[3] = fmember_id
 * params[4] = providertype
 * params[5] = phone
 * params[6] = latitude(경도)
 * params[7] = longitude(위도)
 * params[8] = myFile(사진)
 */
public class RegistAsync extends AsyncTask<String, Void, String> {
    URL url;
    HttpURLConnection con;
    String charset = "utf-8";
    String boundary = Long.toHexString(System.currentTimeMillis());
    String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    InputStream is; // 유저가 선택한 갤러리 이미지에 대한 스트림
    Context context;
    int code; // 응답받을 코드의 값

    public RegistAsync(Context context, InputStream is) {
        this.context = context;
        this.is = is;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Cache-Control", "max-age=0");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = con.getOutputStream();

            con.connect();

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

            // Send title
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"title\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[1]).append(CRLF).flush();

            // Send content
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"content\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[2]).append(CRLF).flush();

            // Send fmember_id
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"fmember_id\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[3]).append(CRLF).flush();

            // Send providertype
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"providertype_id\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[4]).append(CRLF).flush();

            // Send phone number
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"phone\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[5]).append(CRLF).flush();

            // Send latitude
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"latitude\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[6]).append(CRLF).flush();

            // Send longitude
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"longitude\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[7]).append(CRLF).flush();

            // Send image file.

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"myFile\"; filename=\"" + params[8] + "\"").append(CRLF);
            writer.append("Content-Type: " + HttpURLConnection.guessContentTypeFromName(params[8])).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();

            /*
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"member_id\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(params[8]).append(CRLF).flush();
            */
            // 얻어온 입력스트림의 데이터를 output 스트림에 편승시키자 !
            byte[] buff = new byte[1024 * 4];

            int read = 0;
            while (true) {
                read = is.read(buff); // 배열을 사용하여 한꺼번에 읽어들이는 경우 반환되는 데이터는
                if (read == -1) break;
                output.write(buff, 0, read);
            }
            is.close();
            // Files.copy(binaryFile.toPath(), output); 넌 더이상 못써 !못쓴다구!!!!!!!
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            writer.close();

            code = con.getResponseCode();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (code == 204) {
            Toast.makeText(context, "★ 등록 완료 ★" + code, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "## 등록 실패 ##" + code, Toast.LENGTH_SHORT).show();
        }
    }
}
