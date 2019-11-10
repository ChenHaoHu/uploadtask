package top.hcy.uploadtask.controller;


import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import top.hcy.uploadtask.entity.Task;
import top.hcy.uploadtask.maper.TaskMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Controller
public class PageController {


//   public static final String UploadPath = "D:\\task\\hcy\\";
    public static final String UploadPath = "/www/uploadtask/uploadfile/";
    String uri = "/task";

    @Autowired
    TaskMapper taskMapper;

    @RequestMapping("/{taskid}")
    public String index(@PathVariable("taskid") String id,Model model){
        int taskId = 0;
        try {
          taskId = Integer.valueOf(id);
        }catch (Exception e){
            model.addAttribute("err","您携带的任务号有错");
            return "error";
        }
        Optional<Task> taskOp = taskMapper.findById(taskId);
        if (taskOp.isPresent()){
            Task task = taskOp.get();
            int i = new Date().compareTo(task.getEndTime());
            if ( i == -1){
                model.addAttribute("task",task.getTaskId());
                model.addAttribute("title",task.getTaskName());
                model.addAttribute("author",task.getAuthor());
                model.addAttribute("desc",task.getDesct());
                model.addAttribute("endtime",task.getEndTime().toString().substring(0,16));
                model.addAttribute("createtime",task.getCreateTime());
            }else{
                model.addAttribute("err","您携带的任务已结束");
                return "error";
            }

        }else{
            model.addAttribute("err","您携带的任务不存在");
            return "error";
        }
        return "index";
    }


    @RequestMapping("/upload")
    public String uploadFile(@RequestParam("task")String task,
                             @RequestParam("name")String name,
                             @RequestParam("file") MultipartFile file,
                             Model model){

        File f = new File(UploadPath+task);
        if (f.exists()){
        }else{
            System.out.println(f.getAbsolutePath());
            boolean mkdirs = f.mkdirs();
            if (mkdirs == false){
                model.addAttribute("err","内部权限错误，请联系管理员");
                return "error";
            }
        }
        String uploadfileName = file.getOriginalFilename();
        String[] split = uploadfileName.split("\\.");
        if (split.length != 0){
            uploadfileName = name+"."+split[split.length-1];
        }
        File saveFile = new File(UploadPath+task+File.separator+uploadfileName);
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("err","上传成功");
        return "error";
    }


    @RequestMapping("task/add")
    public String add(){
        return "add";
    }

    @RequestMapping("/add")
    public String addTask(@RequestParam("author")String author,
                          @RequestParam("time") int time,
                          @RequestParam("desc")String desc,
                          @RequestParam("passwd")String passwd,
                          @RequestParam("name")String name,Model model){

        HashMap<String,Object> res = new HashMap<>();
        Task task = new Task();
        task.setTaskName(name);
        task.setAuthor(author);
        task.setDesct(desc);
        task.setPasswd(passwd);
        Date date  = new Date();
        int hours = date.getHours()+time;
        date.setHours(hours);
        task.setEndTime(date);
        Task save = taskMapper.save(task);
        if (save==null){
            model.addAttribute("err","创建失败");
            return "error";
        }

        model.addAttribute("err","创建成功，请点击下方访问查看");
        model.addAttribute("url",uri+"/"+save.getTaskId());
        return "error";
    }

    @RequestMapping("task/data")
    public String taskdata(HttpSession httpSession, Model model){
        Object taskOb = httpSession.getAttribute("task");
        int taskId;
        if (taskOb == null){
            model.addAttribute("err","您没有权限");
            return "error";
        }else{
            taskId = (int)taskOb;
        }

        Optional<Task> taskOp = taskMapper.findById(taskId);
        if (taskOp.isPresent()) {
            Task task = taskOp.get();
            model.addAttribute("name",task.getTaskName()+" -- "+task.getAuthor()+" -- "+task.getCreateTime());
            model.addAttribute("desc",task.getDesct());
            model.addAttribute("zip",uri+"/task/v1/zip?task="+task.getTaskId());
            File path = new File(UploadPath+task.getTaskId());
            if (path.isDirectory()){
                List<HashMap<String,String>> list = new ArrayList<>();
                HashMap<String,String> map = null;
                File[] files = path.listFiles();
                for (int i = 0; i < files.length; i++) {
                    map = new HashMap<>();
                    String[] split = files[i].getName().split("\\.");
                    map.put("num",(i+1)+"");
                    map.put("filename",split[0]);
                    Instant timestamp = Instant.ofEpochMilli(files[i].lastModified());
                    ZonedDateTime losAngelesTime = timestamp.atZone(ZoneId.of("Asia/Shanghai"));
                    map.put("filetime",losAngelesTime.toLocalDate().toString());
                    map.put("filetype",split[1]);
                    String absolutePath = files[i].getAbsolutePath();
                    byte[] encode = Base64.getEncoder().encode(absolutePath.getBytes());

                    map.put("url1",uri+"/task/v1/download?file="+new String(encode));
                    map.put("url2",uri+"/task/v1/delete?file="+files[i].getAbsolutePath());
                    list.add(map);
                }
                model.addAttribute("list",list);

            }
        }
        return "data";
    }


    @RequestMapping("/login")
    @PostMapping
    public String loginIn(Model model , HttpServletRequest request, HttpSession httpSession){
        String user = request.getParameter("name");
        String passwd = request.getParameter("passwd");
        List<Task> task = taskMapper.findTaskByAuthorAndPasswd(user, passwd);
        if (task.size() == 0){
            httpSession.setAttribute("login","true");
            return  "redirect:"+uri+"/admin";
        }
        httpSession.setAttribute("task",task.get(0).getTaskId());
        return  "redirect:"+uri+"/task/data";
    }


    @RequestMapping("admin")
    public  String admin(){
        return "admin";
    }
}
