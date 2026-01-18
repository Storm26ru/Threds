package array;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ArrayUser {
    private Scanner scanner;
    private final int[] array;

    public ArrayUser(Scanner scanner){
        this.scanner = scanner;
        this.array = inputArray();
    }
    public ArrayUser(Scanner scanner,String strPath){
        this.scanner = scanner;
        this.array =loadingArrays(strPath);
        if(array.length==0) throw  new RuntimeException("Файл не содержит данных");
    }

    public int[] getArray() {
        return array;
    }

    private int [] inputArray(){
        System.out.println("Введите элементы массива через запятую:");
        String strArray;
        while (true) {
            try {
                if (!(strArray = scanner.nextLine()).isEmpty()) {
                    return Arrays.stream(strArray.trim().split(",")).map(String::trim)
                            .mapToInt(Integer::parseInt).toArray();
                }else System.out.println("Ввод не должен быть пустым. Введите элементы еще раз:");
            }catch (NumberFormatException e){
                System.out.println("Не поддерживаемый формат вводимых данных. "+e.getMessage());
                System.out.println("Введите элементы массива через запятую:");
            }
        }
    }
    public int max(){
        return Arrays.stream(array).max().orElseThrow(()->new NoSuchElementException("Массив пустой"));
    }
    public int min(){
        return Arrays.stream(array).min().orElseThrow(()->new NoSuchElementException("Массив пустой"));
    }
    public int sum(){
        return Arrays.stream(array).sum();
    }
    public BigDecimal average(){
        return BigDecimal.valueOf(this.sum()).divide(BigDecimal.valueOf(this.array.length),3, RoundingMode.HALF_UP);
    }
    private Path inputPath(String strPath){
        Path path;
        while (true){
            if(strPath==null|| strPath.isEmpty()) {
                System.out.println("Путь к файлу не может быть пустым или null. Введите путь к файлу:");
            }else {
                try {
                    path = Paths.get(strPath).toAbsolutePath().normalize();
                    if (!Files.exists(path) || !Files.isReadable(path)) {
                        System.out.printf("Файл %s не найден или не читается.\nВведите путь к файлу: ", path);
                    } else return path;
                } catch (InvalidPathException e) {
                    System.out.println("Недопустимый символ в пути к файлу.\nВведите путь к файлу: ");
                }
            }
            strPath=this.scanner.nextLine();
        }
    }
    private int [] loadingArrays(String strPath){
        List<Integer>array = new ArrayList<>();
        Path path = inputPath(strPath);
        try(BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line= bufferedReader.readLine())!=null) {
                try {
                    array.addAll(Arrays.stream(line.trim().split("[.,;:\\s]+")).map(Integer::parseInt)
                            .toList());
                }catch (NumberFormatException e){
                    System.out.println("Не поддерживаемый формат строки в файле "+path+" "+e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла "+e.getMessage());
        }
        return array.stream().mapToInt(Integer::intValue).toArray();
    }



}
