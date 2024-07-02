//package com.demo.eventify.image;
//
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Service
//public class ImageService {
//
//
//    @Autowired
//    private ImageRepository imageDataRepository;
//
//    public ImageUploadResponse uploadImage(MultipartFile file) throws IOException {
//
//        imageDataRepository.save(ImageEntity.builder()
//                .name(file.getOriginalFilename())
//                .type(file.getContentType())
//                .imageData(ImageUtil.compressImage(file.getBytes())).build());
//
//        return new ImageUploadResponse("Image uploaded successfully: " +
//                file.getOriginalFilename());
//
//    }
//
//    @Transactional
//    public ImageEntity getInfoByImageByName(String name) {
//        Optional<ImageEntity> dbImage = imageDataRepository.findByName(name);
//
//        return ImageEntity.builder()
//                .name(dbImage.get().getName())
//                .type(dbImage.get().getType())
//                .imageData(ImageUtil.decompressImage(dbImage.get().getImageData())).build();
//
//    }
//
//    @Transactional
//    public byte[] getImage(String name) {
//        Optional<ImageEntity> dbImage = imageDataRepository.findByName(name);
//        byte[] image = ImageUtil.decompressImage(dbImage.get().getImageData());
//        return image;
//    }
//
//
//}
