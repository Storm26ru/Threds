package main;

import array.ArrayUser;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    static public void main(String[] args){
        try (Scanner scanner = new Scanner(System.in);
             //
             ExecutorService executorService = Executors.newFixedThreadPool(4)) {
                ArrayUser arrayUser = new ArrayUser(scanner);
                System.out.println("Задание 1");
                Future<Integer>min = executorService.submit(arrayUser::min);
                Future<Integer>max = executorService.submit(arrayUser::max);
                System.out.println("min: "+ min.get());
                System.out.println("max: "+ max.get());
                System.out.println("Задание 2");
                Future<Integer> sum =executorService.submit(arrayUser::sum);
                Future<BigDecimal> average = executorService.submit(arrayUser::average);
                System.out.println("sum: "+sum.get());
                System.out.println("average: "+average.get());
                System.out.println("Задание 3");
                System.out.println("Введите путь к файлу: ");
                ArrayUser arrayUser1 = new ArrayUser(scanner,scanner.nextLine());
                Future<Integer>oddThread = executorService.submit(()->fileSeparator(arrayUser1.getArray(),false));
                Future<Integer>evenThread = executorService.submit(()->fileSeparator(arrayUser1.getArray(),true));
                System.out.println("Колличество четных элементов: "+evenThread.get());
                System.out.println("Колличество нечетных элементов: "+oddThread.get());
                System.out.println("Задание 4");
                System.out.println("Введите путь к файлу: ");
                Path path = inputPath(scanner,scanner.nextLine());
                System.out.println("Введите слово для поиска: ");
                String word;
               while ((word = scanner.nextLine()).isEmpty()){
                    System.out.println("Слово не может быть пустым или null.");
                    System.out.println("Введите слово для поиска: ");
                }
               String searchWord = word;
                Future<SearchResult> future = executorService.submit(()->searchInFile(path,searchWord));
                SearchResult result = future.get();
                if(result.found()) System.out.println("Слово "+searchWord+" найдено на строках\n"+result.lines());
                else System.out.println("Слово "+searchWord+" не найдено в файле "+path);

        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

    }
    static int fileSeparator (int [] array, boolean even ){
        StringBuilder stringBuilder = new StringBuilder();
        String filePath = even ? "even.txt": "odd.txt";
        int count = 0;
        if(array==null || array.length==0){
            System.out.println("Массив пустой или null.");
            return 0;
        }
        for (int a:array){
            if(a%2==0 && even) {
                stringBuilder.append(a).append(" ");
                count++;
            }
            else if(a%2!=0 && !even){
                stringBuilder.append(a).append(" ");
                count++;
            }
        }
        if(stringBuilder.isEmpty()) {
            System.out.println("Массив не содержит "+ (even ? "четных ": "нечетных ")+" чисел.");
            return 0;
        }
        while (true){
            try(FileWriter writer = new FileWriter(filePath)){
                writer.write(stringBuilder.toString().trim());
                System.out.println("Данные успешно записаны в файл "+filePath);
                return count;
            }catch (IOException e){
                System.out.println("Ошибка при работе с файлом: "+filePath+" " + e.getMessage());
            }
        }
    }
    static Path inputPath(Scanner scanner, String strPath){
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
            strPath=scanner.nextLine();
        }
    }
    static SearchResult searchInFile(Path path,String word){
        boolean found = false;
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line=bufferedReader.readLine())!=null){
                lineNumber++;
                if(line.toLowerCase().contains(word.toLowerCase())){
                    found = true;
                    stringBuilder.append("Строка ").append(lineNumber).append(": ").append(line).append("\n");
                }
            }

        }catch (IOException e){
            System.out.println("Ошибка при чтении файла "+e.getMessage());
        }
        return new SearchResult(found,stringBuilder.toString());
    }
}
record SearchResult(boolean found,String lines){}
