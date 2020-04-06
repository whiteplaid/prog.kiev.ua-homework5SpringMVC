package ua.kiev.prog;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/")
public class MyController {

    private Map<Long, byte[]> photos = new HashMap<Long, byte[]>();

    @RequestMapping("/")
    public String onIndex() {
        return "index";
    }

    @RequestMapping(value = "/all_photos", method = RequestMethod.POST)
    public String allPhotos (Model model) {
        Set<Long> keys = photos.keySet();
        model.addAttribute("photo_ids",keys);
        return "all_photos";
    }
    @RequestMapping(value = "/delete_checked", method = RequestMethod.POST)
    public String deleteChecked(@RequestParam(value = "checked[]", required = false) Long[] checked){
        if (checked != null)
            for (Long id:checked) photos.remove(id);
        return "all_photos";
    }
    @RequestMapping(value = "/zip")
    public String zipFile (HttpServletResponse response) throws IOException {
        Set<Long> photo = photos.keySet();
        List<String> fileNames = new ArrayList<>();
        File f= new File("c:\\Photos\\photos.zip");
        Path path = Paths.get(f.getPath());
        for (Long id:photo) fileNames.add(id + ".png");
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(f));
            for (String fileName:fileNames) {
                File file = new File("c:\\Photos\\" + fileName);
                String[] id = fileName.split(".png");
                String idResult = "";
                for (String name: id) idResult+=name;
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(photos.get(Long.parseLong(idResult)));
                fos.flush();
                fos.close();
                ZipEntry ze = new ZipEntry(file.getName());
                zip.putNextEntry(ze);
                zip.write(photos.get(Long.parseLong(idResult)));
                zip.closeEntry();
            }
            zip.close();
            response.setContentType("application/zip");
            response.addHeader("Content-Disposition", "attachment; filename=photos.zip");
            Files.copy(path,response.getOutputStream());
            response.getOutputStream().flush();

        return "index";
    }
    @RequestMapping(value = "/add_photo", method = RequestMethod.POST)
    public String onAddPhoto(Model model, @RequestParam MultipartFile photo) {
        if (photo.isEmpty())
            throw new PhotoErrorException();
        try {
            long id = System.currentTimeMillis();
            photos.put(id, photo.getBytes());

            model.addAttribute("photo_id", id);
            return "result";
        } catch (IOException e) {
            throw new PhotoErrorException();
        }
    }

    @RequestMapping("/photo/{photo_id}")
    public ResponseEntity<byte[]> onPhoto(@PathVariable("photo_id") long id) {
        return photoById(id);
    }

    @RequestMapping(value = "/view", method = RequestMethod.POST)
    public ResponseEntity<byte[]> onView(@RequestParam("photo_id") long id) {
        return photoById(id);
    }

    @RequestMapping("/delete/{photo_id}")
    public String onDelete(@PathVariable("photo_id") long id) {
        if (photos.remove(id) == null)
            throw new PhotoNotFoundException();
        else
            return "index";
    }

    private ResponseEntity<byte[]> photoById(long id) {
        byte[] bytes = photos.get(id);
        if (bytes == null)
            throw new PhotoNotFoundException();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
