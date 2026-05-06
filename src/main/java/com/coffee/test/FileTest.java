package com.coffee.test;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class FileTest {
    public static void main(String[] args) {
        String imageFolder = "c:\\shop\\images";

        File folder = new File(imageFolder);

        if(folder.exists()){
            if(folder.isDirectory()){
                System.out.println("폴더");
                Arrays.stream(Objects.requireNonNull(folder.listFiles(), "널이다 조심해라"))
                        .filter(f-> f.isFile() && f.getName().endsWith(".jpg"))
                        .map(f-> f.getName().substring(0, f.getName().lastIndexOf(".")))
                        .forEach(System.out::println);
            }else{
                System.out.println("파일");
            }
        }else{
            System.out.println("존재하지 않는 디렉토리입니다.");
        }


    }
}
