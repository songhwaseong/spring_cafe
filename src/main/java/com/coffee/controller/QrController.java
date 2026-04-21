package com.coffee.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@Slf4j
@RestController
public class QrController {
    @GetMapping(value = "/qr/generate")
    public ResponseEntity<byte[]> test(@RequestParam String text) throws WriterException {

        // QR 정보
        int width = 100;
        int height = 100;
        String url = "http://192.168.0.227:5173";
        url = text;

        // QR code 이미지로 return
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", bos);

            return ResponseEntity.ok(bos.toByteArray());

        } catch (Exception e) {
            log.error("QR Generate Error : ", e.getMessage());
        }

        return null;
    }
}