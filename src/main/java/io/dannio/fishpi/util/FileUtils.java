package io.dannio.fishpi.util;

import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.ProgressListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.util.Objects;

public class FileUtils {

    @SneakyThrows
    public static File downloadFromTelegram(String fileUrl, String fileName) {
        Request request = new Request.Builder().url(fileUrl).build();
        OkHttpClient okHttpClient = new OkHttpClient();
        final File download = new File(fileName);
        mkdirIfNotExists(download.getParentFile());
        final BufferedSink buffer = Okio.buffer(Okio.sink(download));
        buffer.writeAll(Objects.requireNonNull(okHttpClient.newCall(request).execute().body()).source());
        buffer.close();
        return download;
    }


    @SneakyThrows
    public static void convertByFfmpeg(String input, String output) {
        mkdirIfNotExists(new File(output).getParentFile());
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(input)
                .addOutput(output)
                .done();
        new FFmpegExecutor().createJob(builder).run();
    }


    public static void mkdirIfNotExists(File file) {
        if (!(file.exists() || file.mkdirs())) {
            throw new RuntimeException("Cannot create folder: " + file.getAbsolutePath());
        }
    }
}
