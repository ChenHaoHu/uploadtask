package top.hcy.uploadtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.hcy.uploadtask.entity.Task;
import top.hcy.uploadtask.maper.TaskMapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName: TaskController
 * @Author: hcy
 * @Description:
 * @Date: 2019-10-26 01:24
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/task/v1")
public class TaskController {

    @Autowired
    TaskMapper taskMapper;

    //   public static final String UploadPath = "D:\\task\\hcy\\";
    public static final String UploadPath = "/www/uploadtask/uploadfile/";

    @RequestMapping("/add")
    public Object addTask(@RequestParam("author")String author,
                          @RequestParam("end") Date endTime,
                          @RequestParam("desc")String desc,
                          @RequestParam("passwd")String passwd,
                          @RequestParam("name")String name){

        HashMap<String,Object> res = new HashMap<>();
        Task task = new Task();
        task.setTaskName(name);
        task.setAuthor(author);
        task.setDesct(desc);
        task.setPasswd(passwd);
        task.setEndTime(endTime);
        // get the url by the name+author
//        BASE64Encoder  base64Encoder  = new BASE64Encoder();
//        String encode = base64Encoder.encode((name + author).getBytes());
        Task save = taskMapper.save(task);
        if (save==null){
            res.put("msg",1);
            res.put("data",null);
            return res;
        }

        res.put("msg",0);
        res.put("data",task);
        return res;
    }



    @RequestMapping("/data")
    public Object getTaskData(@RequestParam("task")Integer task){
        Optional<Task> byId = taskMapper.findById(task);
        HashMap<String,Object> res = new HashMap<>();
        res.put("msg",0);
        res.put("data",byId);
        return res;
    }


    @RequestMapping("/upload")
    public Object uploadFile(@RequestParam("task")String task,
                             @RequestParam("name")String name,
                             @RequestParam("file")MultipartFile file){

        File f = new File(UploadPath+task);
        HashMap<String,Object> res = new HashMap<>();
        if (f.exists()){
        }else{
            System.out.println(f.getAbsolutePath());
            boolean mkdirs = f.mkdirs();
            if (mkdirs == false){
                res.put("msg",1);
                res.put("data","dir build error");
                return res;
            }
        }
        String uploadfileName = file.getOriginalFilename();
        String[] split = uploadfileName.split("\\.");
        if (split.length != 0){
            uploadfileName = name+"."+split[split.length-1];
        }
        System.out.println(uploadfileName);
        File saveFile = new File(UploadPath+task+File.separator+uploadfileName);
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res.put("msg",0);
        res.put("data","upload ok");
        return res;
    }


    @RequestMapping("/zip")
    public Object zipFiles1(@RequestParam("task")Integer taskid){
        long l = System.currentTimeMillis();
        String task = "";
        Optional<Task> t = taskMapper.findById(taskid);
        if (!t.isPresent()){
            task = taskid+"_"+ l;
        }else{
            System.out.println( t.get().getTaskName());;
            task = t.get().getTaskName()+"_"+ l;
        }

        File zipFile = new File(UploadPath+task+".zip");
        ZipOutputStream zipOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            File path = new File(UploadPath+taskid);
            if (path.isDirectory()){
                File[] list = path.listFiles();

                for (int i = 0; i < list.length; i++) {
                    zipOutputStream.putNextEntry(new ZipEntry(list[i].getName()));
                    fileInputStream  = new FileInputStream(list[i]);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (zipOutputStream != null){
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ResponseEntity<InputStreamResource> body = null;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(zipFile.getName(), "utf-8")));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            body = ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(zipFile.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(new FileInputStream(zipFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }


    @RequestMapping("/zip2")
    public Object zipFiles2(@RequestParam("task")Integer taskid, HttpServletResponse response){
        long l = System.currentTimeMillis();
        String task = "";
        Optional<Task> t = taskMapper.findById(taskid);
        if (!t.isPresent()){
            task = taskid+"_"+ l;
        }else{
            System.out.println( t.get().getTaskName());;
            task = t.get().getTaskName()+"_"+ l;
        }

        ServletOutputStream outputStream =null;
        File zipFile = new File(UploadPath+task +".zip");
        response.setContentType("application/force-download");// 设置强制下载不打开            
        try {
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(zipFile.getName(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ZipOutputStream zipOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            outputStream = response.getOutputStream();
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            File path = new File(UploadPath+taskid);
            if (path.isDirectory()){
                File[] list = path.listFiles();
                for (int i = 0; i < list.length; i++) {
                    zipOutputStream.putNextEntry(new ZipEntry(list[i].getName()));
                    fileInputStream  = new FileInputStream(list[i]);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                }
            }
            FileInputStream inputStream =  new FileInputStream(zipFile);
            StreamUtils.copy(inputStream,outputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (zipOutputStream != null){
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



    @RequestMapping("/download")
    public Object downloadFile(@RequestParam("file")String file){

        byte[] decode = Base64.getDecoder().decode(file.getBytes());
        file = new String(decode);

        ResponseEntity<InputStreamResource> body = null;
        File file1 = new File(file);
        if (!file1.exists()){
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file1.getName(), "utf-8")));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            body = ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file1.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(new FileInputStream(file1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }


}
