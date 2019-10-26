package top.hcy.uploadtask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.jvm.hotspot.runtime.Bytes;
import sun.misc.BASE64Encoder;
import top.hcy.uploadtask.entity.Task;
import top.hcy.uploadtask.maper.TaskMapper;

import java.io.*;
import java.util.HashMap;

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

    static final String UploadPath = "/Users/hcy/";

    @RequestMapping("/add")
    public Object addTask(@RequestParam("author")String author,
                          @RequestParam("name")String name){

        HashMap<String,Object> res = new HashMap<>();
        Task task = new Task();
        task.setTaskName(name);
        task.setAuthor(author);
        // get the url by the name+author
        BASE64Encoder  base64Encoder  = new BASE64Encoder();
        String encode = base64Encoder.encode((name + author).getBytes());
        System.out.println(encode);
        task.setTaskUrl(encode);
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


    @RequestMapping("/upload")
    public Object uploadFile(@RequestParam("task")String task,
                             @RequestParam("stuid")String stuid,
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
        System.out.println(split.length);
        if (split.length != 0){
            uploadfileName = stuid+"."+split[split.length-1];
        }
        System.out.println(uploadfileName);
        File saveFile = new File(UploadPath+task+File.separator+uploadfileName);
        InputStream inputStream = null;
        BufferedInputStream bi = null;
        FileOutputStream bo = null;

        try {
            inputStream = file.getInputStream();
            bi = new BufferedInputStream((inputStream));
            bo = new FileOutputStream(saveFile);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = bi.read(bytes))!=-1){
                System.out.println(len);
                bo.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            res.put("msg",1);
            res.put("data","file save error");
            return res;
        }finally {

            try {
                if (inputStream!=null){
                    inputStream.close();
                }
                if (bi !=null){
                    bi.close();
                }
                if (bo !=null){
                    bo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        res.put("msg",0);
        res.put("data",saveFile.getAbsolutePath());
        return res;
    }

}
