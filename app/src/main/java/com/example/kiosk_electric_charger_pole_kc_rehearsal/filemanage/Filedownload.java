package com.example.kiosk_electric_charger_pole_kc_rehearsal.filemanage;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.ResponseBody;

public class Filedownload {


    public static boolean unzip(String zipFilePath, String destDir) {
        String apk_name = "";

        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();

                if(getExtension(fileName).equals("apk")||getExtension(fileName).equals("APK")){
                    apk_name = fileName;
                }

            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();

            Log.d("apk name ",apk_name);

            // 인스톨 apk
//            installApk(destDir, apk_name, context);



            // 예제3  --- 껏다 켜지나 업데이트가 안됨
//            installPackage2(context,new FileInputStream(new File(destDir + File.separator + apk_name).getPath()));



            return  true;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;

        }

    }

    private static String getExtension(String url) {
        return url.substring(url.lastIndexOf(".") + 1, url.length());
    }

//    private static void installApk(String destDir, String apk_name, Context context){
//        File file = new File(destDir+File.separator+apk_name);
//        Uri uri = FileProvider.getUriForFile(context, context.getPackageName(), file);
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        try {
//            context.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }



    public static boolean writeResponseBodyToDisk(ResponseBody body, String path) {

        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path);


            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

//                    Log.d("file download", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                Log.i("file download", "file download: " + fileSizeDownloaded + " of " + fileSize);

                return true;

            }catch (NullPointerException ne){
                Log.d("download null",String.valueOf(ne));
                return false;
            } catch (IOException e) {
                Log.d("download err1", String.valueOf(e));
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d("download err2", String.valueOf(e));
            return false;
        }


    }








}
