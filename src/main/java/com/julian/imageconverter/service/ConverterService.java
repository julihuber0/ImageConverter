package com.julian.imageconverter.service;

import com.julian.imageconverter.exceptions.InvalidInputPathException;
import com.julian.imageconverter.exceptions.InvalidOutputPathException;
import com.julian.imageconverter.exceptions.NoSuchProgramException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConverterService {

    private final String SEPARATOR = FileSystems.getDefault().getSeparator();

    private final List<Process> processes = new ArrayList<>();

    public synchronized void convert(String inputPath, String outputPath, String inputFormat, String outputFormat, int compressionFactor) throws InvalidInputPathException, InvalidOutputPathException, NoSuchProgramException {
        ProcessBuilder processBuilder = new ProcessBuilder("magick", "mogrify", "-format", outputFormat, "-path", outputPath, "-auto-orient", "-quality", String.valueOf(compressionFactor), inputPath + SEPARATOR + "*." + inputFormat);
        processBuilder.redirectErrorStream(true);
        if (!Files.exists(Path.of(inputPath))) {
            throw new InvalidInputPathException("Invalid input path: " + inputPath);
        }
        if (!Files.exists(Path.of(outputPath))) {
            throw new InvalidOutputPathException("Invalid output path: " + outputPath);
        }
        try {
            Process process = processBuilder.start();
            processes.add(process);
            process.waitFor();
            processes.remove(process);
        } catch (IOException e) {
            throw new NoSuchProgramException("ImageMagick is not installed");
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public void stopAllProcesses() {
        for (Process process : processes) {
            process.destroyForcibly();
        }
        processes.clear();
    }
}
